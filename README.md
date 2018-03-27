# SPSW Serial Port Socket Wrapper

Access the serial device like UART, usb to serial converter or even a TCP bridge to an serial device on a different machine.

## Maven Dependencies

In your library add this dependency.
```
<dependency>
    <groupId>de.ibapl.spsw</groupId>
    <artifactId>de.ibapl.spsw.api</artifactId>
    <version>2.0.0</version>
</dependency>
```

In the final application add this implementation to the runtime only.
```
<dependency>
    <groupId>de.ibapl.spsw</groupId>
    <artifactId>de.ibapl.spsw.jniprovider</artifactId>
    <version>2.0.0</version>
    <scope>runtime</scope>
</dependency>
```

##Code Examples

###OSGi
Just use the OSGi annotation @Reference. 
```
	@Reference
	List<SerialPortSocketFactory> loader;
```

### Spring, JEE (JSR 330)
Use the @Inject annotation.
```
	@Inject
	List<SerialPortSocketFactory> loader;
```

### J2SE with java.util.ServiceLoader

Use the ServiceLoader to load all instances of SerialPortSocketFactory. Usually there should be only one - but prepared for the other.
```
	ServiceLoader<SerialPortSocketFactory> loader = ServiceLoader.load(SerialPortSocketFactory.class);
	Iterator<SerialPortSocketFactory> iterator = loader.iterator();
	if (!iterator.hasNext()) {
		LOG.severe("NO implementation of SerialPortSocketFactory available - add a provider for that to the test dependencies");
	}
	SerialPortSocketFactory serialPortSocketFactory = iterator.next();
	if (iterator.hasNext()) {
		StringBuilder sb = new StringBuilder("More than one implementation of SerialPortSocketFactory available - fix the test dependencies\n");
		iterator = loader.iterator();
		while ( iterator.hasNext()) {
		sb.append(iterator.next().getClass().getCanonicalName()).append("\n");
		}
		LOG.severe(sb.toString());
	}
	serialPortSocket = serialPortSocketFactory.createSerialPortSocket(PORT_NAME);
```

## Testing Hardware

1.  Grap yourself 2 serial devices and an NullModem adapter or cable. OR use one device with properly cross connected lines.
1.  Goto the directory de.ibapl.spsw.jniprovider.
2.  copy src/test/resources/junit-spsw-config.properties.template to src/test/resources/junit-spsw-config.properties
3.  Edit the portnames in src/test/resources/junit-spsw-config.properties. If you have a "cross connected" device use the same name for readPort a writePort.
4.  Run mvn -PBaselineTests test for the Baseline tests. This tests should never fail.
5.  mvn  test to execute all tests - some will fail. Look at the test itself and on the outcome to device whats up.