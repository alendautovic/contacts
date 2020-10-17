package com.github.alendautovic.contacts.domain

import java.io.Serializable
import javax.persistence.Embeddable

@Embeddable
data class Address(
        var city: String? = null,
        var postalCode: String? = null
): Serializable