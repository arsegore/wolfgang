# Architecture du code

## Organisation des fichiers

```
wolfgang/
├── docker-compose.yml          # Configuration de Docker (pour l'instant juste MySQL, PhpMyAdmin et Tomcat)
├── pom.xml                     # Config maven du projet
├── reload.sh                   # Script pour mettre à jour le déploiement du projet (pr rappel il faut le relancer après chaque changement en dev)
│
├── docs/                       # Documentation.s     
│
└── src/main/
    ├── java/wolfgang/
    │   ├── config/             # Config de l'appli (pour l'instant juste la BDD)
    │   ├── entities/           # Objets accueillant les données de la BDD
    │   ├── repositories/       # Classes qui font le pont entre l'appli et la BDD (s'occupent de récupérer les infos dans la bdd pour créer des objets)
    │   ├── servlets/           # Les servlets (c assez parlant comme ça je pense)
    │   └── utils/              # Des classes pratiques, éventuellement à plusieurs endroits différents
    │
    ├── resources/              # Ressources de l'appli
    │   ├── schema.sql          # Structure de la BDD
    │   └── clean.sql           # Remise à zéro de la BDD
    │
    └── webapp/                 # Racine publique
        ├── css/                # Les feuilles de style (on dépassera pas 2 normalement)
        │   ├── pico.min.css    # Pico CSS (framework, très minimaliste)
        │   └── wolfgang.css    # Pour les styles qu'on veut customiser au delà de Pico
        └── WEB-INF/            # Contient les JSP
            ├── web.xml         # Fichier de config
            └── include/        # JSP non complètes, à inclure dans d'autres JSP
            
        ^ Ici y aura à l'avenir un répertoire js/
```
**Remarques :** 
* Toutes les requêtes SQL doivent être dans un repository. Pour une entité X, on créé une classe 
XRepository, avec **une méthode par requête**.
* Bien séparer les entités des repositories, d'ailleurs c'est simple on ne met pas de méthode autre que les
    getters & setters dans les entités. Rien d'autre que des reqûetes SQL dans les repositories non plus. Si
    besoin de méthodes particulières qui interagissent sur des entités (j'ai pas d'exemple en tête mais ça risque)
    d'arriver, on crééra des XManager
* Faites en sorte d'éviter d'écrire des méthodes trop longues, souvent on peut redécouper (tfaçon vous verrez que 
  je risque d'avoir tendance à repasser sur le code)