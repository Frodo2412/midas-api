package com.principate.midas.lib
package newtypes

import monocle.Iso

trait NewType[A]:

  opaque type T = A

  // Wrap and unwrap values
  inline def apply(x: A): T            = x
  extension (x: T) inline def value: A = x

  // Can be used to derive TypeClasses from underlying to actual type
  def derive[F[_]](using ev: F[A]): F[T] = ev
  given [F[_]](using ev: F[A]): F[T]     = derive[F]

  // Iso instance for macro generation
  given Wrapper[A, T] with
    override def iso: Iso[A, T] = Iso.id
  end given

end NewType
