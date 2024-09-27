INSERT INTO users (id, username, password, role_id)
VALUES (1, 'user1', '$2a$12$UfSyv0l1ZuXAzHAjv16QruWq0ZFV2ZFVZ.CJGzbF70CgAfvP2U3Um', 1),
       (2, 'user2', '$2a$12$UfSyv0l1ZuXAzHAjv16QruWq0ZFV2ZFVZ.CJGzbF70CgAfvP2U3Um', 1)
        on conflict (username) do nothing;