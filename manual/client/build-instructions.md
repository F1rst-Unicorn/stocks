# Build instructions

This guide tells how to build the client.

## Arch Linux

Checkout the git repository. Audit the script in `deploy-client/bin/package.sh`.
By default the script will sign the resulting package using your default gpg
key. If that is not desired, `export NO_SIGNATURE=1` before running the script.
If you have finished reading it, run it. The package will be placed in

## License

Copyright (C)  2019  The stocks developers

Permission is granted to copy, distribute and/or modify this document
under the terms of the GNU Free Documentation License, Version 1.3
or any later version published by the Free Software Foundation;
with no Invariant Sections, no Front-Cover Texts, and no Back-Cover Texts.
A copy of the license is included in the section entitled "GNU
Free Documentation License".

`deploy-client/target/`.
