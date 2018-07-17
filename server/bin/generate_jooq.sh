#!/bin/bash

MAVEN_REPO=~/.m2/repository/

java -classpath "$MAVEN_REPO/org/jooq/jooq/3.11.3/jooq-3.11.3.jar:$MAVEN_REPO/org/jooq/jooq-meta/3.11.3/jooq-meta-3.11.3.jar:$MAVEN_REPO/org/jooq/jooq-codegen/3.11.3/jooq-codegen-3.11.3.jar:$MAVEN_REPO/org/mariadb/jdbc/mariadb-java-client/1.6.5/mariadb-java-client-1.6.5.jar:." \
        org.jooq.codegen.GenerationTool library.xml
