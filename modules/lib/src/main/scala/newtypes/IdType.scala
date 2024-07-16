package com.principate.midas.lib
package newtypes

import cats.Applicative

import java.util.UUID

trait IdType extends NewType[UUID]:

  inline def random[F[_]: Applicative]: F[T] =
    Applicative[F] pure apply(UUID.randomUUID())

  given IsUUID[T] = derive[IsUUID]

end IdType
