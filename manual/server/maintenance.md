# Server maintenance

This document gives a brief introduction to how to administer the server.

## Container logs

Both tomcat and nginx write their own log files. All of the files are found in
`/var/log/`. Tomcat logs can also be found under `${catalina.base}/logs`. The
directory `${catalina.base}` is the base directory for tomcat configuration,
which might differ between operating systems. Common values are
`/usr/share/tomcatX` (e.g. on ArchLinux) and `/var/lib/tomcatX` (e.g. on
Debian).

## Stocks server log

The log file of the stocks server is at `/var/log/tomcatX/stocks.log`, where X
is your tomcat version. The used library is [log4j2](https://logging.apache.org/log4j/2.x/manual/index.html)
and the configuration file is stored in `/etc/stocks-server/log4j2.xml`. The
server refreshes the config every 10 seconds so the log level can be adjusted
without restarting the server.

## Stocks config file

The stocks config in `/etc/stocks-server/stocks.properties` can be adjusted at
any time. A restart of the tomcat container is required to apply the changes.
The recommended way of configuring stocks is via the official ansible role at
https://gitlab.com/veenj/ansible-stocks.

## Stocks migration

If for some reason the server has to be migrated to another instance the
following system parts must be backed up:

 * PostgreSQL stocks database
 * Stocks CA at `/usr/share/stocks-server/instance/<instance-name>/CA/`

In addition to that the following files should be saved if you modified them:

 * `/etc/stocks-server/stocks.properties`
 * Log4j2 configs
 * tomcat config
 * nginx config

If these files are copied to the new instance at the same location then the
whole state of the stocks server is transferred.

ATTENTION: The stocks CA contains sensitive key material which should be
handled with care. Make sure to erase the old keys as well as any temporary copy
to prevent giving access to adversaries.

## License

Copyright (C)  2019  The stocks developers

Permission is granted to copy, distribute and/or modify this document
under the terms of the GNU Free Documentation License, Version 1.3
or any later version published by the Free Software Foundation;
with no Invariant Sections, no Front-Cover Texts, and no Back-Cover Texts.
A copy of the license is included in the section entitled "GNU
Free Documentation License".
