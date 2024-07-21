package com.principate.midas.security
package instances

import io.github.iltotore.iron.skunk.*
import skunk.*
import skunk.codec.all.*

import model.*

object codecs:

  val userId: Codec[UserId]             = uuid.refined
  val email: Codec[Email]               = text.refined
  val firstName: Codec[FirstName]       = text.refined
  val lastName: Codec[LastName]         = text.refined
  val passwordHash: Codec[PasswordHash] = text.refined

end codecs
