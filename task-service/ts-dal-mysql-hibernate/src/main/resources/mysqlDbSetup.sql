SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS WordStats,PageText,Tasks;
SET FOREIGN_KEY_CHECKS=1;

CREATE TABLE Tasks
( id INT NOT NULL AUTO_INCREMENT,
  parentId INT,
  url VARCHAR(2000) NOT NULL,
  depth INT NOT NULL,
  external BOOL NOT NULL,
  startProcessTime LONG,
  endProcessTime LONG,
  errorCount INT,
  clientId VARCHAR(2000),
  PRIMARY KEY (id),
  UNIQUE (url) );
ALTER TABLE Tasks CONVERT TO CHARACTER SET utf8 COLLATE utf8_bin;

CREATE TABLE PageText
( task_id INT NOT NULL,
  text VARCHAR(20000) NOT NULL,
  PRIMARY KEY (task_id) );
ALTER TABLE PageText CONVERT TO CHARACTER SET utf8 COLLATE utf8_bin;

CREATE TABLE WordStats
( task_id INT NOT NULL,
  word VARCHAR(255),
  count LONG,
  PRIMARY KEY (task_id) );
ALTER TABLE WordStats CONVERT TO CHARACTER SET utf8 COLLATE utf8_bin;