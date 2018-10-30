# Server maintainance

This document gives a brief introduction to how to administer the server.

## Container logs

Both tomcat and nginx write their own log files. All of the files are found in
/var/log/.

## Stocks server log

The log file of the stocks server is at /var/log/stocks-server/stocks.log.
The used library is log4j2. If the default tomcat server.xml is used the
log config is stored in /usr/share/tomcat8/webapps/server/WEB-INF/classes/
log4j2.xml. The server refreshes the config every 30 seconds
so the log level can be adjusted without restarting the server.

The config can be reset by removing the directory
/usr/share/tomcat8/webapps/server (but not the symlinks). Note that during
redeployment the webapp might be subject to downtime.

## Stocks config file

The stocks config in /etc/stocks-server/stocks.properties can be adjusted at
any time. A restart of the tomcat container is required to apply the changes.

## Stocks migration

If for some reason the server has to be migrated to another instance the
following system parts must be backed up:

 * PostgreSQL stocks database
 * Stocks CA at /usr/share/stocks-server/root/CA/

In addition to that the following files should be saved if you modified them:

 * /etc/stocks-server/stocks.properties
 * Log4j2 configs
 * tomcat config
 * nginx config

If these files are copied to the new instance at the same location then the
whole state of the stocks server is transferred.

ATTENTION: The stocks CA contains sensitive key material which should be
handled with care. Make sure to erase the old keys as well as any temporary copy
to prevent giving access to adversaries.
