CREATE DATABASE keycloak_db;
CREATE USER keycloak WITH ENCRYPTED PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE keycloak_db TO keycloak;

\c keycloak_db
GRANT ALL ON SCHEMA public TO keycloak;


\c postgres

CREATE DATABASE user_db;

CREATE DATABASE order_db;

CREATE DATABASE payment_db;