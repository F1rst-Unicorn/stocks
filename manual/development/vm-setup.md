# VM Overview


## dp-server

VM to setup new server instances. All stocks depenencies are already installed
to accelerate testing.
        
### Snapshots

 - clean: A VM without any stocks-related files installed. Has all dependencies
   of stocks-server installed. Shutoff.

 - clean-running: A VM in the same state as "clean" but already running.

 - initialised-running: Some server instance has been installed and is ready
   to register users. The default values for the first user when installed via
   ansible are
    
    - User name: Jack
    - User ID: 1
    - Device name: Device
    - Device ID: 1
    - Ticket: 0000
   
   This snapshot is being recreated during automated system tests, so depending
   on the current state of the CI server the snapshot might be missing

## dp-client

Testing the registration of a new client. stocks-server

### Snapshots

 - clean: A VM without any stocks-related files installed. Has all dependencies
   of stocks installed. Shutoff.

 - clean-running: A VM in the same state as "clean" but already running.

