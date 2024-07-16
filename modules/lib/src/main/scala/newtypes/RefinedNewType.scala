package com.principate.midas.lib
package newtypes

import cats.data.Validated
import cats.data.ValidatedNec
import io.github.iltotore.iron.:|
import io.github.iltotore.iron.Constraint
import io.github.iltotore.iron.RefinedTypeOps
import io.github.iltotore.iron.RuntimeConstraint
import io.github.iltotore.iron.cats.*

trait RefinedNewType[A, C](using RuntimeConstraint[A, C])
   extends NewType[A :| C]:

  private object ops extends RefinedTypeOps[A, C, A :| C]

  def assume(x: A): T = apply(ops.assume(x))

  def validate(a: A)(using Constraint[A, C]): Validated[String, T]       =
    ops validated a map apply
  def validateNec(a: A)(using Constraint[A, C]): ValidatedNec[String, T] =
    ops validatedNec a map apply

end RefinedNewType
