package com.principate.midas.security
package interpreters

import algebras.Crypt
import model.*

import cats.effect.Sync
import cats.effect.std.Random
import cats.syntax.all.*
import tsec.passwordhashers.jca.BCrypt

final class TsecCryptInterpreter[F[_]: Sync: Random] extends Crypt[F]:

  override def hash(password: Password): F[PasswordHash] =
    for hash <- BCrypt.hashpw[F](password)
    yield PasswordHash.assume(hash)

end TsecCryptInterpreter
