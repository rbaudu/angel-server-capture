# Angel Server Capture

Serveur de capture, synchronisation et analyse d'activités en temps réel à partir de flux vidéo et audio.

## Présentation

Angel Server Capture est une application Spring Boot qui permet de capturer le flux vidéo d'une caméra et le flux audio d'un ordinateur Windows 11, de les synchroniser et de les afficher en temps réel. L'application intègre un système complet d'analyse d'activités pour détecter la présence de personnes et identifier les activités qu'elles réalisent (dormir, manger, lire, nettoyer, etc.).

## Fonctionnalités

- Capture vidéo depuis une caméra connectée
- Capture audio depuis le système
- Synchronisation des flux audio et vidéo
- Visualisation en temps réel des flux via WebSocket
- Interface utilisateur complète basée sur Thymeleaf, HTML, CSS et JavaScript
- API REST pour le contrôle des services de capture et l'accès aux résultats d'analyse
- **Analyse avancée d'activités**:
  - Détection de présence humaine
  - Classification d'activités: dormir, manger, lire, nettoyer, regarder la télévision, téléphoner, tricoter, parler, jouer
  - Fusion d'analyses audio et vidéo pour une détection plus précise
  - Lissage temporel pour éviter les fluctuations dans la détection

## Technologies utilisées

- **Backend** : Java 17, Spring Boot 3.2
- **Capture média** : JavaCV, OpenCV, FFmpeg
- **Analyse d'activités** : TensorFlow pour Java, TarsosDSP, DeepLearning4J
- **Frontend** : Thymeleaf, HTML5, CSS3, JavaScript, Bootstrap 5
- **Communication en temps réel** : WebSocket avec STOMP
- **Build** : Maven

## Prérequis

