# Server documentation

## Installation

First set up a MariaDB database. Import the schema at database/schema.sql
Also set the database URL and user credentials in TODO. 

Then set up the CA. 

Finally boot the server for the first time. 
It generates a ticket with content
'0000000000000000000000000000000000000000000000000000000000000000' (this is 64
times the '0' character). You can use it to generate the first user. 

Have fun. 

## Configuration
Currently the server has a class Config which serves as factory for all modules that can be
exchanged.

## Authentication mechanism
To authenticate users we use X509 client certificates in HTTPS. The certificates are issued by
the server itself. The server is thus a trusted entity.

The certificate subject name has to conform to the layout 'username$userid$devicename$deviceid'
where username and devicename are strings not containing the $ sign and userid and deviceid only
contain digits 0-9.

Once the connection is verified the subject name is parsed and the User's context is created.
