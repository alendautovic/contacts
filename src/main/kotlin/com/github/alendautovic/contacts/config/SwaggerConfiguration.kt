package com.github.alendautovic.contacts.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2


@Configuration
@EnableSwagger2
class SwaggerConfiguration {
    /**
     * Swagger API documentation configuration
     *
     * @return the [Docket] object
     */
    @Bean
    fun api(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.github.alendautovic.contacts.web.rest"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
    }

    private fun apiInfo(): ApiInfo {
        return ApiInfoBuilder()
                .title("Contacts")
                .description("A demo Spring Boot application that can be used to manage contacts using a RESTful API.")
                .termsOfServiceUrl("localhost")
                .version("1.0")
                .build()
    }
}