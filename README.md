[![Bintray](https://img.shields.io/bintray/v/falkoschumann/maven/muspellheim-commons)](https://bintray.com/falkoschumann/maven/muspellheim-commons)
[![Travis (.org)](https://img.shields.io/travis/falkoschumann/java-muspellheim-commons)](https://travis-ci.org/falkoschumann/java-muspellheim-commons)

# Muspellheim Commons

Bundles common classes for develop Java apps.

## Installation

Add the dependency to your build.

### Gradle

Add JCenter repository:

    repositories {
        jcenter()
    }

Add dependency:

    implementation 'de.muspellheim:muspellheim-commons:1.0.0'

Or if you use PostgreSQL also:

    implementation 'de.muspellheim:muspellheim-commons:1.0.0'
    runtimeOnly('de.muspellheim:muspellheim-commons:1.0.0') {
        capabilities {
            requireCapability('de.muspellheim:muspellheim-commons-postgresql-support')
        }
    }

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
        <version>1.0.0</version>
    </dependency>

## Usage

See [JavaDoc](https://falkoschumann.github.io/java-muspellheim-commons/).
