USE ${kas.db.schema};

DROP TABLE IF EXISTS kas_mq_command_permissions;
DROP TABLE IF EXISTS kas_mq_application_permissions;
DROP TABLE IF EXISTS kas_mq_queue_permissions;

DROP TABLE IF EXISTS kas_mq_users_to_groups;
DROP TABLE IF EXISTS kas_mq_users CASCADE;
DROP TABLE IF EXISTS kas_mq_groups CASCADE;

DROP TABLE IF EXISTS kas_mq_parameters;


--
-- parameters
--
CREATE TABLE kas_mq_parameters (
  param_name  VARCHAR(100) NOT NULL UNIQUE PRIMARY KEY,
  param_value VARCHAR(100)
);

INSERT INTO kas_mq_parameters (param_name, param_value)
  VALUES('schema_version', '1');

--
-- users
--
CREATE TABLE kas_mq_users (
  id          INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name        VARCHAR(20) NOT NULL UNIQUE,
  description VARCHAR(100),
  password    VARCHAR(50)
);

INSERT INTO kas_mq_users (name, description, password)
  VALUES('root', 'system root', 'root');
  
INSERT INTO kas_mq_users (name, description, password)
  VALUES('oper', 'system operator', 'oper');
  
INSERT INTO kas_mq_users (name, description, password)
  VALUES('guest', 'guest user', 'guest');

--
-- groups
--
CREATE TABLE kas_mq_groups (
  id          INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name        VARCHAR(20) NOT NULL UNIQUE,
  description VARCHAR(100)
);

INSERT INTO kas_mq_groups (name, description)
  VALUES('administrators', 'administrators');
  
INSERT INTO kas_mq_groups (name, description)
  VALUES('moderators', 'moderators');

--
-- group assignment
--
CREATE TABLE kas_mq_users_to_groups (
  user_id     INT NOT NULL,
  group_id    INT NOT NULL,
  PRIMARY KEY(user_id, group_id),
  FOREIGN KEY fk_user_id(user_id)   REFERENCES kas_mq_users(id)  ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY fk_group_id(group_id) REFERENCES kas_mq_groups(id) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO kas_mq_users_to_groups
  SELECT user_id, group_id FROM 
  (
    SELECT id user_id
    FROM kas_mq_users
    WHERE name = 'root'
  ) users,
  (
    SELECT id group_id
    FROM kas_mq_groups 
    WHERE name = 'administrators'
  ) groups;
  
INSERT INTO kas_mq_users_to_groups
  SELECT user_id, group_id FROM 
  (
    SELECT id user_id
    FROM kas_mq_users
    WHERE name = 'oper'
  ) users,
  (
    SELECT id group_id
    FROM kas_mq_groups 
    WHERE name = 'moderators'
  ) groups;

--
-- command permissions:
--   administrators and moderators can issue all commands
--
CREATE TABLE kas_mq_command_permissions (
  pattern      VARCHAR(100) NOT NULL,
  group_id     INT NOT NULL,
  access_level INT NOT NULL,
  PRIMARY KEY(pattern, group_id),
  FOREIGN KEY fk_group_id(group_id) REFERENCES kas_mq_groups(id) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO kas_mq_command_permissions (pattern, group_id, access_level)
  SELECT '.*', id, 1
  FROM   kas_mq_groups
  WHERE name = 'administrators';

INSERT INTO kas_mq_command_permissions (pattern, group_id, access_level)
  SELECT '.*', id, 1
  FROM   kas_mq_groups
  WHERE name = 'moderators';

--
-- application permissions:
--   administrators can login from all applications
--   moderators can login from all KAS applications
--
CREATE TABLE kas_mq_application_permissions (
  pattern      VARCHAR(100) NOT NULL,
  group_id     INT NOT NULL,
  access_level INT NOT NULL,
  PRIMARY KEY(pattern, group_id),
  FOREIGN KEY fk_group_id(group_id) REFERENCES kas_mq_groups(id) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO kas_mq_application_permissions (pattern, group_id, access_level)
  SELECT '.*', id, 1
  FROM   kas_mq_groups
  WHERE name = 'administrators';

INSERT INTO kas_mq_application_permissions (pattern, group_id, access_level)
  SELECT 'kas.*', id, 1
  FROM   kas_mq_groups
  WHERE name = 'moderators';

--
-- queue permissions:
--   administrators can perform all actions against all queues
--   moderators can alter all queues
--
CREATE TABLE kas_mq_queue_permissions (
  pattern      VARCHAR(100) NOT NULL,
  group_id     INT NOT NULL,
  access_level INT NOT NULL,
  PRIMARY KEY(pattern, group_id),
  FOREIGN KEY fk_group_id(group_id) REFERENCES kas_mq_groups(id) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO kas_mq_queue_permissions (pattern, group_id, access_level)
  SELECT '.*', id, 7
  FROM   kas_mq_groups
  WHERE name = 'administrators';

INSERT INTO kas_mq_queue_permissions (pattern, group_id, access_level)
  SELECT '.*', id, 4
  FROM   kas_mq_groups
  WHERE name = 'moderators';
