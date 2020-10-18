package com.github.alendautovic.contacts.domain

import java.io.Serializable
import java.time.LocalDate
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
data class Contact(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long? = null,
        @get:NotNull
        @Column(nullable = false)
        var fullName: String? = null,
        var dateOfBirth: LocalDate? = null,
        @Embedded
        var address: Address? = null
) : Serializable