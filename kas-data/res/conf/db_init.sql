DROP SCHEMA IF EXISTS @schema_name;
CREATE SCHEMA @schema_name;

DROP USER IF EXISTS '@user_name'@'%';
CREATE USER '@user_name'@'%' IDENTIFIED BY '@user_password';
GRANT ALL PRIVILEGES ON @schema_name.* TO '@<user_name'@'%';

USE @schema_name;

DROP TABLE IF EXISTS @schema_name.kas_mq_users;
CREATE TABLE @schema_name.kas_mq_users (
  user_name VARCHAR(20),
  password  VARCHAR(50),
  uuid      BINARY(16)
);

DROP TABLE IF EXISTS @schema_name.kas_mq_groups;
CREATE TABLE @schema_name.kas_mq_groups (
  group_name VARCHAR(20),
  uuid       BINARY(16)
);

DROP TABLE IF EXISTS @schema_name.kas_mq_users_to_groups;
CREATE TABLE @schema_name.kas_mq_users_to_groups (
  user_uuid  BINARY(16),
  group_uuid BINARY(16)
);

