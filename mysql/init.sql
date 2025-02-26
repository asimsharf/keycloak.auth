-- Grant all privileges to keycloak user on all databases
ALTER USER 'keycloak'@'%' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON *.* TO 'keycloak'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;
