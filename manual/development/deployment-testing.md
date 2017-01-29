# Deployment Testing

## Server / Sentry

There are various different ways to test the server components

### Unit and Integration Tests

There are many unit and integration tests located in src/java/test of the
respective component. It contains a mix of unit tests which can run in
isolation, but also integration tests which require the existence of a stocks
mariadb database. They only test small parts of the system in isolation.

#### Test DB setup

To run the tests you need a mariadb stocks instance. It can be created by
importing the deploy-server/config/schema.sql script into a newly created
database. No data imports are needed. Then configure your profile for running
inside the server/sentry pom.xml, or adapt your setup to match an existing
environment. Then run the tests via 'mvn test' or from inside your IDE.

### System tests

To test the whole system including the setup you need to build a package from
the sources and deploy it on some machine. Ask the other developers to provide
you a libvirt VM to perform testing or build an own VM setup.
The following assumes you use the developer's libvirt VMs.

#### Building a new package

To create an Arch Linux package from the sources, run the deploy-server/bin/
package.sh script (this will also sign the package with your default GPG key).
The package will be located in deploy-server/target.

#### Testing new server creation

After the package has been built all you need to do is running the server/src/
test/system/test.sh script. It will locate the package you built depending on
the version number.
Then it resets the VM to a clean state. After that two ansible scripts will
be started on the VM, one for installing, and one for deploying. The deployment
script will just do the same as described in manual/server-admin/server-
installation.md.
After successful setup the server will be tested from a mock client which calls
on all the available endpoints and verifies the answers from the server. It
also tests the creation of new devices (with certificate handling) as well
as user revocation.

#### Testing server upgrades

To test whether the server package can be used to upgrade existing instances
the scripts in server/src/test/upgrade can be used. To setup the new server
just install it according to the manual. Then copy the two scripts to the
server. Run the setup.sh script. It will save the client certificate on the
server to use it in the upgrade test script.
Now whenever an upgrade shall be tested, you install the package according
to the upgrade instructions. After startup of the server just run the upgrade.sh
script which will test whether all endpoints still work.