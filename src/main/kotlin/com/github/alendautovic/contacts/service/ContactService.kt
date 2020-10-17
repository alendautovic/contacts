package com.github.alendautovic.contacts.service

import com.github.alendautovic.contacts.domain.Contact
import com.github.alendautovic.contacts.repository.ContactRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ContactService(
        private val contactRepository: ContactRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun save(contact: Contact): Contact {
        log.info("Request to save Contact : {}", contact)
        return contactRepository.save(contact)
    }

    @Transactional(readOnly = true)
    fun findAll(): List<Contact> = contactRepository.findAll()

    @Transactional(readOnly = true)
    fun findAllByPostalCode(postalCode: String) = contactRepository.findAllByPostalCode(postalCode)
}