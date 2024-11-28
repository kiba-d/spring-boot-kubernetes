package com.dk.springbootkubernetes

import javax.management.ObjectName
import javax.management.remote.JMXConnectorFactory
import javax.management.remote.JMXServiceURL
import kotlin.system.exitProcess

// Example: java JmxInvoke "service:jmx:rmi:///jndi/rmi://localhost:9010/jmxrmi" "spring-boot-kubernetes:type=Endpoint,name=Health" "health"
fun main(args: Array<String>) {
    require(args.size >= 3) { "Usage: JmxInvoke url objectName operation" }
    System.out.printf("JMX Invoke: url=%s, objectName=%s, operation=%s%n", args[0], args[1], args[2])
    val url = JMXServiceURL(args[0])
    val objectName = ObjectName(args[1])
    val operation = args[2]
    JMXConnectorFactory.connect(url).use { jmxConnector ->
        val result = jmxConnector.mBeanServerConnection.invoke(
            objectName, operation, arrayOf(), arrayOf()
        )
        System.out.printf("Result: %s%n", result)
        if (result.toString().contains("status=UP")) {
            exitProcess(0)
        } else {
            println("Health check failed")
            exitProcess(1)
        }
    }
}