[![Download](https://api.bintray.com/packages/falkoschumann/maven/muspellheim-commons/images/download.svg)](https://bintray.com/falkoschumann/maven/muspellheim-commons)
[![javadoc.io](https://javadoc.io/badge2/de.muspellheim/muspellheim-commons/javadoc.io.svg)](https://javadoc.io/doc/de.muspellheim/muspellheim-commons)
[![Build Status](https://travis-ci.org/falkoschumann/java-muspellheim-commons.png?branch=master)](https://travis-ci.org/falkoschumann/java-muspellheim-commons)
[![Sonar Coverage](https://img.shields.io/sonar/coverage/falkoschumann_java-muspellheim-commons?server=https%3A%2F%2Fsonarcloud.io)](https://sonarcloud.io/dashboard?id=falkoschumann_java-muspellheim-commons)
[![Sonar Quality Gate](https://img.shields.io/sonar/quality_gate/falkoschumann_java-muspellheim-commons?server=https%3A%2F%2Fsonarcloud.io)](https://sonarcloud.io/dashboard?id=falkoschumann_java-muspellheim-commons)

# Muspellheim Commons

Bundles common classes for developing Java apps.

## Installation

Add the dependency to your build.

### Gradle

Add JCenter repository:

    repositories {
        jcenter()
    }

Add dependency:

    implementation 'de.muspellheim:muspellheim-commons:1.7.0'

Or if you use PostgreSQL also:

    implementation 'de.muspellheim:muspellheim-commons:1.7.0'
    runtimeOnly 'org.postgresql:postgresql:42.2.8'

### Maven

Add JCenter repository:

    <repositories>
        <repository>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
            <id>jcenter</id>
            <name>jcenter</name>
            <url>http://jcenter.bintray.com</url>
        </repository>
    </repositories>

Add dependency:

    <dependency>
        <groupId>de.muspellheim</groupId>
        <artifactId>muspellheim-commons</artifactId>
        <version>1.7.0</version>
    </dependency>

Or if you use PostgreSQL also:

    <dependency>
        <groupId>de.muspellheim</groupId>
        <artifactId>muspellheim-commons</artifactId>
        <version>1.7.0</version>
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <version>42.2.8</version>
    </dependency>

## Usage

See [JavaDoc](https://javadoc.io/doc/de.muspellheim/muspellheim-commons).
