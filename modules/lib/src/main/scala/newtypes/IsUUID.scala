package com.principate.midas.lib
package newtypes

import monocle.Iso

import java.util.UUID

trait IsUUID[A]:

  def iso: Iso[UUID, A]

end IsUUID

object IsUUID:

  def apply[A: IsUUID]: IsUUID[A] = summon[IsUUID[A]]

  given IsUUID[UUID] with
    def iso: Iso[UUID, UUID] = Iso.id
  end given

end IsUUID
