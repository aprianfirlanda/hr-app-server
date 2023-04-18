INSERT INTO users (id, username, email, password)
VALUES ('f8987732-cf26-430f-9d03-1d3149846821', 'test-login-user', 'test.login.user@email.com', '$2a$12$5VT2lzChvtB2RQuHWgm4NOkATuIRbhEMJhYFmb5g9MU5lBqPi89FW');

INSERT INTO user_roles (role_id, user_id)
VALUES ('fc1ed01c-9c5d-4723-9db4-a70e64ec8e71', 'f8987732-cf26-430f-9d03-1d3149846821');