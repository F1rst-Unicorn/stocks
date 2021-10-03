# Change Log

## Unreleased

## [3.11.0.0]

### Added

* Allow management of recipes. This means adding, viewing, modifying and
  deleting them ([#1](https://gitlab.com/veenj/stocks/-/issues/1))

## [3.10.0.2]

### Fixed

* The app used to hang in certain circumstances when a 0 amount should have been
  displayed for a food type.

## [3.10.0.1]

### Added

* Aggregate amounts with common unit

* Sort units alphabetically

* Trim unit names before adding them

### Fixed

* Display food amounts with unit in all places now

## [3.10.0.0]

### Added

* STOCKS-115: Support for units of measurement

## [3.9.0.2]

### Fixed

* STOCKS-132: Improve usability of food item list swiping by disabling the swipe
  gestures of the overall Tab view. This makes it easier again to remove
  consumed food items.

## [3.9.0.1]

### Fixed

* Crash during startup from older app version

## [3.9.0.0]

### Added

* STOCKS-100: Allow viewing and editing food and location descriptions

## [3.8.0.3]

### Changed

* STOCKS-127: Don't show an UNDO button when deleting food items

* STOCKS-127: Predict eat-by date for new items based on items which have been
  deleted if currently no items are present

## [3.8.0.2]

### Fixed

* Fix reset of selected date while adding food items

* STOCKS-126: Fix histogram plot which counted too many items

## [3.8.0.1]

### Fixed

* STOCKS-104: Only show whole numbers on food item count axis

## [3.8.0.0]

### Added

* STOCKS-104: Show neat diagrams on food items

## [3.7.0.0]

### Added

* STOCKS-117: Show initiator of each event by consuming new feature from server.

### Fixed

* STOCKS-122: Fix reset to default of location and date when adding new food
  items.

* STOCKS-112: Don't show history button if it is not useful.

* STOCKS-118: Fix displaying main event history's very first event, which is the
  first user creation.

## [3.6.0.3]

### Added

* STOCKS-103: Show events of a single food type or location.

## [3.6.0.2]

### Added

* STOCKS-103: Allow clicking on events.

## [3.6.0.1]

### Fixed

* STOCKS-103: Fix crash when opening the app because of illegally reused views.

* STOCKS-103: Display missing information about recent events.

## [3.6.0.0]

### Added

* STOCKS-103: Support for bitemporal data. Save network bandwidth by only
  synchronising data that really changed. Present recent activity to the users.

## [3.5.0.2]

### Fixed

* STOCKS-101 contained an invalid DB migration making the app crash on start up.
  This is now fixed.

## [3.5.0.1]

### Added

* STOCKS-101: Support microsecond-resolution timestamps

## [3.5.0.0]

### Added

* STOCKS-49: Add search suggestions in dropdown while searching

* STOCKS-99: Allow renaming, deleting and mark-for-shopping on all screens
  showing food types

* STOCKS-99: Improve input validation in settings

## [3.4.0.0]

### Added

* STOCKS-69: Support default locations

## [3.3.0.1]

### Fixed

* Startup crash when upgrading an existing app version

## [3.3.0.0]

### Added

* STOCKS-59: Support for expiration offsets for food

## [3.2.0.0]

### Added

* STOCKS-84: Allow editing of settings for server hostname and ports

## [3.1.0.0]

### Added

* STOCKS-83: Add shopping list feature

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
