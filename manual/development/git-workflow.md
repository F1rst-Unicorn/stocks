# Git Workflow for this project

The programming tasks in the project have to be done on dedicated branches. 
Eventually features are merged into the mainstream branches. The exact tasks for
the branches follow. 

## Branch master
This branch is the production branch. Any code on this branch has to be 
deployable as is on a new system. It contains the latest stable version of every
of the three subcomponents (defined below). 

## Branches dev-server, dev-client, dev-android
These branches contain all completed features for the specific subcomponent. 
Once all features of the roadmap of the specific component are implemented and 
verified to work in integration, the version number is increased to the new 
version and merged into the master branch. This way the master branch contains
the latest version of each subcomponent. 

## Feature branches
Each feature needed for a new version is implemented in a separate feature 
branch. The naming convention is subcomponent-featureid, where the subcomponent
is one of the four defined above and the featureid can be obtained by the 
roadmap of the project. 
Inside a feature branch, everything that is needed for the feature to function
is written. This includes in particular

* Implementation
* Model documentation (changes in the DB in ER, etc.)
* User documentation
* Unit tests
* Integration tests (if possible)
* System tests

Only features that prove themselves verified to work properly are merged into 
the dev branch. 

## SOS branches
In case a severe bug is found that needs immediate fix the work is done of an
SOS branch of that component (e.g. sos-server or sos-android). The SOS branch
is checked out directly from master. 
The fix has to be scoped to the bug at hand. For merging back the same criteria
as for feature branches apply. The fix will be merged directly to master and
the patch version of the package is incremented. The last commit of the SOS
branch is tagged with the new version. 

## Merging cross-component features
The branches form a 3-layer tree hierarchy. master is the root of the tree and
has the component's dev branches as children. These components have the feature
branches as children. 
It is only allowed to merge from and to branches which are direct ancestors of
the branch at hand. This prevents features from passing by the QA of a component
by being introduced by a different component which accidentally merged premature
changes. 