- Java 17+ 
- Maven 3.6+
- Une caméra connectée (webcam ou autre)
- Windows 11 avec accès au flux audio du système
- [OpenCV](https://opencv.org/) et [FFmpeg](https://ffmpeg.org/) (les dépendances sont gérées par Maven)
- Modèles TensorFlow pour l'analyse d'activités (à placer dans le dossier `src/main/resources/models/`)

## Installation et démarrage

1. Cloner le dépôt :
   ```bash
   git clone https://github.com/rbaudu/angel-server-capture.git
   cd angel-server-capture
   ```

2. Ajouter les modèles TensorFlow nécessaires dans les dossiers :
   ```
   src/main/resources/models/human_detection/
   src/main/resources/models/activity_recognition/
   src/main/resources/models/audio_classification/
   ```

3. Construire le projet avec Maven :
   ```bash
   mvn clean package
   ```

4. Lancer l'application :
   ```bash
   java -jar target/angel-server-capture-0.1.0-SNAPSHOT.jar
   ```

5. Accéder à l'interface web :
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
│   │   │               ├── analyzer/            # Module d'analyse d'activités
│   │   │               │   ├── config/          # Configuration de l'analyse
│   │   │               │   ├── controller/      # API REST pour l'analyse
│   │   │               │   ├── model/           # Modèles d'analyse
│   │   │               │   ├── service/         # Services d'analyse
│   │   │               │   │   ├── audio/       # Analyse audio
│   │   │               │   │   ├── fusion/      # Fusion multimodale
│   │   │               │   │   └── video/       # Analyse vidéo
│   │   │               │   └── util/            # Utilitaires d'analyse
│   │   │               ├── config/              # Configuration Spring Boot
│   │   │               ├── controller/          # Contrôleurs REST et WebSocket
│   │   │               ├── event/               # Classes d'événements
│   │   │               ├── model/               # Modèles de données
│   │   │               └── service/             # Services de capture et traitement
│   │   └── resources/
│   │       ├── models/                    # Modèles TensorFlow
│   │       │   ├── human_detection/       # Modèle de détection de présence
│   │       │   ├── activity_recognition/  # Modèle de reconnaissance d'activités
│   │       │   └── audio_classification/  # Modèle de classification audio
│   │       ├── static/                    # Ressources statiques (CSS, JS, images)
│   │       ├── templates/                 # Templates Thymeleaf
│   │       └── application.properties     # Configuration de l'application
│   └── test/                              # Tests unitaires et d'intégration
└── pom.xml                                # Configuration Maven
```

## Configuration

La configuration principale se trouve dans le fichier `src/main/resources/application.properties`. Voici les principaux paramètres :

### Capture vidéo et audio
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

### Analyse d'activités
- `angel.analyzer.humanDetectionModel` : Chemin vers le modèle de détection de présence humaine
- `angel.analyzer.activityRecognitionModel` : Chemin vers le modèle de reconnaissance d'activités
- `angel.analyzer.audioClassificationModel` : Chemin vers le modèle de classification audio
- `angel.analyzer.captureIntervalMs` : Intervalle de capture en millisecondes
- `angel.analyzer.presenceThreshold` : Seuil de confiance pour la détection de présence (entre 0.0 et 1.0)
- `angel.analyzer.activityConfidenceThreshold` : Seuil de confiance pour la classification d'activités (entre 0.0 et 1.0)
- `angel.analyzer.historySize` : Taille de l'historique pour le lissage temporel
- `angel.analyzer.audioAnalysisEnabled` : Active/désactive l'analyse audio

## Détection de présence humaine

### Modèles de détection de présence

Plusieurs approches sont disponibles pour la détection de présence humaine :

1. **Modèle MobileNet-SSD** (recommandé) :
   - Modèle léger et rapide, idéal pour les applications temps réel
   - Pré-entraîné sur le dataset COCO (classe 1 = "personne")
   - Peut être téléchargé depuis [TensorFlow Model Zoo](https://github.com/tensorflow/models/blob/master/research/object_detection/g3doc/tf2_detection_zoo.md)
   - Modèle recommandé : "SSD MobileNet v2 FPNLite 320x320"

2. **HOG (Histograms of Oriented Gradients) + SVM** :
   - Approche classique via OpenCV
   - Plus léger mais moins précis que les modèles de deep learning
   - Implémentation en fallback dans la classe `PresenceDetector`

3. **Cascade Classifier pour la détection de visages** :
   - Solution simple basée sur OpenCV
   - Utilise des classificateurs en cascade de type Haar
   - Fonctionne bien dans des conditions contrôlées

### Intégration du modèle

Pour installer un modèle de détection de présence humaine :

1. Téléchargez le modèle SSD MobileNet depuis TensorFlow Model Zoo
2. Décompressez-le dans `src/main/resources/models/human_detection/`
3. Configurez le chemin dans `application.properties` :

```properties
angel.analyzer.humanDetectionModel=models/human_detection/saved_model
```

### Personnalisation de la détection

Vous pouvez ajuster les paramètres de détection dans `application.properties` :

```properties
# Seuil de confiance pour la détection (entre 0.0 et 1.0)
angel.analyzer.presenceThreshold=0.5

# Intervalle de détection (en ms)
angel.analyzer.captureIntervalMs=1000
```

Un seuil plus élevé réduit les faux positifs mais peut manquer des détections réelles. Un seuil plus bas détecte plus facilement les personnes mais avec plus de faux positifs.

### Optimisation des performances

Pour améliorer les performances de la détection :

1. **Redimensionnement des images** : Traitez les images en résolution réduite (320x320)
2. **Mise en cache** : Analysez seulement une frame sur N (par exemple 1 sur 5)
3. **Solution de secours** : Utilisez HOG+SVM si TensorFlow ne peut pas être initialisé
4. **Lissage temporel** : Conservez les N derniers résultats et décidez par vote majoritaire

Ces optimisations sont implémentées dans la classe `PresenceDetector`.

## API REST pour l'analyse d'activités

Le module d'analyse d'activités expose une API REST permettant de consulter les résultats d'analyse et configurer les paramètres. Voici les principaux endpoints :

### Consultation des résultats d'analyse

#### Récupérer la dernière analyse
```
GET /api/analysis/latest
```
Retourne le résultat d'analyse le plus récent.

#### Récupérer les analyses récentes
```
GET /api/analysis/recent?limit=10
```
Retourne les N analyses les plus récentes (10 par défaut).

#### Filtrer par type d'activité
```
GET /api/analysis/activity/{activityType}
```
Retourne les analyses correspondant à un type d'activité spécifique.
Valeurs possibles pour `{activityType}` : 
- `ABSENT` : Personne absente
- `PRESENT_INACTIVE` : Personne présente mais inactive
- `SLEEPING` : Personne en train de dormir
- `EATING` : Personne en train de manger
- `READING` : Personne en train de lire
- `CLEANING` : Personne en train de nettoyer
- `WATCHING_TV` : Personne en train de regarder la télévision
- `CALLING` : Personne en train de téléphoner
- `KNITTING` : Personne en train de tricoter
- `TALKING` : Personne en train de parler
- `PLAYING` : Personne en train de jouer
- `UNKNOWN` : Activité inconnue

### Configuration de l'analyse

#### Récupérer la configuration actuelle
```
GET /api/analysis/config
```
Retourne la configuration actuelle du module d'analyse.

#### Mettre à jour la configuration
```
POST /api/analysis/config
Content-Type: application/json

{
  "audioAnalysisEnabled": true,
  "presenceThreshold": 0.6,
  "activityConfidenceThreshold": 0.7
}
```
Met à jour partiellement la configuration du module d'analyse.

### Test de la détection de présence

Vous pouvez tester la détection de présence avec l'endpoint suivant :

```
GET /api/analysis/test-detection
```

Retourne un résultat tel que :
```json
{
  "personDetected": true,
  "confidence": 0.89,
  "detectionTimeMs": 125
}
```

## Exemples d'utilisation de l'API

### Obtenir la dernière analyse
```bash
curl -X GET http://localhost:8080/api/analysis/latest
```
Réponse :
```json
{
  "timestamp": "2025-03-19T14:30:45.123",
  "activityType": "WATCHING_TV",
  "confidence": 0.89,
  "personPresent": true
}
```

### Obtenir les 5 dernières analyses
```bash
curl -X GET http://localhost:8080/api/analysis/recent?limit=5
```

### Filtrer les résultats pour une activité spécifique
```bash
curl -X GET http://localhost:8080/api/analysis/activity/SLEEPING
```

### Activer ou désactiver l'analyse audio
```bash
curl -X POST http://localhost:8080/api/analysis/config \
  -H "Content-Type: application/json" \
  -d '{"audioAnalysisEnabled": false}'
```

## Fonctionnement technique

Le module d'analyse fonctionne ainsi :

1. Le service de capture vidéo capture des images et les stocke dans des objets `VideoFrame` avec la matrice OpenCV pour l'analyse.
2. Le service de synchronisation combine les trames vidéo et les segments audio en objets `SynchronizedMedia`.
3. Quand un nouveau média synchronisé est créé, un événement est publié.
4. Le service d'analyse écoute ces événements et lance l'analyse :
   - Détection de présence humaine dans l'image
   - Classification d'activités basée sur l'analyse vidéo
   - Détection de patterns audio (si l'analyse audio est activée)
   - Fusion multimodale des résultats audio et vidéo
   - Lissage temporel pour stabiliser les détections
5. Les résultats sont stockés dans les objets média et publiés via l'API REST.

## Dépannage et conseils de développement

### Problèmes de dépendances

Si vous rencontrez des erreurs liées aux dépendances TensorFlow ou TarsosDSP :

1. Pour TarsosDSP, utilisez cette dépendance via JitPack :
   ```xml
   <dependency>
       <groupId>com.github.axet</groupId>
       <artifactId>TarsosDSP</artifactId>
       <version>2.4</version>
   </dependency>
   ```

2. Pour javax.annotation.PostConstruct :
   ```xml
   <dependency>
       <groupId>javax.annotation</groupId>
       <artifactId>javax.annotation-api</artifactId>
       <version>1.3.2</version>
   </dependency>
   ```

3. Ajoutez le référentiel JitPack :
   ```xml
   <repositories>
       <repository>
           <id>jitpack.io</id>
           <url>https://jitpack.io</url>
       </repository>
   </repositories>
   ```

### Optimisation des performances

- Réduisez la résolution des images avant l'analyse
- Utilisez un système de cache pour éviter d'analyser chaque frame
- Implémentez un fallback vers des méthodes plus simples si les modèles TensorFlow ne peuvent pas être chargés
- Utilisez le lissage temporel pour éviter les changements brusques dans les détections

## Licence

Ce projet est sous licence MIT. Voir le fichier LICENSE pour plus de détails.

## Contact

Pour toute question ou suggestion, veuillez ouvrir une issue sur ce dépôt GitHub.
