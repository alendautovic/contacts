package com.github.alendautovic.contacts.domain

import java.io.Serializable
import java.time.LocalDate
import javax.persistence.*

@Entity
data class Contact(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long? = null,
        var fullName: String? = null,
        var dateOfBirth: LocalDate? = null,
        @Embedded
        var address: Address? = null
): Serializable