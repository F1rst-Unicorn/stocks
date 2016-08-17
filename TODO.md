# Planned features

## Linux
* Add emitting QR codes 

## Android
* Add searching food
* Add possibility to rename food and locations
* Prevent usage of spaces in names
* Add warning level (i.e. when to show alarm icon) setting
* Add notifications for food to eat

## General
* Add methods to get and clear pending tickets
* Image support for Food
* Remember maximum items per food to emit warnings based on thresholds
* Add EAN-13 code support
* Add history of actions
* Add favourite food markers



# Open issues 

## General
* Race condition when adding Food_item and retrieving updates afterwards. 
Add command reaches server but client refresh does not get it. 

## Server
* First access to database crashes due to driver not found
* setup-ca script emits "unable to write random state"

## Sentry
* First access to database crashes due to driver not found



# Internal
* Add test suites
