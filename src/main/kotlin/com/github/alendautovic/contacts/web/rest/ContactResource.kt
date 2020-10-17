package com.github.alendautovic.contacts.web.rest

import com.github.alendautovic.contacts.domain.Contact
import com.github.alendautovic.contacts.service.ContactService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import javax.validation.Valid

/**
 * REST controller for managing [Contact]
 */
@RestController
@RequestMapping("/api")
class ContactResource(
        private val contactService: ContactService
) {

    /**
     * `POST  /contacts` : Create a new contact.
     *
     * @param contact the [Contact] to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new contact, or with status `400 (Bad Request)` if the contact has already an ID.
     */
    @PostMapping("/contacts")
    fun createContact(@Valid @RequestBody contact: Contact): ResponseEntity<Contact> {
        if (contact.id != null) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A new contact cannot already have an ID")
        }
        val result = contactService.save(contact)
        return ResponseEntity(result, HttpStatus.CREATED)
    }

    /**
     * `GET  /contacts` : find contacts - all or filtered by postal code, depending on request parameter
     *
     * @param postalCode Request parameter representing postal code to be filtered on. Not required
     * @return the HTTP response with status `200 (OK)` and the list of contacts in body.
     */
    @GetMapping("/contacts")
    fun findContacts(@RequestParam(required = false) postalCode: String?): List<Contact> {
        if (postalCode == null) {
            return contactService.findAll()
        }
        return contactService.findAllByPostalCode(postalCode)
    }
}