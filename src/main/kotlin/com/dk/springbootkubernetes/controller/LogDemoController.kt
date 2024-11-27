package com.dk.springbootkubernetes.controller

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class LogDemoController {
    private val logger = LoggerFactory.getLogger(LogDemoController::class.java)

    @GetMapping("/demo")
    fun logDemo(
        @RequestParam(defaultValue = "test") message: String
    ): Map<String, Any> {
        logger.trace("TRACE level message: Processing demo request with message: {}", message)
        logger.debug("DEBUG level message: Generating response for message: {}", message)
        logger.info("INFO level message: Handled request with message: {}", message)
        logger.warn("WARN level message: This is a warning example for message: {}", message)
        logger.error("ERROR level message: This is an error example for message: {}", message)

        return mapOf(
            "message" to "Log messages generated for: $message",
            "timestamp" to System.currentTimeMillis(),
            "checkLogFile" to "/app/logs/application.log"
        )
    }

    @GetMapping("/simulate-scenario")
    fun simulateScenario(): Map<String, Any> {
        logger.debug("Starting user registration simulation")

        try {
            logger.info("Processing new user registration")
            logger.debug("Validating user data...")

            if (System.currentTimeMillis() % 2 == 0L) {
                logger.warn("Unusual login pattern detected")
                throw IllegalStateException("Simulated validation error")
            }

            logger.info("User registration completed successfully")
            return mapOf(
                "status" to "success",
                "message" to "Scenario completed successfully"
            )

        } catch (e: Exception) {
            logger.error("Error during user registration: ${e.message}", e)
            return mapOf(
                "status" to "error",
                "message" to "Scenario failed with error: ${e.message}"
            )
        }
    }
}