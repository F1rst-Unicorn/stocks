# Roadmap for subcomponent server

## Version 0.4
* 00000 : DONE : Basic functionality

## Version 0.5 (current)
* 00012 : DONE : Provide unit tests and integrate them with maven
* 00014 : DONE : Add Ansible playbook for deployment
* 00002 : INVALID : First database access on new server causes exception

## Version 0.6
* 00018 : NEW  : First database access on new server causes exception
* 00019 : NEW  : Split ansible script into setup and installation part
* 00017 : NEW  : Add integration test
* 00016 : NEW  : Migrate from Jetty to Tomcat 8
* 00001 : NEW  : Better logging, divide access from errors
* 00005 : NEW  : Clear obsolete tickets
* 00004 : NEW  : Add EAN-13 add and delete methods for food
* 00006 : NEW  : Image support for Food
* 00015 : DONE : Replace ansible systemd module by service calls

## Version 0.7
* 00010 : NEW  : Edit eat-by date
* 00007 : NEW  : Remember maximum items per food to emit warnings based on thresholds

## Version 1.0
* 00009 : NEW  : Add favourite food markers
* 00003 : NEW  : setup-ca script emits "unable to write random state"
* 00011 : NEW  : Race condition when adding Food_item and retrieving updates 
                 afterwards. Add command reaches server but client refresh does 
                 not get it. 

