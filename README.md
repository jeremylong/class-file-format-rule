# DEPRECATED

This project is no longer maintained. A better, maintained alternative exists: [org.codehaus.mojo:extra-enforcer-rules](https://www.mojohaus.org/extra-enforcer-rules/enforceBytecodeVersion.html).

class-file-format-rule
================

A maven-enforcer rule that ensures dependencies do not exceed the required class file format version. In other words, 
if your project must support an older version of Java - this rule will ensure your dependencies are able to 
run in the older JVM.

Usage
-------------
To use this plugin simply add the following plugin to your build/plugins section of the pom.xml.
The valid options for the `supportedClassFileFormat` is the major version of the class file format
as described [here](https://en.wikipedia.org/wiki/Java_class_file#General_layout).

- Java 9 = 53
- Java 8 = 52
- Java 7 = 51

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-enforcer-plugin</artifactId>
    <version>3.0.0</version>
    <dependencies>
        <dependency>
            <groupId>org.owasp.maven.enforcer</groupId>
            <artifactId>class-file-format-rule</artifactId>
            <version>2.0.0</version>
        </dependency>
    </dependencies>
    <executions>
        <execution>
            <id>enforce-classfileformat</id>
            <configuration>
                <rules>
                    <byteCodeRule implementation="org.owasp.maven.enforcer.rule.ClassFileFormatRule">
                        <supportedClassFileFormat>51</supportedClassFileFormat>
                        <excludeScopeTest>true</excludeScopeTest>
                        <excludeScopeProvided>true</excludeScopeProvided>
                    </byteCodeRule>
                </rules>
            </configuration>
            <goals>
                <goal>enforce</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```