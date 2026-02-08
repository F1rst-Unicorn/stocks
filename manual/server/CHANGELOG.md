# Change Log

## Unreleased

### Changed

* Move to Spring Boot
  ([#112](https://veenj.de/git/veenj/stocks/issues/112)).

### Fixed

* Also recipe related tables are now cleaned by history cleanup
  ([#113](https://veenj.de/git/veenj/stocks/issues/113)).

## [6.0.0.0]

### Changed

* Moved to tomcat10 and Jakarta EE
  ([#109](https://veenj.de/git/veenj/stocks/issues/109)).

## [5.7.0.6]

### Fixed

* Clients must pass an `upUntil` parameter when getting updates to ensure they
  know the state of the data they receive
  ([#47](https://veenj.de/git/veenj/stocks/issues/47)).

## [5.7.0.5]

### Added

* All creating endpoints now return IDs of the created entity
  ([#62](https://veenj.de/git/veenj/stocks/issues/62)).

## [5.7.0.4]

### Fixed

* Errors connecting to the database are now reported as unreachable database to
  clients ([#61](https://veenj.de/git/veenj/stocks/issues/61)).

* DB connection timeout has been lowered to 3s.

* Reject adding user or device names with spaces
  ([#60](https://veenj.de/git/veenj/stocks/issues/60)).

## [5.7.0.3]

### Performance

* Accelerate consistency checks by only checking changed data instead of the
  whole database on change
  ([#79](https://veenj.de/git/veenj/stocks/issues/79)).

## [5.7.0.2]

### Added

* New food endpoint to edit all its fields at once.

## [5.7.0.1]

### Performance

* Accelerate consistency check done on every data change
  ([#58](https://veenj.de/git/veenj/stocks/issues/58))

## [5.7.0.0]

### Added

* New location endpoint to edit all its fields at once.

## [5.6.0.0]

### Added

* New food and location endpoints to add these things with all fields set.

## [5.5.0.7]

### Changed

* Remove the hystrix circuit breaker support. Thus
  `de.njsm.stocks.server.v2.circuitbreaker.timeout` is no longer needed in the
  configuration file. Stability will likely increase on slow hardware.
  ([#39](https://veenj.de/git/veenj/stocks/issues/39))

## [5.5.0.6]

### Fixed

* Update log4j to protect from
  [CVE-2021-44228](https://nvd.nist.gov/vuln/detail/CVE-2021-44228)

## [5.5.0.5]

### Fixed

* Prohibit devices from deleting themselves.
  ([#29](https://veenj.de/git/veenj/stocks/issues/29))

## [5.5.0.4]

### Fixed

* Run timed jobs every hour instead of every half minute.

## [5.5.0.3]

### Fixed

* When removing the first created scaled unit "1 Default" from the system no
  more food could be created.
  ([#24](https://veenj.de/git/veenj/stocks/issues/24))

## [5.5.0.2]

### Added

* Every installation now has a default system user "Stocks" with a device "Job
  Runner" ([#9](https://veenj.de/git/veenj/stocks/issues/9))

### Fixed

* Background jobs cleaning up the database actually work now
  ([#9](https://veenj.de/git/veenj/stocks/issues/9))

## [5.5.0.1]

### Fixed

* Allow recipe editing endpoint to add and delete ingredients and products

## [5.5.0.0]

### Added

* STOCKS-115: Extend API to support food and food item unit editing

* STOCKS-115: Extend API to support editing of units and scaled units

## [5.4.0.0]

### Added

* STOCKS-115: Extend data model for recipes and related data types. Extend API
  to allow managing units and scaled units

* STOCKS-115: Add DB indices for improved performance

### Changed

* STOCKS-115: Use lowercase names for all DB tables

### Fixed

* STOCKS-129: Allow empty food and location descriptions

* STOCKS-129: Editing food in the CLI client now won't overwrite expration
  offset or default location from the android client.

## [5.3.0.0]

### Added

* STOCKS-100: Add description field for food and locations.

### Fixed

* STOCKS-125: Fix SQL query for updates on future rows. This has had no impact
  on data validity.

## [5.2.0.1]

### Fixed

* STOCKS-124: Allow "younger" devices to modify and delete "older" objects which
  were present before the device was registered.

## [5.2.0.0]

### Fixed

* STOCKS-119: Fix memory leak leading to warnings during hot redeployments of
  the WAR.

* STOCKS-116: Fix error condition leading to background jobs failing without
  chance to recover.

### Changed

* STOCKS-120: Change file system locations of some files. SEE
  `manual/server/upgrading.md` FOR UPGRADE.

* STOCKS-119: The log configuration file is now available at
  `/etc/stocks-server/log4j2-<instance>.xml`

## [5.1.0.0]

### Added

* STOCKS-111: Add Debian packaging

* STOCKS-109: For each change to the data, store the initiating user device for
  auditing.

### Changed

* STOCKS-111: Replace nginx-reload by systemctl invocation

## [5.0.0.3-1]

### Fixed

* STOCKS-108: Fix logic of `startingFrom` query parameter in GET
  methods. Otherwise it cannot be used productively.

## [5.0.0.2-1]

### Fixed

* STOCKS-107: Fix setting food's default storage location

### Maintenance

* STOCKS-106: Upgrade to Java 11

* STOCKS-106: Upgrade library dependencies

## [5.0.0.1-1]

### Fixed

* Fix transaction + connection handlung for periodic jobs.

## [5.0.0.0-1]

### Added

* STOCKS-101: Support for bitemporal data. This allows showing the history of
  the stored entities. This is a breaking change because the API now reports all
  timestamps up to microsecond precision which breaks clients' timestamp
  parsing.

* STOCKS-102: Improve stability by reconfiguring the circuit breaker. This might
  improve stability on slow systems.

## [4.8.0.0-1]

### Added

* STOCKS-93: Use DB migration framework liquibase so this will be the last time
  to install DB changes manually

## [4.7.0.0-1]

### Added

* Use DB connection pool

### Modified

* Logs are now stored in `/var/log/tomcat8` to simplify file permission
  management

### Fixed

* Repeat business transactions correctly

* Stream entities when getting them. Leads to lower memory footprint

* Shutdown properly by closing all libraries' threads

### Maintenance

* Update library dependencies

## [4.6.0.1-1]

### Fixed

* DB Connection failure in background job

## [4.6.0.0-1]

### Fixed

* DB Connection leak in health check

### Added

* STOCKS-66: Add background job to check for data inconsistency on users

## [4.5.0.0-1]

### Added

* STOCKS-69: Support default store location for food

## [4.4.0.0-1]

### Added

* STOCKS-59: Add food default expiration offsets

## [4.3.0.0-1]

### Added

* STOCKS-90: Add health check endpoint

## [4.2.0.0-1]

### Added

* STOCKS-87: IPv6 Support

* STOCKS-88: Prometheus metrics

### Fixed

* STOCKS-89: Set HTTP status codes correctly

## [4.1.0.1-1]

### Fixed

* STOCKS-85: Handle concurrent item changes without triggering the circuit
  breaker.

* STOCKS-76: Reject creation of users with names containing `$` or `=` which are
  forbidden.

## [4.1.0.0-1]

### Added

* STOCKS-83: Add shopping list feature

## Release [4.0.0.0-1]

### Removed

* STOCKS-81: v1 API endpoints are gone

### Added

* STOCKS-78: New configuration option for circuit breaker timeout

### Fixed

* STOCKS-77: Circuit breaker ignores errors when DB is unreachable

## Release [3.1.0.2-1]

### Fixed

* STOCKS-72: `PUT /v2/device` no longer returns superfluous field `pemFile`.

### Removed

* STOCKS-73: Postgresql migration tool is no longer packaged

## Release [3.1.0.1-1]

### Fixed

* `PUT /v2/food/rename` and `PUT /v2/location/rename` no longer read the new
  name from the wrong parameter `newName` but from the documented parameter
  `new`.

## Release [3.1.0.0-1]

### Added

* STOCKS-65: Run DB transactions serialisable

### Changed

* STOCKS-64: Update software library dependencies

## Release [3.0.0.0-1]

### Changed

* STOCKS-60: Replace MariaDB by Postgresql as DBMS

## Release [2.2.0.0-1]

### Deprecated

* STOCKS-37: All endpoints without `/v2/` prefix are being removed in the far
  future

### Added

* STOCKS-37: Add new protocol version `/v2/`

* STOCKS-61: Publish ansible role as official deployment method. See
  [the repo](https://github.com/F1rst-Unicorn/ansible-stocks.git)

## Release [2.1.0.0-1]

### Added

* STOCKS-9: Introduce circuit breaker for database and file system interaction

* STOCKS-11: Add data versioning. Every entity is now tracked with a version
  number.

### Fixed

* STOCKS-10: Prevent concurrent access to the CA which might lead to
  inconsistency

## Release [2.0.1.0-0]

### Added

* STOCKS-14: Recommend DH parameters for nginx

## Release [2.0.0.0-0]

### Fixed

* STOCKS-50: Store timestamps up to millisecond precision

## Release [1.0.2]

### Fixed

* Adapt principal parser for different nginx format for TLS client name

## Release [1.0.1]

Maintenance release

## Release [1.0.0]

First stable version reached

## Release [0.5.7]

### Added

* Support for storing EAN codes

* Provide upgrade instructions for users

* Log all HTTP accesses

### Fixed

* Close DB connections after usage

## Release [0.5.6]

### Added

* Write maintenance guide for admins

* Provide logging via log4j

* Introduce rigorous system tests

### Changed

* Replace Jetty container support by tomcat8

## Release [0.5]

Creative chaos

## Release [0.4]

Creative chaos
