package com.principate.midas.security
package algebras

trait Crypt[F[_]]:

  def hash(password: String): F[String]

end Crypt
