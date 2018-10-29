USE ${kas.db.schema};

DROP TABLE IF EXISTS kas_mq_users CASCADE;
CREATE TABLE kas_mq_users (
  id          INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
  name        VARCHAR(20) NOT NULL UNIQUE,
  description VARCHAR(100),
  password    VARCHAR(50)
);

INSERT INTO kas_mq_users (name, description, password)
  VALUES('root', 'system root', 'root');

DROP TABLE IF EXISTS kas_mq_groups CASCADE;
CREATE TABLE kas_mq_groups (
  id          INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
  name        VARCHAR(20) NOT NULL UNIQUE,
  description VARCHAR(100)
);

INSERT INTO kas_mq_groups (name, description)
  VALUES('system', 'system');

DROP TABLE IF EXISTS kas_mq_users_to_groups;
CREATE TABLE kas_mq_users_to_groups (
  user_id     INT NOT NULL,
  group_id    INT NOT NULL,
  PRIMARY KEY(user_id, group_id),
  FOREIGN KEY fk_user_id(user_id)   REFERENCES kas_mq_users(id)  ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY fk_group_id(group_id) REFERENCES kas_mq_groups(id) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO kas_mq_users_to_groups
SELECT user_id, group_id FROM 
  (SELECT id user_id
   FROM kas_mq_users
   WHERE name ='root') users,
  (SELECT id group_id
   FROM kas_mq_groups 
   WHERE name = 'system') groups;
