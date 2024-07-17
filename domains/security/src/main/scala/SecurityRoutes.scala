package com.principate.midas
package security

import com.principate.midas.lib.newtypes.GenUUID
import com.principate.midas.security.SecurityRoutes.UserNotFound
import com.principate.midas.security.algebras.Users.CreateUserError
import com.principate.midas.security.algebras.Users.CreateUserError.EmailAlreadyInUse
import com.principate.midas.security.algebras.*
import com.principate.midas.security.interpreters.SkunkUsersInterpreter
import com.principate.midas.security.interpreters.TsecCryptInterpreter
import com.principate.midas.security.models.*
import com.principate.midas.security.programs.CreateUser

import cats.MonadThrow
import cats.effect.Resource
import cats.effect.kernel.Sync
import cats.effect.std.Random
import cats.syntax.all.*
import io.circe.generic.auto.*
import scribe.Scribe
import skunk.Session
import sttp.capabilities.fs2.Fs2Streams
import sttp.model.StatusCode
import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.server.ServerEndpoint

import java.util.UUID

final class SecurityRoutes[F[_]: MonadThrow: Scribe] private (
    users: Users[F],
    crypt: Crypt[F]
):

  private val createUser =
    endpoint.post
      .in("users")
      .in(jsonBody[RegistrationForm])
      .out(jsonBody[User])
      .errorOut(
        oneOf[CreateUserError](
          oneOfVariant(
            statusCode(StatusCode.Conflict) and jsonBody[EmailAlreadyInUse]
          )
        )
      )
      .serverLogic(CreateUser[F](users, crypt).apply)

  private val getUser =
    endpoint.get
      .in("users")
      .in(query[UUID]("userId"))
      .out(jsonBody[User])
      .errorOut(statusCode(StatusCode.NotFound) and jsonBody[UserNotFound])
      .serverLogic: id =>
        users.find(id).map(_.toRight(UserNotFound(id)))

  val routes: List[ServerEndpoint[Fs2Streams[F], F]] = List(createUser, getUser)

end SecurityRoutes

object SecurityRoutes:

  def apply[F[_]: Sync: GenUUID: Scribe: Random](
      sessionPool: Resource[F, Session[F]]
  ): SecurityRoutes[F] =
    val users = SkunkUsersInterpreter[F](sessionPool)
    val crypt = TsecCryptInterpreter[F]
    new SecurityRoutes(users, crypt)

  case class UserNotFound(userId: UUID)

end SecurityRoutes
