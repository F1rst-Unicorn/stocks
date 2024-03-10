# Git Workflow for this project

The programming tasks in the project have to be done on dedicated branches.
Eventually features are merged into the mainstream branches. The exact tasks for
the branches follow.

## Branch `master`

This branch is the production branch. Any code on this branch has to be
deployable as is on a new system. It contains the latest stable version of every
of the three subcomponents (defined below).

## Feature branches

Each feature needed for a new version is implemented in a separate feature
branch. The naming convention is the ticket number in the YouTrack Ticket
System (access on request).
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

## License

Copyright (C)  2019  The stocks developers

Permission is granted to copy, distribute and/or modify this document
under the terms of the GNU Free Documentation License, Version 1.3
or any later version published by the Free Software Foundation;
with no Invariant Sections, no Front-Cover Texts, and no Back-Cover Texts.
A copy of the license is included in the section entitled "GNU
Free Documentation License".