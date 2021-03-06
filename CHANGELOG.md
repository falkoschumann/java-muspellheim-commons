# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [v2.3.1] - 2020-01-07

### Fixed

*   `ConstructorResultSetMapper` throws illegal state exception with helpful
    message if constructor properties configuration missing, instead of thrown
    null pointer exception.

## [v2.3.0] - 2019-12-08

### Added

*   Future for SQL queries.
*   Flux architecture helper classes. 

## [v2.2.0] - 2019-12-01

### Added

*   Repository exceptions for connection and authorization.
*   Base class for SQL based repository, which can check for default repository
    exceptions.  

## [v2.1.0] - 2019-11-20

### Added

*   `ConfigurationNotFoundException` will be thrown if properties file was not
    found.

## [v2.0.0] - 2019-11-13

### Added

*   Query canceled exception for repository.

### Changed

*   Annotation `@TestSeam` moved to new package `test`. 

### Removed

*   Deprecated APIs.

## [v1.9.0] - 2019-11-12

### Added

*   Queued executor for task.

## [v1.8.0] - 2019-11-07

### Added

*   Map value and value list from single column with ´ResultSetMapper`.

## [v1.7.0] - 2019-11-05

### Changes

*   Time intervals override `toString()`.

## [v1.6.0] - 2019-11-02

### Changes

*   Event bus delivers events asynchronously.

### Fixes

*   Creating `About` when reading from class object.

## [v1.5.0] - 2019-10-31

### Added

*   Methods to obtain `LocalDateInterval` for yesterday, today, tomorrow, last
    days and next days. 

## [v.1.4.0] - 2019-10-29

### Changed

*   Time intervals can be empty (duration = 0).

## [v1.3.0] - 2019-10-28

### Added

*   `ResultSetMapper`: Add method `resultList()` to map a list of entities from
    single result set.

### Fixed

*   `ResultSetMapper`: Instead of throwing `NullPointerException` without
     message throws `IllegalStateException` with missing column mapper. 

## [v1.2.0] - 2019-10-27

### Changed

*   Resource bundles do not rely on default locale.
*   Simplify creation of `About` instances.

### Fixed

*   Wrong parameter notation in resource bundle of `About`.

## [v1.1.0] - 2019-10-27

### Added

*   Simple object relational mapping (ORM).
*   Interval objects based on `LocalDateTime`, `LocalDate` or `Instant`. 

## [v1.0.0] - 2019-10-21

### Added

*   Additional event patterns.
*   A configuration object for configuring an app with properties files.
*   An version object for dealing with semantic versioning.
*   An object for dealing with information about an app.
*   Configurable factory for a Postgres `DataSource` (JDBC).
*   Common domain exceptions for implementing a repository.


[Unreleased]: https://github.com/falkoschumann/java-muspellheim-commons/compare/v2.2.0...HEAD
[v221.0]: https://github.com/falkoschumann/java-muspellheim-commons/compare/v2.1.0...v2.2.0
[v2.1.0]: https://github.com/falkoschumann/java-muspellheim-commons/compare/v2.0.0...v2.1.0
[v2.0.0]: https://github.com/falkoschumann/java-muspellheim-commons/compare/v1.9.0...v2.0.0
[v1.9.0]: https://github.com/falkoschumann/java-muspellheim-commons/compare/v1.8.0...v1.9.0
[v1.8.0]: https://github.com/falkoschumann/java-muspellheim-commons/compare/v1.7.0...v1.8.0
[v1.7.0]: https://github.com/falkoschumann/java-muspellheim-commons/compare/v1.6.0...v1.7.0
[v1.6.0]: https://github.com/falkoschumann/java-muspellheim-commons/compare/v1.5.0...v1.6.0
[v1.5.0]: https://github.com/falkoschumann/java-muspellheim-commons/compare/v1.4.0...v1.5.0
[v1.4.0]: https://github.com/falkoschumann/java-muspellheim-commons/compare/v1.3.0...v1.4.0
[v1.3.0]: https://github.com/falkoschumann/java-muspellheim-commons/compare/v1.2.0...v1.3.0
[v1.2.0]: https://github.com/falkoschumann/java-muspellheim-commons/compare/v1.1.0...v1.2.0
[v1.1.0]: https://github.com/falkoschumann/java-muspellheim-commons/compare/v1.0.0...v1.1.0
[v1.0.0]: https://github.com/falkoschumann/java-muspellheim-commons/tree/v1.0.0
