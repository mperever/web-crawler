SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS Users;
SET FOREIGN_KEY_CHECKS=1;

CREATE TABLE Users
( name VARCHAR(50) NOT NULL,
  password VARCHAR(200) NOT NULL,
  role VARCHAR(50) NOT NULL,
  PRIMARY KEY (name) );
ALTER TABLE Users;