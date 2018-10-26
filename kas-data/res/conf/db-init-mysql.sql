DROP TABLE IF EXISTS ${kas.mq.db.schema}.kas_mq_users;
CREATE TABLE ${kas.mq.db.schema}.kas_mq_users (
  id          INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
  name        VARCHAR(20) NOT NULL UNIQUE,
  description VARCHAR(100),
  password    VARCHAR(50)
);

INSERT INTO ${kas.mq.db.schema}.kas_mq_users (name, description, password)
  VALUES('root', 'system root', 'root');

DROP TABLE IF EXISTS ${kas.mq.db.schema}.kas_mq_groups;
CREATE TABLE ${kas.mq.db.schema}.kas_mq_groups (
  id          INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
  name        VARCHAR(20) NOT NULL UNIQUE,
  description VARCHAR(100)
);

INSERT INTO ${kas.mq.db.schema}.kas_mq_groups (name, description)
  VALUES('system', 'system');

DROP TABLE IF EXISTS ${kas.mq.db.schema}.kas_mq_users_to_groups;
CREATE TABLE ${kas.mq.db.schema}.kas_mq_users_to_groups (
  user_id     INT NOT NULL,
  group_id    INT NOT NULL,
  PRIMARY KEY(user_id, group_id)
);

INSERT INTO ${kas.mq.db.schema}.kas_mq_users_to_groups (user_id, group_id)
  VALUES (1, 1);
