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

## Modèles d'analyse d'activités

### Détection de présence humaine

#### Modèles de détection de présence

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

#### Intégration du modèle

Pour installer un modèle de détection de présence humaine :

1. Téléchargez le modèle SSD MobileNet depuis TensorFlow Model Zoo
2. Décompressez-le dans `src/main/resources/models/human_detection/`
3. Configurez le chemin dans `application.properties` :

```properties
angel.analyzer.humanDetectionModel=models/human_detection/saved_model
```

### Classification d'activités vidéo

La classe `VisualActivityClassifier` nécessite un modèle TensorFlow pour la classification d'activités visuelles. Voici comment créer et intégrer ce modèle :

#### Création d'un modèle de classification d'activités vidéo

1. **Préparation des données**
   - Extraire des images à partir de vos vidéos MP4
   - Organiser les images par catégorie d'activités
   - Diviser les données en ensembles d'entraînement, validation et test

   ```python
   import cv2
   import os
   
   def extract_frames(video_path, output_folder, sample_rate=1):
       """Extrait des images d'une vidéo à un taux d'échantillonnage donné"""
       video = cv2.VideoCapture(video_path)
       frame_count = 0
       saved_count = 0
       
       os.makedirs(output_folder, exist_ok=True)
       
       while video.isOpened():
           ret, frame = video.read()
           if not ret:
               break
               
           if frame_count % sample_rate == 0:
               output_path = f"{output_folder}/frame_{saved_count:05d}.jpg"
               cv2.imwrite(output_path, frame)
               saved_count += 1
               
           frame_count += 1
               
       video.release()
       return saved_count
   ```

2. **Création du modèle par transfer learning**
   - Utiliser un modèle pré-entraîné comme MobileNetV2
   - Ajouter des couches de classification personnalisées
   - Entraîner sur vos données d'activités spécifiques

   ```python
   import tensorflow as tf
   from tensorflow.keras.applications import MobileNetV2
   from tensorflow.keras.layers import Dense, GlobalAveragePooling2D
   from tensorflow.keras.models import Model
   
   # Dimensions attendues par votre application Java
   input_shape = (224, 224, 3)
   
   # Charger un modèle pré-entraîné sans les couches de classification
   base_model = MobileNetV2(weights='imagenet', include_top=False, input_shape=input_shape)
   
   # "Geler" les couches du modèle de base pour conserver les features pré-entraînées
   for layer in base_model.layers:
       layer.trainable = False
   
   # Ajouter vos propres couches de classification
   x = base_model.output
   x = GlobalAveragePooling2D()(x)
   x = Dense(1024, activation='relu')(x)
   predictions = Dense(10, activation='softmax')(x)  # 10 classes d'activités
   
   # Créer le modèle final
   model = Model(inputs=base_model.input, outputs=predictions)
   
   # Compiler le modèle
   model.compile(optimizer='adam',
                 loss='categorical_crossentropy',
                 metrics=['accuracy'])
   ```

3. **Entraînement du modèle**
   - Utiliser l'augmentation de données pour améliorer la robustesse
   - Entraîner sur vos images classées par activité

   ```python
   from tensorflow.keras.preprocessing.image import ImageDataGenerator
   
   # Générateur d'images avec augmentation de données
   train_datagen = ImageDataGenerator(
       rescale=1./255,
       rotation_range=20,
       width_shift_range=0.2,
       height_shift_range=0.2,
       shear_range=0.2,
       zoom_range=0.2,
       horizontal_flip=True,
       validation_split=0.2  # 20% pour validation
   )
   
   # Chargement des images d'entraînement
   train_generator = train_datagen.flow_from_directory(
       'dataset',
       target_size=input_shape[:2],
       batch_size=32,
       class_mode='categorical',
       subset='training'
   )
   
   # Chargement des images de validation
   validation_generator = train_datagen.flow_from_directory(
       'dataset',
       target_size=input_shape[:2],
       batch_size=32,
       class_mode='categorical',
       subset='validation'
   )
   
   # Entraînement du modèle
   history = model.fit(
       train_generator,
       steps_per_epoch=train_generator.samples // 32,
       epochs=20,
       validation_data=validation_generator,
       validation_steps=validation_generator.samples // 32
   )
   ```

