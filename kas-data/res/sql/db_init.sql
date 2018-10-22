DROP USER IF EXISTS '<user_name>'@'%';
CREATE USER '<user_name>'@'%' IDENTIFIED BY '<user_password>';
GRANT ALL PRIVILIGES ON <schema_name>.* TO '<user_name>'@'%';

DROP SCHEMA IF EXISTS <schema_name>;
CREATE SCHEMA <schema_name>;

DROP TABLE IF EXISTS kas.mq.users;
CREATE TABLE kas.mq.users (
  user_name VARCHAR(20),
  password  VARCHAR(50),
  uuid      BINARY[16]
);

DROP TABLE IF EXISTS kas.mq.groups;
CREATE TABLE kas.mq.users (
  group_name VARCHAR(20),
  uuid       BINARY[16]
);

DROP TABLE IF EXISTS kas.mq.users.to.groups;
CREATE TABLE kas.mq.users (
  user_uuid  BINARY[16],
  group_uuid BINARY[16]
);

