package com.principate.midas.security
package programs

import cats.Monad
import cats.syntax.all.*

import algebras.Users.CreateUserError
import algebras.{Crypt, Users}
import models.{RegistrationForm, User}

final class CreateUser[F[_]: Monad](users: Users[F], crypt: Crypt[F]):

  def apply(form: RegistrationForm): F[Either[CreateUserError, User]] =
    for
      passwordHash <- crypt.hash(form.password)
      user         <- users.create(
                        form.email,
                        form.firstName,
                        form.lastName,
                        passwordHash
                      )
    yield user

end CreateUser
