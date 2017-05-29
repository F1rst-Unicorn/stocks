# Roadmap for subcomponent client

## Version 0.4 (current)
* 00000 : DONE : Basic functionality

## Version 0.5
* 00002 : DONE : Add Unit testing
* 00003 : NEW  : Include database setup into program on first startup
* 00005 : DONE : Rework logging, use log4j
* 00008 : DONE : Add release build management
* 00009 : DONE : Refactor client for testability
* 00010 : DONE : Verify output in system tests
* 00011 : DONE : Use dependency injection
* 00012 : DONE : Make sure select methods return index from list
* 00013 : DONE : Refactor input parsing to lower dependencies
* 00015 : DONE : Fix broken system tests
* 00016 : DONE : Prevent sending SQL statements in JSON
* 00017 : DONE : Add visitors for common data
* 00019 : NEW  : Test client on dedicated server
* 00020 : DONE : Update client maven dependencies
* 00021 : NEW  : Add mechanism to upgrade DB on software update
* 00022 : NEW  : Add system tests for jline

## Version 0.6
* 00001 : NEW  : Add emitting QR codes for new users
* 00004 : NEW  : Add methods to get and clear pending tickets
* 00006 : NEW  : Improve displaying food lists
* 00007 : NEW  : Enable entering names with spaces
* 00018 : NEW  : Optimise system tests to only use one JVM process
