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
  VALUES('schema_version', '2');

--
-- users
--
CREATE TABLE kas_mq_users (
  user_id          INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_name        VARCHAR(20) NOT NULL UNIQUE,
  user_description VARCHAR(100),
  user_password    VARCHAR(50)
);

INSERT INTO kas_mq_users (user_name, user_description, user_password)
  VALUES('root', 'system root', 'root');
  
INSERT INTO kas_mq_users (user_name, user_description, user_password)
  VALUES('oper', 'system operator', 'oper');
  
INSERT INTO kas_mq_users (user_name, user_description, user_password)
  VALUES('guest', 'guest user', 'guest');

--
-- groups
--
CREATE TABLE kas_mq_groups (
  group_id          INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  group_name        VARCHAR(20) NOT NULL UNIQUE,
  group_description VARCHAR(100)
);

INSERT INTO kas_mq_groups (group_name, group_description)
  VALUES('administrators', 'administrators');
  
INSERT INTO kas_mq_groups (group_name, group_description)
  VALUES('moderators', 'moderators');

--
-- group assignment
--
CREATE TABLE kas_mq_users_to_groups (
  user_id     INT NOT NULL,
  group_id    INT NOT NULL,
  PRIMARY KEY(user_id, group_id),
  FOREIGN KEY fk_user_id(user_id)   REFERENCES kas_mq_users(user_id)   ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY fk_group_id(group_id) REFERENCES kas_mq_groups(group_id) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO kas_mq_users_to_groups
  SELECT user_id, group_id FROM 
  (
    SELECT user_id
    FROM kas_mq_users
    WHERE user_name = 'root'
  ) users,
  (
    SELECT group_id
    FROM kas_mq_groups 
    WHERE group_name = 'administrators'
  ) groups;
  
INSERT INTO kas_mq_users_to_groups
  SELECT user_id, group_id FROM 
  (
    SELECT user_id
    FROM kas_mq_users
    WHERE user_name = 'oper'
  ) users,
  (
    SELECT group_id
    FROM kas_mq_groups 
    WHERE group_name = 'moderators'
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
  FOREIGN KEY fk_group_id(group_id) REFERENCES kas_mq_groups(group_id) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO kas_mq_command_permissions (pattern, group_id, access_level)
  SELECT '.*', group_id, 1
  FROM   kas_mq_groups
  WHERE group_name = 'administrators';

INSERT INTO kas_mq_command_permissions (pattern, group_id, access_level)
  SELECT '.*', group_id, 1
  FROM   kas_mq_groups
  WHERE group_name = 'moderators';

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
  FOREIGN KEY fk_group_id(group_id) REFERENCES kas_mq_groups(group_id) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO kas_mq_application_permissions (pattern, group_id, access_level)
  SELECT '.*', group_id, 1
  FROM   kas_mq_groups
  WHERE group_name = 'administrators';

INSERT INTO kas_mq_application_permissions (pattern, group_id, access_level)
  SELECT 'KAS.*', group_id, 1
  FROM   kas_mq_groups
  WHERE group_name = 'moderators';

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
  FOREIGN KEY fk_group_id(group_id) REFERENCES kas_mq_groups(group_id) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO kas_mq_queue_permissions (pattern, group_id, access_level)
  SELECT '.*', group_id, 7
  FROM   kas_mq_groups
  WHERE group_name = 'administrators';

INSERT INTO kas_mq_queue_permissions (pattern, group_id, access_level)
  SELECT '.*', group_id, 4
  FROM   kas_mq_groups
  WHERE group_name = 'moderators';
