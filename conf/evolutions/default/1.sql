# Creates PARTAKE configuration table.

# --- !Ups

CREATE TABLE ConfigurationItems (
    key     TEXT        PRIMARY KEY,
    value   TEXT        NOT NULL
);

# --- !Downs

DROP TABLE ConfigurationItems;
