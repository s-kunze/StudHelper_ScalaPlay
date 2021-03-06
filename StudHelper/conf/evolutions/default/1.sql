# Studhelper schema
 
# --- !Ups

CREATE TABLE Admin(
  id          BIGINT NOT NULL AUTO_INCREMENT,
  username    VARCHAR(255) NOT NULL,
  password    VARCHAR(255) NOT NULL,
  PRIMARY KEY (ID)
);

CREATE TABLE University(
  id      BIGINT NOT NULL AUTO_INCREMENT,
  name    VARCHAR(255) NOT NULL,
  PRIMARY KEY (ID) 
);

CREATE TABLE Department(
  id            BIGINT NOT NULL AUTO_INCREMENT,
  name          VARCHAR(255) NOT NULL,
  universityId  BIGINT,
  PRIMARY KEY (ID)
);

CREATE TABLE DegreeCourse(
  id              LONG NOT NULL AUTO_INCREMENT,
  name            VARCHAR(255) NOT NULL,
  creditPoints    INTEGER,
  departmentId    LONG,
  PRIMARY KEY (ID) 
);

# --- !Downs

DROP TABLE IF EXISTS Admin;
DROP TABLE IF EXISTS University;
DROP TABLE IF EXISTS Department;
DROP TABLE IF EXISTS DegreeCourse;