4. **Fine-tuning du modèle**
   - Dégeler des couches profondes pour un ajustement plus fin
   - Réentraîner avec un taux d'apprentissage plus faible

   ```python
   # Dégeler certaines couches du modèle de base
   for layer in base_model.layers[-30:]:  # Les 30 dernières couches
       layer.trainable = True
   
   # Recompiler avec un taux d'apprentissage plus faible
   model.compile(optimizer=tf.keras.optimizers.Adam(1e-5),
                 loss='categorical_crossentropy',
                 metrics=['accuracy'])
   
   # Autre phase d'entraînement
   model.fit(
       train_generator,
       steps_per_epoch=train_generator.samples // 32,
       epochs=10,
       validation_data=validation_generator,
       validation_steps=validation_generator.samples // 32
   )
   ```

5. **Export du modèle**
   - Exporter le modèle au format SavedModel avec les noms de nœuds corrects
   - Placer le modèle dans le répertoire approprié

   ```python
   # Créer un modèle avec les noms d'entrée/sortie spécifiques
   input_tensor = tf.keras.layers.Input(shape=input_shape, name='input')
   model_output = model(input_tensor)
   output_tensor = tf.keras.layers.Activation('linear', name='output')(model_output)
   
   export_model = tf.keras.models.Model(inputs=input_tensor, outputs=output_tensor)
   
   # Sauvegarder le modèle au format SavedModel
   export_path = 'models/activity_recognition/model'
   tf.saved_model.save(export_model, export_path)
   ```

6. **Intégration dans l'application**
   - Placer le modèle exporté dans `src/main/resources/models/activity_recognition/model/`
   - Vérifier que le modèle fonctionne avec la classe `VisualActivityClassifier`

### Classification audio

La classe `AudioPatternDetector` nécessite un modèle TensorFlow de classification audio pour fonctionner. Voici les options recommandées et les étapes pour créer et intégrer ce modèle :

#### Options de modèles audio pré-entraînés

