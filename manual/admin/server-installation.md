# Server Installation

## Ansible Installation

To automatically deploy the server to a fresh machine the ansible playbook in 
deploy-server/deploy.yml can be used. However, this is bound to certain 
constraints, since the playbook assumes a clean machine. You should not use the
playbook if one of the following holds:

 * The machine already has an nginx instance running with a custom nginx.conf
 * The machine already contains a database named stocks
 * One of the ports 10910, 10911, 10912, 10916 or 10917 is already in use

If one of the constraints holds the configs have to be edited as described 
below. 

### Playbook variables

The playbook defines many variables over which one can finetune the 
installation. Their meaning is described below: 

target_host: The ansible host group on which to run the playbook
remote_mysql_password: The password of the MySQL root user on the target host
stocks_base: Default install folder, Don't change this, only for readability
stocks_user: The first user to install. Change to your needs
stocks_device: The first device to install. Change to your needs
sudoers_line: The line added to sudoers. To simplify auditing

## Manual Installation

### Database

Stocks needs a MariaDB database. Set it up and make it reachable over 
localhost. Create a user for the server. 

```
CREATE DATABASE stocks;
CREATE USER 'server'@'localhost' IDENTIFIED BY 'linux';
GRANT ALL PRIVILEGES ON stocks.* TO 'server'@'localhost';
FLUSH PRIVILEGES;
```

Then set the credentials in /etc/stocks-server/stocks.properties to make it 
accessible to the server. Import the schema in 
/usr/share/stocks-server/schema.sql into the database. 

### CA

Run the script in /usr/lib/stocks-server/setup-ca as user stocks to generate a 
CA and all needed keys. The script takes two arguments, provided as fully 
qualified paths: The first is the directory where to install the CA. The second
path is where to find the openSSL config templates. For production deployment 
these arguments are:

* /usr/share/stocks-server/root/CA
* /usr/share/stocks-server/

After the script has finished copy the files below to the nginx root which 
defaults to /usr/share/stocks-server/root/nginx. 

* /usr/share/stocks-server/root/CA/certs/ca.cert.pem
* /usr/share/stocks-server/root/CA/intermediate/certs/ca-chain.cert.pem

### nginx

Set up nginx as reverse proxy with the corresponding SSL configs. 
For an example config have a look at /usr/share/stocks-server/nginx.conf.
Give nginx access to the certificates generated above. Particularly

* server.key.pem
* server.cert.pem
* ca-chain.cert.pem
* ca.cert.pem

Furthermore the stocks server must be able to reload nginx to update the CRL
nginx reads from. This means the user stocks must be allowed to execute 
/usr/lib/stocks-server/nginx-reload as root via sudo. Add the following line
to your /etc/sudoers file

```
stocks ALL=NOPASSWD: /usr/lib/stocks-server/nginx-reload
```

### Jetty server

To adapt the stocks server to your system, adjust the needed values in 
/etc/stocks-server/stocks.properties. 

### Start up the components

* Start mysql
* Start jetty
* Start nginx

### Adding the first user

Due to the protocol to add new users, adding the first one has to be done
manually. Log into the database and add a user, user device and ticket. E.g. 

```
INSERT INTO User (name) VALUES (desired_name);
INSERT INTO User_device (name, belongs_to) VALUES (desired_name, LAST_INSERT_ID());
INSERT INTO Ticket (ticket, belongs_device) VALUES (some_string, LAST_INSERT_ID());
```

Then you can read out the needed values from the database and talk to the 
sentry (see spec/new-user-creation-protocol.md). 
 
