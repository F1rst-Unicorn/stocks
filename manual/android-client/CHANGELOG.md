# Change Log

## [Unreleased]

### Removed

* STOCKS-81: v1 API endpoints are gone

### Added

* STOCKS-78: New configuration option for circuit breaker timeout

## Release [3.0.0.2]

### Fixed

* Swipe-to-delete icon was not drawn on Android 9

## Release [3.0.0.1]

### Fixed

* Registration was broken

## Release [3.0.0.0]

### Changed

* Use new `/v2/` API now. The client now respects versioned objects

* New Look-and-Feel: Animated screen transitions, swipe-to-delete

## Release [2.0.1.6]

### Added

* STOCKS-58: Support for Android 8.1

* STOCKS-18: Show QR code for device registration

## Release [2.0.1.5]

### Added

* Scanning unknown barcodes now shows list of food to assign barcode to

## Release [2.0.1.4]

Maintenance release

## Release [2.0.1.3]

Maintenance release

## Release [2.0.1.2]

### Fixed

* STOCKS-54: Fix race condition when editing items leading to a crash

## Release [2.0.1.1]

### Fixed

* STOCKS-52: Fix crash when viewing list of crash logs

* STOCKS-24: Select correct location when editing an item

* STOCKS-24: Keep food item when aborting to edit

### Changed

* STOCKS-24: Set title to "Edit" when editing items

## Release [2.0.1.0]

### Added

* STOCKS-24: Allow editing food items

### Fixed

* STOCKS-51: No more crashing when aborting to scan EAN code

## Release [2.0.0.0]

### Fixed

* STOCKS-50: Support new timezone format. This helps showing changes to the user
  which sometimes were omitted.

## Release [1.0.1.0]

### Added

* STOCKS-43: Support for EAN code scanning and managing

## Release [1.0.0.1]

### Fixed

* STOCKS-45: Wrong location was preselected according to maximum storage
  location when adding items

### Added

* STOCK-46: Show message when search yields no result

## Release [1.0.0.0]

First stable release

## Release [0.6.0.1]

### Fixed

* STOCKS-42: Prevent adding food items when no location present

### Added

* STOCKS-41: Validate user input during registration

## Release [0.6.0.0]

### Added

* STOCKS-35: Introduce rigorous system tests

## Release [0.5.1.1]

Maintenance release

## Release [0.5.1]

### Added

* Catch exceptions and write them to a crash log which can be sent to the
  developers

## Release [0.5.0]

### Fixed

* Crash no more when updates arrive in different order from server

## Release [0.4.1]

Creative chaos

## Release [0.4.0]

Creative chaos