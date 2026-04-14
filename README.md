# Wolfgang

Concept : édition collaborative de documents musicaux style fichiers MIDI, pas forcément très 
complexe mais au moins avec les features de base (et on verra ensuite)

Gestion d'utilisateurs, relations, listes de musiques, etc...

## Configuration
### IDE
Pas de config particulière en théorie, vu que tous les services tournent via Docker,
ça devrait être indépendant (mais si tt le monde se met pas sur le goatesque IntelliJ)
faudra qd mm que je check Eclipse

### Docker
En gros ça permet de faire tourner les services dont on a besoin (mysql, phpmyadmin,
potiellement autres ?) dans des conteneurs plutôt que "directement" sur la machine. 
Faut voir ça un peu comme des VM mais qui font tourner qu'un truc. Toute la config se 
gère dans `docker-compose.yml`, donc c'est indépendant de la config de la machine. Le 
projet marche sur n'importe quel poste directement, pas besoin de s'amuser à config 
mysql etc sur chacun.

Oubliez pas d'installer docker avant, évidemment, [cf la doc](https://docs.docker.com/engine/install/).

Pour lancer les conteneurs : `docker compose up -d`  
Pour fermer les conteneurs : `docker compose down`  
Pour voir les conteneurs qui tournent : `docker ps`

### Rafraichir le projet
Comme tomcat est dans docker, les modifs sont pas automatiquement prises
en compte, du coup j'ai fait un mini script `reload.sh` qu'il suffit de mettre
en cible sur votre IntelliJ, et vous pourrez rafraîchir le projet juste en 1 clic