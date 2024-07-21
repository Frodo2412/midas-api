package com.principate.midas.security
package algebras

import model.*

trait Crypt[F[_]]:

  def hash(password: Password): F[PasswordHash]

end Crypt
