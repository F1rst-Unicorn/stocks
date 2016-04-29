# Protocol to add new users

This document describes all entities and the actions they take for a new 
user to be added to the system. 

## Involved parties

* The user device to be added
* The user acting as a host
* The CA distribution point
* The sentry 
* The server

## Steps taken in order

### User creation
First the host creates a new user in the server's database (if the new user
and the host are not the same). Then the new device is added by the host. The
server will answer this request with a ticket for the new user. The server will
store the new user and device in the database. In the same action the device
is added, the server will create a new entry in the Ticket table, with the new
device id, the generated ticket and the current time. 

### Ticket transfer
The host will hand over (authenticated and confidential) the following message
to the new user: (username, user id, device name, device id, ticket, CA fpr). 
The parts of the message mean the following:

* username: The name of the user in the database
* user id: The id the database has assigned to the user
* device name: The name of the newly created device
* device id: The id the database has assigned to the device
* ticket: A string of random characters which grants access to the new device
* CA fpr: The SHA256 sum of the CA's key. 

### CA retrival
The new user device will now contact the CA distribution point and download the 
CA certificate. It will be compared to the fpr passed form the host. If the two
don't match the protocol fails now. 

### Certificate generation
The new device will create a new certificate and key. The Subject name of the 
certificate must have the form "<username>$<user id>$<device name>$<device id>".
He also generates a CSR for the certificate. 

### Certificate verification
The new device will send the following to the sentry: (ticket, CSR). The sentry
will now check for validity as follows:

* Check if the ticket is in the database
* Check if the CSR subject name matches the ticket's associated device
* Check if the ticket is in the valid time interval

If one of the checks fails, the sentry will reject the request. If everything
succeeds the sentry will sign the CSR and return the certificate to the new 
device. The sentry will remove the ticket from the Ticket table. 

### Synchronisation
Now the new device is set up. It can contact the server for the first time and
get the current system state. 
