FROM mysql/mysql-server

ADD  ./init_schema.sql /docker-entrypoint-initdb.d/