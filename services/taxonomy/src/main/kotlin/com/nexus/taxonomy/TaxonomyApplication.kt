package com.nexus.taxonomy

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TaxonomyApplication

fun main(args: Array<String>) {
    runApplication<TaxonomyApplication>(*args)
}
