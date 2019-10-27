[![Download](https://api.bintray.com/packages/falkoschumann/maven/muspellheim-commons/images/download.svg)](https://bintray.com/falkoschumann/maven/muspellheim-commons)
[![Build Status](https://travis-ci.org/falkoschumann/java-muspellheim-commons.png?branch=master)](https://travis-ci.org/falkoschumann/java-muspellheim-commons)

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

    implementation 'de.muspellheim:muspellheim-commons:1.2.0'

Or if you use PostgreSQL also:

    implementation 'de.muspellheim:muspellheim-commons:1.2.0'
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
        <version>1.2.0</version>
    </dependency>

Or if you use PostgreSQL also:

    <dependency>
        <groupId>de.muspellheim</groupId>
        <artifactId>muspellheim-commons</artifactId>
        <version>1.2.0</version>
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <version>42.2.8</version>
    </dependency>

## Usage

See [JavaDoc](https://falkoschumann.github.io/java-muspellheim-commons/).
