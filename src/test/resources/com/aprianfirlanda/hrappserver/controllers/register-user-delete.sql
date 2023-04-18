delete
from user_roles
where user_id = (select user_id
                 from users
                 where username = 'test-register-user');

delete
from users
where username = 'test-register-user';