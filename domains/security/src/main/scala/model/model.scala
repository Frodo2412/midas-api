package com.principate.midas.security
package model

import cats.effect.Sync
import cats.syntax.all.*
import io.github.iltotore.iron.:|
import io.github.iltotore.iron.RefinedTypeOps
import io.github.iltotore.iron.constraint.all.*

import java.util.UUID

type UserId = UUID :| Pure
object UserId extends RefinedTypeOps.Transparent[UserId]:

  def random[F[_]: Sync]: F[UserId] =
    Sync[F].delay(UUID.randomUUID()).map(assume)

end UserId

type Email = String :|
  (Match["^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"] DescribedAs
    "Value should be an email")
object Email extends RefinedTypeOps.Transparent[Email]

type FirstName = String :| Alphanumeric
object FirstName extends RefinedTypeOps.Transparent[FirstName]

type LastName = String :| Alphanumeric
object LastName extends RefinedTypeOps.Transparent[LastName]

type Password = String :|
  (Match["^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]+$"] DescribedAs
    "Value should contain at least an upper, a lower and a number")
object Password extends RefinedTypeOps.Transparent[Password]

type PasswordHash = String :| Pure
object PasswordHash extends RefinedTypeOps.Transparent[PasswordHash]
