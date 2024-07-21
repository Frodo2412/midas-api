package com.principate.midas.security
package algebras

import scala.util.control.NoStackTrace

import algebras.Users.CreateUserError
import model.*

trait Users[F[_]]:

  def create(
      email: Email,
      firstName: FirstName,
      lastName: LastName,
      passwordHash: PasswordHash
  ): F[Either[CreateUserError, User]]

  def find(userId: UserId): F[Option[User]]

end Users

object Users:

  enum CreateUserError extends NoStackTrace:
    case EmailAlreadyInUse(email: String)
  end CreateUserError

end Users
