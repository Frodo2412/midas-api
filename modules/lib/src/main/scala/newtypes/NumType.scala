package com.principate.midas.lib
package newtypes

trait NumType[A](using Numeric[A]) extends NewType[A]:

  given Numeric[T] = derive[Numeric]

  // TODO: Should I be able to operate with items of type A?
  extension (lhs: T)
    def +(rhs: T): T = Numeric[T].plus(lhs, rhs)
    def -(rhs: T): T = Numeric[T].minus(lhs, rhs)
    def *(rhs: T): T = Numeric[T].times(lhs, rhs)
    def unary_- : T  = Numeric[T].negate(lhs)

    def abs: T = Numeric[T].abs(lhs)

    def sign: T = Numeric[T].sign(lhs)

    def toInt: Int       = Numeric[T].toInt(lhs)
    def toLong: Long     = Numeric[T].toLong(lhs)
    def toFloat: Float   = Numeric[T].toFloat(lhs)
    def toDouble: Double = Numeric[T].toDouble(lhs)
  end extension

end NumType
