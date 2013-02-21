# Users schema

# --- !Ups

CREATE TABLE users(
  id varchar(254),
  creationDate timestamp without time zone,
  updateDate timestamp without time zone,
  status varchar(254),
  prename varchar(254),
  lastname varchar(254),
  email varchar(254),
  password varchar(254),
  phone varchar(254),
  PRIMARY KEY (id)
)

# --- !Downs
drop table users;