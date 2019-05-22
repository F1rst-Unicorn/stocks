# Deployment Testing

## Server

There are various different ways to test the server components

### Unit and Integration Tests

There are many unit and integration tests located in `src/java/test`.
It contains a mix of unit tests which can run in isolation, but also
integration tests which require the existence of a stocks
PostgreSQL database or do access the local file system. They only test small
parts of the system in isolation.

#### Integration Test Database Setup

To run the tests you need a PostgreSQL stocks instance. It can be created by
importing the `deploy-server/config/schema.sql` script into a newly created
database. No data imports are needed. Then configure your profile for running
inside the server pom.xml, or adapt your setup to match an existing
environment. Then run the tests via `mvn test` or from inside your IDE.

### System Tests

To test the whole system including the setup you need to build a package from
the sources (see below) and run the system test script (see below).
Ask the other developers to provide you a libvirt VM and help you setup a
local environment. The following assumes you use the developer's libvirt VMs.

#### Building a New Package

To create an Arch Linux package from the sources, run the `deploy-server/bin/
package.sh` script. This will also sign the package with your default GPG key.
To prevent signing the package, `export NO_SIGNATURE=1`.
The package will be located in `deploy-server/target`.

#### Testing New Server Creation

After the package has been built all you need to do is running the `server/src/
test/system/bin/vm-deployment-test.sh` script. It will locate the package you
built depending on the version number.
After successful setup the server will be tested from a mock client which calls
on all the available endpoints and verifies the answers from the server. It
also tests the creation of new devices (with certificate handling) as well
as user revocation.

#### Testing Server Upgrades

There is currently no uniform concept to test server upgrades.

## Client

### Unit and Integration Tests

There are many unit and integration tests located in `src/java/test`.
It contains a mix of unit tests which can run in isolation, but also
integration tests which require the existence of a stocks
mariadb database or do access the local file system. They only test small
parts of the system in isolation.

### System Tests

To test the whole client you need to have the server set up as described above.
Moreover you need a second VM set up to deploy the client on as described in
`manual/development/vm-setup.md`.

#### Building a New Package

To create an Arch Linux package from the sources, run the `deploy-client/bin/
package.sh` script. This will also sign the package with your default GPG key
unless you `export NO_SIGNATURE=1`.
The package will be located in `deploy-client/target`.

#### Testing New Clients

After the package has been built all you need to do is running the `client/src/
test/system/bin/vm-deployment-test.sh` script. It will locate the package you
built depending on the version number.
The use cases provided in `client/src/test/system/usecases/` will be run in
alphabetical order to verify the behaviour of the client.
Output texts will contain both human readable text as well as TeamCity
service messages to be parsed by the CI server.

#### Testing Client Upgrades

Since the client has to be able to upgrade itself without any interaction
upgrades should not contain any test-worthy logic. Anything which needs to be
done has to be implemented in the Java upgrading package and be unit/integration
tested with JUnit.

