# Roadmap for subcomponent server

## Version 0.4 (current)
* 00000 : DONE : Basic functionality

## Version 0.5
* 00012 : NEW  : Provide unit tests and integrate them with maven
* 00001 : NEW  : Better logging, divide access from errors
* 00002 : NEW  : First database access on new server causes exception
* 00004 : NEW  : Add EAN-13 add and delete methods for food

## Version 0.6
* 00013 : NEW  : Add Vagrant integration tests
* 00014 : NEW  : Add Ansible playbook for deployment
* 00005 : NEW  : Clear obsolete tickets
* 00006 : NEW  : Image support for Food
* 00007 : NEW  : Remember maximum items per food to emit warnings based on thresholds
* 00009 : NEW  : Add favourite food markers

## Version 0.7
* 00010 : NEW  : Edit eat-by date



## Version 1.0
* 00003 : NEW  : setup-ca script emits "unable to write random state"
* 00011 : NEW  : Race condition when adding Food_item and retrieving updates 
                 afterwards. Add command reaches server but client refresh does 
                 not get it. 

