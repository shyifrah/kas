INSERT INTO kas_mq_users (name, description, password)
VALUES ('root', 'root', 'root');

INSERT INTO kas_mq_users (name, description, password)
VALUES ('shy', 'shy ifrah', 'shy');

INSERT INTO kas_mq_users (name, description, password)
VALUES ('dean', 'dean winchester', 'dean');

INSERT INTO kas_mq_users (name, description, password)
VALUES ('sam', 'sam winchester', 'sam');

INSERT INTO kas_mq_users (name, description, password)
VALUES ('bobby', 'bobby singer', 'bobby');



INSERT INTO kas_mq_groups (name, description)
VALUES ('root', 'root');

INSERT INTO kas_mq_groups (name, description)
VALUES ('winchesters', 'winchester family');

INSERT INTO kas_mq_groups (name, description)
VALUES ('hunters', 'demon hunters');



INSERT INTO kas_mq_users_to_groups (user_id, group_id)
VALUES (1, 1);

INSERT INTO kas_mq_users_to_groups (user_id, group_id)
VALUES (3, 2);

INSERT INTO kas_mq_users_to_groups (user_id, group_id)
VALUES (3, 3);

INSERT INTO kas_mq_users_to_groups (user_id, group_id)
VALUES (4, 2);

INSERT INTO kas_mq_users_to_groups (user_id, group_id)
VALUES (4, 3);

INSERT INTO kas_mq_users_to_groups (user_id, group_id)
VALUES (5, 3);
