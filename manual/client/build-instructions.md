# Build instructions

This guide tells how to build the client.

## Arch Linux

Checkout the git repository. Audit the script in `deploy-client/bin/package.sh`.
By default the script will sign the resulting package using your default gpg
key. If that is not desired, `export NO_SIGNATURE=1` before running the script.
If you have finished reading it, run it. The package will be placed in
`deploy-client/target/`.
