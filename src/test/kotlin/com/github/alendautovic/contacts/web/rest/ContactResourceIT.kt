package com.github.alendautovic.contacts.web.rest

import com.github.alendautovic.contacts.ContactsApplication
import com.github.alendautovic.contacts.domain.Address
import com.github.alendautovic.contacts.domain.Contact
import com.github.alendautovic.contacts.repository.ContactRepository
import com.github.alendautovic.contacts.service.ContactService
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import org.mockito.MockitoAnnotations
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.Validator
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Integration tests for the [ContactResource] REST controller.
 *
 * @see ContactResource
 */
@SpringBootTest(classes = [ContactsApplication::class])
@AutoConfigureMockMvc
@Extensions(
        ExtendWith(MockitoExtension::class)
)
class ContactResourceIT {

    @Autowired
    private lateinit var contactRepository: ContactRepository

    @Autowired
    private lateinit var contactService: ContactService

    @Autowired
    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter

    @Autowired
    private lateinit var validator: Validator

    private lateinit var restContactMockMvc: MockMvc

    private lateinit var contact: Contact

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        val contactResource = ContactResource(contactService)
        this.restContactMockMvc = MockMvcBuilders.standaloneSetup(contactResource)
                .setMessageConverters(jacksonMessageConverter)
                .setValidator(validator).build()
    }

    @BeforeEach
    fun initTest() {
        contact = createEntity()
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createContact() {
        val databaseSizeBeforeCreate = contactRepository.findAll().size

        // Create the Contact
        restContactMockMvc.perform(
                post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertObjectToJsonBytes(contact))
        ).andExpect(status().isCreated)

        // Validate the Contact in the database
        val contactList = contactRepository.findAll()
        assertThat(contactList).hasSize(databaseSizeBeforeCreate + 1)
        val testContact = contactList[contactList.size - 1]
        assertThat(testContact.fullName).isEqualTo(DEFAULT_FULL_NAME)
        assertThat(testContact.dateOfBirth).isEqualTo(DEFAULT_DATE_OF_BIRTH)
        assertThat(testContact.address).isEqualTo(DEFAULT_ADDRESS)
        assertThat(testContact.address?.city).isEqualTo(DEFAULT_CITY)
        assertThat(testContact.address?.postalCode).isEqualTo(DEFAULT_POSTAL_CODE)
    }

    @Test
    @Transactional
    fun createContactWithExistingId() {
        val databaseSizeBeforeCreate = contactRepository.findAll().size

        // Create the Contact with an existing ID
        contact.id = 1L

        // An entity with an existing ID cannot be created, so this API call must fail
        restContactMockMvc.perform(
                post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertObjectToJsonBytes(contact))
        ).andExpect(status().isBadRequest)

        // Validate the Contact in the database
        val contactList = contactRepository.findAll()
        assertThat(contactList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    fun checkFullNameIsRequired() {
        val databaseSizeBeforeTest = contactRepository.findAll().size
        // set the field null
        contact.fullName = null

        // Create the Contact, which fails.

        restContactMockMvc.perform(
                post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertObjectToJsonBytes(contact))
        ).andExpect(status().isBadRequest)

        val contactList = contactRepository.findAll()
        assertThat(contactList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun findAllContacts() {
        // Initialize the database
        contactRepository.saveAndFlush(contact)

        // Get all the contactList
        restContactMockMvc.perform(get("/api/contacts"))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(contact.id?.toInt())))
                .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
                .andExpect(jsonPath("$.[*].dateOfBirth").value(hasItem(DEFAULT_DATE_OF_BIRTH.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")))))
                .andExpect(jsonPath("$.[*].address.city").value(hasItem(DEFAULT_CITY)))
                .andExpect(jsonPath("$.[*].address.postalCode").value(hasItem(DEFAULT_POSTAL_CODE)))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun findContactsByPostalCode() {
        // Initialize the database
        val defaultContactList: MutableList<Contact> = mutableListOf()
        for (i in 1..3) {
            val defaultContact = createEntity()
            defaultContactList.add(contactRepository.saveAndFlush(defaultContact))
        }

        val contactWithDifferentPostalCode = Contact(fullName = DEFAULT_FULL_NAME, dateOfBirth = DEFAULT_DATE_OF_BIRTH, address = Address(DEFAULT_CITY, OTHER_POSTAL_CODE))
        contactRepository.saveAndFlush(contactWithDifferentPostalCode)

        // Get contactList by postalCode
        restContactMockMvc.perform(get("/api/contacts?postalCode=$DEFAULT_POSTAL_CODE"))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize<Any>(defaultContactList.size)))
                .andExpect(jsonPath("$.[0].id").value(`is`(defaultContactList[0].id?.toInt())))
                .andExpect(jsonPath("$.[0].fullName").value(`is`(DEFAULT_FULL_NAME)))
                .andExpect(jsonPath("$.[0].dateOfBirth").value(`is`(DEFAULT_DATE_OF_BIRTH.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")))))
                .andExpect(jsonPath("$.[0].address.city").value(`is`(DEFAULT_CITY)))
                .andExpect(jsonPath("$.[0].address.postalCode").value(`is`(DEFAULT_POSTAL_CODE)))
                .andExpect(jsonPath("$.[1].id").value(`is`(defaultContactList[1].id?.toInt())))
                .andExpect(jsonPath("$.[1].fullName").value(`is`(DEFAULT_FULL_NAME)))
                .andExpect(jsonPath("$.[1].dateOfBirth").value(`is`(DEFAULT_DATE_OF_BIRTH.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")))))
                .andExpect(jsonPath("$.[1].address.city").value(`is`(DEFAULT_CITY)))
                .andExpect(jsonPath("$.[1].address.postalCode").value(`is`(DEFAULT_POSTAL_CODE)))
                .andExpect(jsonPath("$.[2].id").value(`is`(defaultContactList[2].id?.toInt())))
                .andExpect(jsonPath("$.[2].fullName").value(`is`(DEFAULT_FULL_NAME)))
                .andExpect(jsonPath("$.[2].dateOfBirth").value(`is`(DEFAULT_DATE_OF_BIRTH.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")))))
                .andExpect(jsonPath("$.[2].address.city").value(`is`(DEFAULT_CITY)))
                .andExpect(jsonPath("$.[2].address.postalCode").value(`is`(DEFAULT_POSTAL_CODE)))

        restContactMockMvc.perform(get("/api/contacts?postalCode=$OTHER_POSTAL_CODE"))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize<Any>(1)))
                .andExpect(jsonPath("$.[0].id").value(`is`(contactWithDifferentPostalCode.id?.toInt())))
                .andExpect(jsonPath("$.[0].fullName").value(`is`(contactWithDifferentPostalCode.fullName)))
                .andExpect(jsonPath("$.[0].dateOfBirth").value(`is`(contactWithDifferentPostalCode.dateOfBirth?.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")))))
                .andExpect(jsonPath("$.[0].address.city").value(`is`(contactWithDifferentPostalCode.address?.city)))
                .andExpect(jsonPath("$.[0].address.postalCode").value(`is`(contactWithDifferentPostalCode.address?.postalCode)))

    }

    companion object {

        private const val DEFAULT_FULL_NAME = "James Miller"
        private val DEFAULT_DATE_OF_BIRTH: LocalDate = LocalDate.now().minusYears(25)
        private const val DEFAULT_CITY = "Podgorica"
        private const val DEFAULT_POSTAL_CODE = "81000"
        private const val OTHER_POSTAL_CODE = "82000"

        private val DEFAULT_ADDRESS: Address = Address(DEFAULT_CITY, DEFAULT_POSTAL_CODE)

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity() = Contact(
                fullName = DEFAULT_FULL_NAME,
                dateOfBirth = DEFAULT_DATE_OF_BIRTH,
                address = DEFAULT_ADDRESS
        )
    }
}