# Projet Custodia

## Document technique de travail

### Messages

Pour que les différentes parties communiquent, il est necessaire d'envoyer des messages dans un format spécifique, de façon à ce que le serveur de répétition les envoie aux bons élements. Ces messages transitent via des socket Java (sockets TCP usuellement). La connexion est réalisée ainsi:

``` JSON
{
    type: "init",
    clientType: "Robotino" | "ArduinoRobotino" | "Java" | "Webcam" | "sensorDatabase" | "sensors"
}

```

Ce message d'initialisation permet d'instancier le bon type de connexion selon le type de client. Un `Robotino` doit forcément être traité différemment d'un `Webcam`. Le choix est réalisé dans `ServerRobotino.java` du `JavaRepeatServer`. Chaque type de connexion possède son propre fichier d'implémentation `ConnexionTYPE.java` où `TYPE` est le type de client.

Le serveur de répétition support aussi les connexions HTTP afin de s'interfacer avec des clients web ou des flux video.

#### Types de connexion:

Après le message intial, réponse serveur:

``` JSON

{
    type: "init",
    infoInit: "Connexion accepte"
}

```

**Robotino:**

Types de messages valides à envoyer à un robotino

``` JSON

{
    type: "message",
    message: "Something"
}

```

``` JSON

{
    type: "video",
    link: "Some http link to send web clients for camera streaming"
}

```

**ArduinoRobotino:**

Types de message valide à envoyer à un Arduino:

``` JSON

{
    type: "command",
    commande: "Some string" 
}

```

**Java:**

``` JSON

{
    type: "message",
    message: "Something"
}

```

**Webcam:**

``` JSON

{
    ip: "127.0.0.1", // The ip of your streaming device (a webcam for example)
    port: "50009", // The port on which your webcam stream is available
    clientName: "My Webcam", // A friendly name for client (optional)
}

```

Le serveur va ensuite initier une connexion HTTP vers le couple ip/port donné par le message et redistribuer le stream (HTTP multipart) aux client web qui le demandent. En interne, cela se fait par l'instanciation d'une classe `ConnexionFluxWebcam`.

**SensorDatabase:**

``` JSON

{
    type: "message",
    message: "Something"
}

```

**sensors:**

Il s'agit en général d'une requête HTTP, celle ci est redistribuée tel quel aux client web et aux client sensorDatabase.

**Web:**

Le type connexion Web est automatiquement choisi si la connexion commence par `GET:`. Il s'agit du type le plus complet:

``` JSON

{
    type: "message",
    message: "Something"
}

```

Le message suivant est une commande envoyée à un robot spécifique

``` JSON

{
    type: "command",
    robot:
    {
        ip: "127.0.0.0" // The ip of the robot
    }
    // Some other JSON that will be send as-is to the robot
}

```

Le message suivant est une (autre) commande envoyée à un robot spécifique

``` JSON

{
    type: "auto",
    robot:
    {
        ip: "127.0.0.0" // The ip of the robot
    }
    // Some other JSON that will be send as-is to the robot
}

```

Le message suivant est une commande envoyée à tous les robots connectés

``` JSON

{
    type: "commandAll"
    // Some other JSON that will be send as-is to all robots
}

```

Le message suivant est une commande d'attache envoyé à tous les robots. Elle indique que le nom `name` est assigné et peut être utilisé pour un flux webcam.

``` JSON

{
    type: "nameToPort",
    name: "some name"
    // Some other JSON that will be send as-is to the robot
}

```

#### Type "message"

Le type `message` est un type "de base", il signifie simplement que le serveur de répétition va répéter le message tel quel à tous les clients conditionné par le type d'init selon la règle suivante:

```

ArduinoRobotino --> Robot
Robotino --> tous
Java --> tous
Web:
    message --> tous
    command --> robot spéciique
    auto --> robot spécfique
    commandAll --> tous les robots
    nameToPort --> tous les robots

```

Cette règle est modifiable dans les fichier d'implémentation de chaque connexion.
