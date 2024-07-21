package com.principate.midas.security
package instances

import skunk.Codec
import skunk.codec.AllCodecs

import model.UserId

object codecs extends AllCodecs:

  val userId: Codec[UserId] = uuid.imap(UserId.apply)(_.value)

end codecs
