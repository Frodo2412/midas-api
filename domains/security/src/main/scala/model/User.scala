package com.principate.midas.security
package model

import model.UserId

final case class User(
    id: UserId,
    email: Email,
    firstName: FirstName,
    lastName: LastName
)
