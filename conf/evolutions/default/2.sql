# Events schema

# --- !Ups

CREATE TABLE events(
  id varchar(254),
  creationDate timestamp without time zone,
  updateDate timestamp without time zone,
  creatorid varchar(254),
  name varchar(254),
  date varchar(254),
  nbreplaces varchar(254),
  description varchar(254),
  PRIMARY KEY (id)
)

# --- !Downs
drop table events;