package com.dk.springbootkubernetes

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringBootKubernetesApplication

fun main(args: Array<String>) {
    runApplication<SpringBootKubernetesApplication>(*args)
}
