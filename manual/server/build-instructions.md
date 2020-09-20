# Build instructions

This guide tells you how to build the server.

## General steps

1. Checkout the git repository.
2. Create a PostgreSQL database. Make sure the database is accessible for the
   build user with the properties given in the default profile in
   `server/pom.xml`.

## Arch Linux

1. Audit the PKGBUILD in `deploy-server`.
2. Build the package using the provided `PKGBUILD`.

## Debian

1. Audit the `debian/` directory.
2. Ensure that the build dependencies are installed.
3. Build the package using `dpkg-buildpackge -us -uc` from the project root.

This will build both the client and server packages and put them in the parent
directory of the project root.

## License

Copyright (C)  2019  The stocks developers

Permission is granted to copy, distribute and/or modify this document
under the terms of the GNU Free Documentation License, Version 1.3
or any later version published by the Free Software Foundation;
with no Invariant Sections, no Front-Cover Texts, and no Back-Cover Texts.
A copy of the license is included in the section entitled "GNU
Free Documentation License".
