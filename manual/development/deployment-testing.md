# Deployment Testing

Basic testing can be done by following the installation guide in manual/admin/
server-installation.md. However this is very cumbersome. 

## Building a package

Currently only Arch Linux packaging is available. To build the package, copy the
PKGBUILD and stocks-server.install to an empty directory. Then run 

```
$ makepkg
```

to build the package. Default is packaging the master branch. To package the
dev-server branch run

```
$ makepkg STOCKSBRANCH=dev-server
```

## Ansible deployment

Once the package is built and installed on the server you can use the 
ansible-playbook in deploy-server/deploy.yml to test deployment via ansible. 
