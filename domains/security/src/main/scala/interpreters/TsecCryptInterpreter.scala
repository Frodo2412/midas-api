package com.principate.midas.security
package interpreters

import cats.effect.Sync
import cats.effect.std.Random
import cats.syntax.all.*
import tsec.passwordhashers.jca.BCrypt

import algebras.Crypt

final class TsecCryptInterpreter[F[_]: Sync: Random] extends Crypt[F]:

  override def hash(password: String): F[String] =
    for hash <- BCrypt.hashpw[F](password)
    yield hash

end TsecCryptInterpreter
