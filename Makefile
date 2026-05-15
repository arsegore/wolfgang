MYSQL_CONTAINER = wolfgang-mysql
MYSQL_USER      = dev
MYSQL_PASSWORD  = dev
MYSQL_DB        = wolfgang_db
DOCKER_COMPOSE  = docker compose # de mon coté c'est docker-compose, chez vous c peut etre "docker compose" donc si ça marche pas ça vient de là

MYSQL_EXEC = docker exec -i $(MYSQL_CONTAINER) mysql -u$(MYSQL_USER) -p$(MYSQL_PASSWORD) $(MYSQL_DB)

clean-bdd:
	$(MYSQL_EXEC) < src/main/resources/clean.sql

load-bdd:
	$(MYSQL_EXEC) < src/main/resources/schema.sql

load-fixtures:
	$(MYSQL_EXEC) < src/main/resources/fixtures.sql

reload-bdd: clean-bdd load-bdd load-fixtures

start-project:
	./mvnw package && $(DOCKER_COMPOSE) up -d

stop-project:
	$(DOCKER_COMPOSE) down

reload-project:
	./mvnw package && $(DOCKER_COMPOSE) restart tomcat