1. **VGGish**
   - Développé par Google
   - Accepte des MFCC comme entrée
   - Déjà pré-entraîné sur AudioSet (une grande collection de sons étiquetés)
   - Lien : [https://github.com/tensorflow/models/tree/master/research/audioset/vggish](https://github.com/tensorflow/models/tree/master/research/audioset/vggish)
   - Avantage : modèle bien documenté, haute précision

2. **YAMNet**
   - Modèle léger basé sur MobileNetV1
   - Pré-entraîné sur AudioSet avec 521 classes
   - Adapté pour les appareils avec ressources limitées
   - Lien : [https://github.com/tensorflow/models/tree/master/research/audioset/yamnet](https://github.com/tensorflow/models/tree/master/research/audioset/yamnet)
   - Accepte des formes d'onde audio brutes, ce qui nécessite une adaptation

3. **PANNs (Pretrained Audio Neural Networks)**
   - Collection de réseaux neuronaux pré-entraînés pour la classification audio
   - Plusieurs modèles disponibles, dont certains acceptant des MFCC
   - Lien : [https://github.com/qiuqiangkong/audioset_tagging_cnn](https://github.com/qiuqiangkong/audioset_tagging_cnn)
   - Avantage : état de l'art en termes de performance

#### Création d'un modèle de classification audio simple

Si vous préférez créer un modèle simple directement adapté à votre application, voici comment procéder :

```python
import tensorflow as tf
import numpy as np
import os

# Création des répertoires nécessaires
os.makedirs('models/audio_classification/model', exist_ok=True)

# Créer un modèle simple pour la classification audio basée sur MFCC
def create_audio_classification_model():
    # Entrée: MFCC avec 13 coefficients et un nombre variable de frames
    input_layer = tf.keras.layers.Input(shape=(None, 13), name='input')
    
    # Couche de convolution 1D pour capturer les patterns temporels
    x = tf.keras.layers.Conv1D(32, 3, activation='relu', padding='same')(input_layer)
    x = tf.keras.layers.MaxPooling1D(pool_size=2)(x)
    
    # Autre couche de convolution
    x = tf.keras.layers.Conv1D(64, 3, activation='relu', padding='same')(x)
    x = tf.keras.layers.MaxPooling1D(pool_size=2)(x)
    
    # Global pooling pour gérer les séquences de longueur variable
    x = tf.keras.layers.GlobalAveragePooling1D()(x)
    
    # Couches denses pour la classification
    x = tf.keras.layers.Dense(64, activation='relu')(x)
    x = tf.keras.layers.Dropout(0.5)(x)
    
    # Sortie: 5 classes d'audio (selon mapAudioClassesToActivities)
    output_layer = tf.keras.layers.Dense(5, activation='softmax', name='output')(x)
    
    # Créer le modèle
    model = tf.keras.Model(inputs=input_layer, outputs=output_layer)
    
    # Compiler le modèle
    model.compile(
        optimizer='adam',
        loss='categorical_crossentropy',
        metrics=['accuracy']
    )
    
    return model

# Créer le modèle
model = create_audio_classification_model()

# Sauvegarder le modèle
model.save('models/audio_classification/model', save_format='tf')
```

#### Adaptation de VGGish pour votre application

Si vous préférez utiliser VGGish, voici comment l'adapter :

```python
import tensorflow as tf
import numpy as np
import os

# Télécharger le modèle VGGish pré-entraîné
import tensorflow_hub as hub

# Charger le modèle VGGish de base
vggish_model = hub.load('https://tfhub.dev/google/vggish/1')

# Créer un modèle personnalisé avec la sortie adaptée
# VGGish produit des embeddings de 128 dimensions
input_mfcc = tf.keras.layers.Input(shape=(96, 64), name='input')  # Format MFCC attendu par VGGish

# Calculer les embeddings VGGish
embedding = vggish_model(input_mfcc)

# Ajouter des couches pour classifier en 5 catégories
x = tf.keras.layers.Dense(64, activation='relu')(embedding)
output = tf.keras.layers.Dense(5, activation='softmax', name='output')(x)

# Créer le modèle final
final_model = tf.keras.Model(inputs=input_mfcc, outputs=output)

# Compiler le modèle
final_model.compile(optimizer='adam', 
                   loss='categorical_crossentropy', 
                   metrics=['accuracy'])

# Sauvegarder le modèle
os.makedirs('models/audio_classification/model', exist_ok=True)
tf.saved_model.save(final_model, 'models/audio_classification/model')
```

#### Entraînement sur vos propres données audio

Pour entraîner le modèle sur vos propres données audio :

1. Collectez des échantillons audio pour chacune des 5 classes (silence/bruit de fond, voix humaine, sons TV/radio, sons de cuisine, sons de nettoyage)
2. Convertissez ces échantillons en MFCC
3. Créez des ensembles d'entraînement et de validation
4. Entraînez le modèle

```python
# Exemple simplifié d'entraînement sur des données MFCC
X_train = np.array([...])  # Vos données MFCC d'entraînement
y_train = np.array([...])  # Labels des échantillons (one-hot encoded)

model.fit(
    X_train, y_train,
    epochs=50,
    batch_size=32,
    validation_split=0.2
)
```

#### Intégration dans l'application

Une fois le modèle exporté, placez-le dans le dossier `src/main/resources/models/audio_classification/model/`. Le service `AudioPatternDetector` le chargera automatiquement au démarrage de l'application.

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

### Installation de TensorFlow Python pour l'entraînement des modèles

Pour installer TensorFlow en Python (nécessaire pour l'entraînement des modèles) :

1. Créez un environnement virtuel avec Python 3.10 ou 3.11 (recommandé) :
   ```bash
   python -m venv tf_env
   
   # Activation sur Windows
   tf_env\Scripts\activate
   
   # Activation sur Linux/macOS
   source tf_env/bin/activate
   ```

2. Installez TensorFlow et les packages nécessaires :
   ```bash
   pip install tensorflow tensorflow-hub numpy matplotlib opencv-python pillow
   ```

3. Si vous rencontrez des problèmes avec NumPy 2.x et TensorFlow :
   ```bash
   pip uninstall numpy
   pip install "numpy<2.0.0"
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
