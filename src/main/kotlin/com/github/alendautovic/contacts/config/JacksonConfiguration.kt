package com.github.alendautovic.contacts.config

import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Configuration
class JacksonConfiguration {
    /**
     * Support for Java date and time API.
     *
     * @return the corresponding Jackson module.
     */
    @Bean
    fun javaTimeModule(): JavaTimeModule {
        val dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        val localDateDeserializer = LocalDateDeserializer(dateTimeFormatter)
        val localDateSerializer = LocalDateSerializer(dateTimeFormatter)
        val javaTimeModule = JavaTimeModule()
        javaTimeModule.addDeserializer(LocalDate::class.java, localDateDeserializer)
        javaTimeModule.addSerializer(LocalDate::class.java, localDateSerializer)
        return javaTimeModule
    }

    @Bean
    fun jdk8TimeModule(): Jdk8Module {
        return Jdk8Module()
    }
}