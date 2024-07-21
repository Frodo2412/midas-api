package com.principate.midas.security
package models

import java.util.UUID

import model.UserId

final case class User(
    id: UserId,
    email: String,
    firstName: String,
    lastName: String
)
