package com.dk.springbootkubernetes

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class springBootKubernetesApplication

fun main(args: Array<String>) {
    runApplication<springBootKubernetesApplication>(*args)
}
