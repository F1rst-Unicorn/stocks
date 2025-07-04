# Change Log

## Unreleased

### Fixed

* Adapt to a new server version 
  ([#47](https://veenj.de/git/veenj/stocks/issues/47)).  

## [4.9.0.4]

### Fixed

* Prevent the app bar from disappearing below the android status bar.

## [4.9.0.3]

### Fixed

* Setting a food's default expiration offset to 0 works now
  ([#108](https://veenj.de/git/veenj/stocks/issues/108)).

## [4.9.0.2]

### Fixed

* The search result view now shows the same results as the search-as-you-type
  dropdown ([#94](https://veenj.de/git/veenj/stocks/issues/94)).

* A crash after editing a recipe has been fixed
  ([#107](https://veenj.de/git/veenj/stocks/issues/107)).

## [4.9.0.1]

### Fixed

* The food item view shown from the recipe cooking view is now fully functional
  ([#103](https://veenj.de/git/veenj/stocks/issues/103)).

* The activity feed now scrolls to top on new events only when refreshing
  ([#102](https://veenj.de/git/veenj/stocks/issues/102)).

## [4.9.0.0]

### Fixed

* The activity feed now scrolls to top on new events
  ([#96](https://veenj.de/git/veenj/stocks/issues/96)).

### Added

* Clicking on an ingredient or product when cooking a recipe shows its food
  details
  ([#44](https://veenj.de/git/veenj/stocks/issues/44)).

## [4.8.0.4]

### Fixed

* Store form data when switching apps during recipe cooking, adding or editing
  ([#93](https://veenj.de/git/veenj/stocks/issues/93)).

## [4.8.0.3]

### Fixed

* Fix rare error where showing activity events showed wrong order.

## [4.8.0.2]

### Fixed

* Fix that first scan of QR code on setup doesn't move to final form
  ([#92](https://veenj.de/git/veenj/stocks/issues/92)).

## [4.8.0.1]

### Fixed

* Long-pressing a user device actually shows its history.

## [4.8.0.0]

### Added

* User's and device's activity (by long pressing it) can be shown
  ([#78](https://veenj.de/git/veenj/stocks/issues/78)).

## [4.7.0.0]

### Added

* Recipes can now be sorted by their cookability indicators
  ([#73](https://veenj.de/git/veenj/stocks/issues/73)).

### Fixed

* After the initial synchronisation the activicy view is no longer empty
  ([#91](https://veenj.de/git/veenj/stocks/issues/91)).

## [4.6.0.1]

### Fixed

* Adding food items via recipe where the food type has no default location set
  works now ([#89](https://veenj.de/git/veenj/stocks/issues/89)).

* A problem where a food list is not updated if a food is put on the shopping
  list via swiping was fixed
  ([#90](https://veenj.de/git/veenj/stocks/issues/90)).

* A problem where the food item numbers of a recipe are not updated after recipe
  cooking was fixed
  ([#90](https://veenj.de/git/veenj/stocks/issues/90)).

## [4.6.0.0]

### Added

* Recipe ingredients and products can now be checked out in bulk again
  ([#69](https://veenj.de/git/veenj/stocks/issues/69)).

## [4.5.0.4]

### Fixed

* Improved performance of the activity feed.

## [4.5.0.3]

### Fixed

* Retrying registration after an initial failed attempt works again.

* Improved performance of the activity feed.

## [4.5.0.2]

### Fixed

* Improved performance of the activity feed.

* Upgraded library dependencies.

## [4.5.0.1]

### Fixed

* Improved performance when navigating back and forth between different views a
  lot ([#85](https://veenj.de/git/veenj/stocks/issues/85)).

* Units are grouped by unit name before scale again
  ([#84](https://veenj.de/git/veenj/stocks/issues/84)).

## [4.5.0.0]

### Added

* Recipes can now be edited again
  ([#66](https://veenj.de/git/veenj/stocks/issues/66)).

## [4.4.0.0]

### Added

* Recipes can now be deleted again
  ([#68](https://veenj.de/git/veenj/stocks/issues/68)).

### Fixed

* Adding food items no longer modifies the form still adding more items.
  ([#81](https://veenj.de/git/veenj/stocks/issues/81)).

* Inside a food's activity list swiping down now synchronises.

## [4.3.0.2]

### Added

* Show food details with charts again
  ([#64](https://veenj.de/git/veenj/stocks/issues/64)).

## [4.3.0.1]

### Fixed

* Sort units by their scale again
  ([#76](https://veenj.de/git/veenj/stocks/issues/76)).

## [4.3.0.0]

### Added

* Devices can now be added again
  ([#71](https://veenj.de/git/veenj/stocks/issues/71)).

### Fixed

* Show error message when adding user and clearing its name in the form.

* Editing food was not possible and is fixed now
  ([#80](https://veenj.de/git/veenj/stocks/issues/80)).

## [4.2.0.0]

### Added

* Users can now be added again
  ([#70](https://veenj.de/git/veenj/stocks/issues/70)).

## [4.1.0.1]

### Fixed

* Sum amounts of same unit in recipe ingredient details
  ([#75](https://veenj.de/git/veenj/stocks/issues/75)).

* Show food default unit of food in stock in recipe details instead of no text
  ([#75](https://veenj.de/git/veenj/stocks/issues/75)).

* Hide optional ingredients with needed amount of 0 from recipe ingredients
  ([#75](https://veenj.de/git/veenj/stocks/issues/75)).

* Sort ingredients and products alphabetically
  ([#75](https://veenj.de/git/veenj/stocks/issues/75)).

* Hide ingredients or products if they are empty, respectively
  ([#75](https://veenj.de/git/veenj/stocks/issues/75)).

* Show bottom toolbar when viewing specific history lists
  ([#75](https://veenj.de/git/veenj/stocks/issues/75)).

* Locations are now ordered alphabetically when adding food items
  ([#75](https://veenj.de/git/veenj/stocks/issues/75)).

## [4.1.0.0]

### Added

* Show recipe details for cooking
  ([#67](https://veenj.de/git/veenj/stocks/issues/67)).

## [4.0.0.1]

### Fixed

* Improve performance of listing food
  ([#27](https://veenj.de/git/veenj/stocks/issues/27)).

## [4.0.0.0]

### Added

* Bottom toolbar for error handling and background work indication
  ([#42](https://veenj.de/git/veenj/stocks/issues/42)).

* Any errors when changing data is now recorded for later retrying
  ([#42](https://veenj.de/git/veenj/stocks/issues/42)).

## [3.16.0.0]

### Added

* Allow searching for [subsequences](https://en.wikipedia.org/wiki/Subsequence)
  of food names. So searching for 'crt' can find the food 'Carrot' as all
  searched characters appear in the result in the same order.
  ([#36](https://veenj.de/git/veenj/stocks/issues/36))

* Hide recipe ingredients and products with amount 0 so they can be used to mark
  "optional" ingredients / products.
  ([#18](https://veenj.de/git/veenj/stocks/issues/18))

* Change layout of recipe registration form
  ([#37](https://veenj.de/git/veenj/stocks/issues/37))

## [3.15.0.0]

### Added

* Allow all ingredients and products of a recipe to be checked out and
  registered in one form.
  ([#12](https://veenj.de/git/veenj/stocks/issues/12))

## [3.14.0.0]

### Added

* Simplify device registration by adding all information to the QR code
  ([#26](https://veenj.de/git/veenj/stocks/issues/26))

## [3.13.0.2]

### Fixed

* Sort unit dropdown in food and recipes by the scale as well.
  ([#10](https://veenj.de/git/veenj/stocks/issues/10))

## [3.13.0.1]

### Fixed

* Crash during startup due to wrong DB migration

## [3.13.0.0]

### Added

* Allow sorting recipes by availability of ingredients. Every recipe gets two
  ratings, ranging from 0 to 7. One for "necessary for cooking" which increases
  if _any_ amount of an ingredient is present. If it is smaller than 7 at least
  one ingredient is definitely missing. The smaller the number, the more
  ingredients are definitely missing.
  Te second rating is "sufficient for cooking" which increases if an ingredient
  is present in the required unit of measure in sufficient amount. So a rating
  of 7 here guarantees that the recipe can be cooked.
  ([#15](https://veenj.de/git/veenj/stocks/issues/15))

## [3.12.0.0]

### Added

* Show current food stock in recipe detail view
  ([#20](https://veenj.de/git/veenj/stocks/issues/20))

## [3.11.0.1]

### Fixed

* Fix crash which would occur if a server cleanup job removes old devices
  ([#9](https://veenj.de/git/veenj/stocks/issues/9))

## [3.11.0.0]

### Added

* Allow management of recipes. This means adding, viewing, modifying and
  deleting them ([#1](https://veenj.de/git/veenj/stocks/issues/1))

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
