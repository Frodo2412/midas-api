package com.principate.midas.security
package algebras

import com.principate.midas.security.model.UserId

import java.util.UUID

import scala.util.control.NoStackTrace

import algebras.Users.CreateUserError
import models.User

trait Users[F[_]]:

  def create(
      email: String,
      firstName: String,
      lastName: String,
      passwordHash: String
  ): F[Either[CreateUserError, User]]

  def find(userId: UserId): F[Option[User]]

end Users

object Users:

  enum CreateUserError extends NoStackTrace:
    case EmailAlreadyInUse(email: String)
  end CreateUserError

end Users
