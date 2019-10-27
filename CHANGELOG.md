# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

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


[Unreleased]: https://github.com/falkoschumann/java-muspellheim-commons/compare/v1.2.0...HEAD
[v1.2.0]: https://github.com/falkoschumann/java-muspellheim-commons/compare/v1.1.0...v1.2.0
[v1.1.0]: https://github.com/falkoschumann/java-muspellheim-commons/compare/v1.0.0...v1.1.0
[v1.0.0]: https://github.com/falkoschumann/java-muspellheim-commons/tree/v1.0.0
