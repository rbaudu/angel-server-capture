# Angel Server Capture

Serveur de capture, synchronisation et analyse de flux vidéo et audio en temps réel.

## Présentation

Angel Server Capture est une application Spring Boot qui permet de capturer le flux vidéo d'une caméra et le flux audio d'un ordinateur Windows 11, de les synchroniser et de les afficher en temps réel. L'application inclut également des fonctionnalités d'analyse pour détecter la présence de personnes, les mouvements et les positions.

## Fonctionnalités

- Capture vidéo depuis une caméra connectée
- Capture audio depuis le système
- Synchronisation des flux audio et vidéo
- Visualisation en temps réel des flux via WebSocket
- Interface utilisateur complète basée sur Thymeleaf, HTML, CSS et JavaScript
- API REST pour le contrôle des services de capture
- Analyse de base pour la détection de mouvement
- Architecture extensible pour ajouter des fonctionnalités d'analyse avancées

## Technologies utilisées

- **Backend** : Java 17, Spring Boot 3.2
- **Capture média** : JavaCV, OpenCV, FFmpeg
- **Frontend** : Thymeleaf, HTML5, CSS3, JavaScript, Bootstrap 5
- **Communication en temps réel** : WebSocket avec STOMP
- **Build** : Maven

## Prérequis

- Java 17+ 
- Maven 3.6+
- Une caméra connectée (webcam ou autre)
- Windows 11 avec accès au flux audio du système
- [OpenCV](https://opencv.org/) et [FFmpeg](https://ffmpeg.org/) (les dépendances sont gérées par Maven)

## Installation et démarrage

1. Cloner le dépôt :
   ```bash
   git clone https://github.com/rbaudu/angel-server-capture.git
   cd angel-server-capture
   ```

2. Construire le projet avec Maven :
   ```bash
   mvn clean package
   ```

3. Lancer l'application :
   ```bash
   java -jar target/angel-server-capture-0.1.0-SNAPSHOT.jar
   ```

4. Accéder à l'interface web :
   ```
   http://localhost:8080/angel/
   ```

## Structure du projet

```
angel-server-capture/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── rbaudu/
│   │   │           └── angel/
│   │   │               ├── config/        # Configuration Spring Boot
│   │   │               ├── controller/    # Contrôleurs REST et WebSocket
│   │   │               ├── event/         # Classes d'événements
│   │   │               ├── model/         # Modèles de données
│   │   │               └── service/       # Services de capture et traitement
│   │   └── resources/
│   │       ├── static/              # Ressources statiques (CSS, JS, images)
│   │       ├── templates/           # Templates Thymeleaf
│   │       └── application.properties # Configuration de l'application
│   └── test/                       # Tests unitaires et d'intégration
└── pom.xml                        # Configuration Maven
```

## Configuration

La configuration principale se trouve dans le fichier `src/main/resources/application.properties`. Voici les principaux paramètres :

- `angel.capture.video.enabled` : Active/désactive la capture vidéo
- `angel.capture.video.camera-index` : Index de la caméra à utiliser (0 pour la caméra par défaut)
- `angel.capture.video.width` : Largeur de la capture vidéo
- `angel.capture.video.height` : Hauteur de la capture vidéo
- `angel.capture.video.fps` : Images par seconde pour la vidéo

- `angel.capture.audio.enabled` : Active/désactive la capture audio
- `angel.capture.audio.device-index` : Index du périphérique audio à utiliser
- `angel.capture.audio.sample-rate` : Taux d'échantillonnage audio
- `angel.capture.audio.channels` : Nombre de canaux audio

- `angel.sync.buffer-size` : Taille du buffer de synchronisation
- `angel.sync.max-delay-ms` : Délai maximum de synchronisation en millisecondes

- `angel.analysis.enabled` : Active/désactive l'analyse
- `angel.analysis.motion-detection` : Active/désactive la détection de mouvement
- `angel.analysis.person-detection` : Active/désactive la détection de personnes
- `angel.analysis.position-tracking` : Active/désactive le suivi de position

## Développement futur

Ce projet est actuellement en phase initiale et se concentre sur la capture, la synchronisation et l'affichage des flux. Les prochaines étapes de développement incluront :

1. Amélioration des algorithmes de détection de personnes et de suivi de position
2. Ajout de fonctionnalités d'enregistrement des flux
3. Support pour plusieurs caméras et sources audio
4. Interface de configuration plus complète
5. Analyses avancées avec intelligence artificielle

## Licence

Ce projet est sous licence MIT. Voir le fichier LICENSE pour plus de détails.

## Contact

Pour toute question ou suggestion, veuillez ouvrir une issue sur ce dépôt GitHub.