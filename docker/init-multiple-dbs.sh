#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE DATABASE nexus_attribute;
    CREATE DATABASE nexus_catalog;
    CREATE DATABASE nexus_taxonomy;
    CREATE DATABASE nexus_content;
    CREATE DATABASE nexus_media;
    CREATE DATABASE nexus_workflow;
    CREATE DATABASE nexus_search;
EOSQL
