package com.principate.midas.security
package models

final case class RegistrationForm(
    email: String,
    firstName: String,
    lastName: String,
    password: String
)
