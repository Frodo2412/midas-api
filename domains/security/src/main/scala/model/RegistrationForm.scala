package com.principate.midas.security
package model

final case class RegistrationForm(
    email: Email,
    firstName: FirstName,
    lastName: LastName,
    password: Password
)
