package com.principate.midas.lib
package newtypes

import cats.effect.Sync
import cats.syntax.all.*

import java.util.UUID

trait GenUUID[F[_]]:

  def make[A: IsUUID]: F[A]

end GenUUID

object GenUUID:
  def apply[F[_]: GenUUID]: GenUUID[F] = summon

  given [F[_]: Sync]: GenUUID[F] with
    def make[A: IsUUID]: F[A] =
      Sync[F].delay(UUID.randomUUID()).map(IsUUID[A].iso.get)
  end given

end GenUUID
