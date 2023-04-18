delete
from user_roles
where user_id = (select user_id
                 from users
                 where username = 'test-user');

delete
from users
where username = 'test-user';