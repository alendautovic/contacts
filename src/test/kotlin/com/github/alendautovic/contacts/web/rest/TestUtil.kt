@file:JvmName("TestUtil")

package com.github.alendautovic.contacts.web.rest

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import java.io.IOException

private val mapper = createObjectMapper()

private fun createObjectMapper() =
        ObjectMapper().apply {
            configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false)
            setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
            registerModule(JavaTimeModule())
        }

/**
 * Convert an object to JSON byte array.
 *
 * @param object the object to convert.
 * @return the JSON byte array.
 * @throws IOException
 */
@Throws(IOException::class)
fun convertObjectToJsonBytes(`object`: Any): ByteArray = mapper.writeValueAsBytes(`object`)