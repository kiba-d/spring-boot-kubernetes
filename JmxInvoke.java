import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class JmxInvoke {

    // Example: java JmxInvoke "service:jmx:rmi:///jndi/rmi://localhost:9010/jmxrmi" "spring-boot-kubernetes:type=Endpoint,name=Health" "health"
    public static void main(String... args) throws Exception {
        if (args.length < 3) {
            throw new IllegalArgumentException("Usage: JmxInvoke url objectName operation");
        }
        System.out.printf("JMX Invoke: url=%s, objectName=%s, operation=%s%n", args[0], args[1], args[2]);
        JMXServiceURL url = new JMXServiceURL(args[0]);
        ObjectName objectName = new ObjectName(args[1]);
        String operation = args[2];
        try (JMXConnector jmxConnector = JMXConnectorFactory.connect(url)) {
            Object result = jmxConnector.getMBeanServerConnection().invoke(
                    objectName, operation, new Object[]{}, new String[]{}
            );
            System.out.printf("Result: %s%n", result);
            if (result.toString().contains("status=UP")) {
                System.exit(0);
            } else {
                System.out.println("Health check failed");
                System.exit(1);
            }
        }
    }

}
