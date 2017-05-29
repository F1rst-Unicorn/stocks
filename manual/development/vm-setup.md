# VM Overview

## Server testing

### dp-server

Testing the deployment of a fresh server instance
        
#### Snapshots

 - clean: A VM without any stocks-related files installed. Has all dependencies
   of stocks-server installed. Shutoff.

 - clean-running: A VM in the same state as "clean" but already running.

### int-server

Testing the upgrade procedure and regular operation

#### Snapshots

 - clean: A VM without any stocks-related files installed. Has all dependencies
   of stocks-server installed. Shutoff.

 - clean-running: A VM in the same state as "clean" but already running.

 - x.x.x: A VM with a setup stocks server at version x.x.x.

 - x.x.x-no-devices: A VM with a setup stocks server at version x.x.x but no
   devices registered yet. 

### dp-client-server

Testing the registration of a new client. stocks-server

#### Snapshots

 - clean: A VM without any stocks-related files installed. Has all dependencies
   of stocks-server installed. Shutoff.

 - clean-running: A VM in the same state as "clean" but already running.

 - initialised-running: A VM with a freshly installed stocks-server initialised
   with ansible.
