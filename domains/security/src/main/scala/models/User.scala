package com.principate.midas.security
package models

import java.util.UUID

final case class User(
    id: UUID,
    email: String,
    firstName: String,
    lastName: String
)
