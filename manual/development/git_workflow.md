# Git Workflow for this project

The programming tasks in the project have to be done on dedicated branches. 
Eventually features are merged into the mainstream branches. The exact tasks for
the branches follow. 

## Branch master
This branch is the production branch. Any code on this branch has to be 
deployable as is on a new system. It contains the latest stable version of every
of the four subcomponents (defined below). 

## Branches dev-server, dev-sentry, dev-client, dev-android
These branches contain all completed features for the specific subcomponent. 
Once all features of the roadmap of the specific project are implemented and 
verified to work in integration, the version number is increased to the new 
version and merged into the master branch. This way the master branch contains
the latest version of each subcomponent. 
If a branch does not exist, this means that it is at the same commit as the 
master branch, so to start new features, first the corresponding dev branch
has to be created. 

## Feature branches
Each feature needed for a new version is implemented in a separate feature 
branch. The naming convention is subcomponent-featureid, where the subcomponent
is one of the four defined above and the featureid can be obtained by the 
roadmap of the project. 
Inside a feature branch, everything that is needed for the feature to function
is written. This includes in particular

* Implementation
* Unit tests
* Code documentation
* Model documentation (changes in the DB in ER, etc.)
* User documentation
* Integration tests (if possible)

Only features that prove themselves verified to work properly are merged into 
the dev branch. 
