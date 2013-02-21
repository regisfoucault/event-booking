# Userevents schema

# --- !Ups

CREATE TABLE userevents(
  userid varchar(254),
  eventid varchar(254),
  PRIMARY KEY (userid)
)

# --- !Downs
drop table userevents;