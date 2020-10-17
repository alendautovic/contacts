package com.github.alendautovic.contacts.repository

import com.github.alendautovic.contacts.domain.Contact
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ContactRepository : JpaRepository<Contact, Long> {

    @Query("select c from Contact c where c.address.postalCode = ?1")
    fun findAllByPostalCode(postalCode: String): List<Contact>
}