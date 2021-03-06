SET TIME ZONE 'UTC';

DROP TABLE IF EXISTS kas_mq_users_to_groups;
DROP TABLE IF EXISTS kas_mq_users CASCADE;
DROP TABLE IF EXISTS kas_mq_groups CASCADE;
DROP TABLE IF EXISTS kas_mq_resource_classes;

CREATE TABLE kas_mq_users (
  id          SERIAL NOT NULL PRIMARY KEY,
  name        VARCHAR(20) NOT NULL UNIQUE,
  description VARCHAR(100),
  password    VARCHAR(50)
);

INSERT INTO kas_mq_users (name, description, password)
  VALUES('root', 'system root', 'root');



CREATE TABLE kas_mq_groups (
  id          SERIAL NOT NULL PRIMARY KEY,
  name        VARCHAR(20) NOT NULL UNIQUE,
  description VARCHAR(100)
);

INSERT INTO kas_mq_groups (name, description)
  VALUES('system', 'system');



CREATE TABLE kas_mq_users_to_groups (
  user_id     INT NOT NULL,
  group_id    INT NOT NULL,
  PRIMARY KEY(user_id, group_id),
  FOREIGN KEY(user_id)  REFERENCES kas_mq_users(id)  ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY(group_id) REFERENCES kas_mq_groups(id) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO kas_mq_users_to_groups
  SELECT user_id, group_id FROM 
  (
    SELECT id user_id
    FROM kas_mq_users
    WHERE name ='root'
  ) users,
  (
    SELECT id group_id
    FROM kas_mq_groups 
    WHERE name = 'system'
  ) groups;

CREATE TABLE kas_mq_resource_classes (
  id            SERIAL NOT NULL PRIMARY KEY,
  name          VARCHAR(32) NOT NULL UNIQUE,
  access_levels INT
);

INSERT INTO kas_mq_resource_classes
  VALUES('application', 1);
INSERT INTO kas_mq_resource_classes
  VALUES('queue', 7);
