# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

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


[Unreleased]: https://github.com/falkoschumann/java-muspellheim-commons/compare/v1.7.0...HEAD
[v1.7.0]: https://github.com/falkoschumann/java-muspellheim-commons/compare/v1.6.0...v1.7.0
[v1.6.0]: https://github.com/falkoschumann/java-muspellheim-commons/compare/v1.5.0...v1.6.0
[v1.5.0]: https://github.com/falkoschumann/java-muspellheim-commons/compare/v1.4.0...v1.5.0
[v1.4.0]: https://github.com/falkoschumann/java-muspellheim-commons/compare/v1.3.0...v1.4.0
[v1.3.0]: https://github.com/falkoschumann/java-muspellheim-commons/compare/v1.2.0...v1.3.0
[v1.2.0]: https://github.com/falkoschumann/java-muspellheim-commons/compare/v1.1.0...v1.2.0
[v1.1.0]: https://github.com/falkoschumann/java-muspellheim-commons/compare/v1.0.0...v1.1.0
[v1.0.0]: https://github.com/falkoschumann/java-muspellheim-commons/tree/v1.0.0
