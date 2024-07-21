package com.principate.midas.security
package instances

import sttp.tapir.Codec
import sttp.tapir.Codec.PlainCodec
import sttp.tapir.Codec.uuid

import model.UserId

object validators {

  given PlainCodec[UserId] = UserId.derive[PlainCodec]

}
