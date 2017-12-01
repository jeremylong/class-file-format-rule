[![Build Status](https://travis-ci.org/jeremylong/bytecode-version-rule.svg?branch=master)](https://travis-ci.org/jeremylong/bytecode-version-rule) [![Apache 2.0 License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.txt)

bytecode-version-rule
================

A maven-enforcer rule that ensures dependencies do not exceed a required bytecode version. In other words, 
if your project must support an older version of Java - this rule will ensure your dependencies are able to 
run in the older JVM.

Usage
-------------
To use this plugin simply add the following plugin to your build/plugins section of the pom.xml.
The valid options for the `supportedJvmByteCodeLevel` is the major version of the class file
as described [here](https://en.wikipedia.org/wiki/Java_class_file#General_layout).

- Java 9 = 53
- Java 8 = 52
- Java 7 = 51

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-enforcer-plugin</artifactId>
    <version>3.0.0-M1</version>
    <dependencies>
        <dependency>
            <groupId>org.owasp.enforcer</groupId>
            <artifactId>bytecode-version-rule</artifactId>
            <version>1.0.0</version>
        </dependency>
    </dependencies>
    <executions>
        <execution>
            <id>enforce-bytecodelevel</id>
            <configuration>
                <rules>
                    <byteCodeRule implementation="org.owasp.maven.enforcer.rule.BytecodeLevelRule">
                        <supportedJvmByteCodeLevel>51</supportedJvmByteCodeLevel>
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