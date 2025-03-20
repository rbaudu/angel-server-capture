# Module de reconnaissance de comportement

Ce module étend le serveur Angel pour ajouter la reconnaissance de comportements à partir des activités détectées. Il permet d'analyser des séquences d'activités pour en déduire des comportements de plus haut niveau et détecter des anomalies.

## Fonctionnalités

- Analyse des séquences d'activités pour détecter des patterns de comportement
- Détection d'anomalies de comportement
- API REST pour consulter les résultats d'analyse
- Notifications en temps réel via WebSocket
- Configuration flexible des paramètres d'analyse
- Définition de patterns de comportement personnalisés via JSON

## Architecture

Le module est organisé selon les composants suivants :

- **Modèles** (`model/`) : Classes représentant les types de comportements, patterns, et résultats d'analyse
- **Services** (`service/`) : Services d'analyse et de gestion des patterns
- **Contrôleurs** (`controller/`) : API REST pour l'interaction avec le module
- **Configuration** (`config/`) : Paramètres configurables
- **Événements** (`event/`) : Mécanisme de notification pour les résultats
- **WebSocket** (`websocket/`) : Services de notification en temps réel

## Implémentation technique

### Analyse de comportement

L'analyse de comportement se fait en plusieurs étapes :

1. Les résultats d'analyse d'activités sont interceptés via un écouteur d'événements
2. Ces activités sont stockées dans une séquence temporelle
3. À intervalles réguliers, cette séquence est analysée pour détecter des patterns de comportement
4. Les patterns définis dans le fichier JSON (`patterns.json`) sont comparés à la séquence observée
5. Le meilleur pattern correspondant est identifié comme comportement actuel
6. Si aucun pattern ne correspond mais que la séquence présente des caractéristiques inhabituelles, une anomalie est détectée

### Détection d'anomalies

La détection d'anomalies se base sur plusieurs facteurs :

- Activités inhabituelles pour l'heure de la journée
- Durées anormales des activités
- Transitions rapides et fréquentes entre activités
- Activités répétitives ou alternances anormales

Ces facteurs sont combinés pour produire un score d'anomalie global.

### API REST

L'API REST expose les endpoints suivants :

- `GET /api/behavior/latest` : Dernier résultat d'analyse
- `GET /api/behavior/recent` : Résultats récents
- `GET /api/behavior/type/{type}` : Résultats pour un type spécifique
- `POST /api/behavior/analyze` : Déclenche une analyse manuelle
- `GET /api/behavior/patterns` : Liste des patterns définis
- `GET /api/behavior/config` : Configuration actuelle
- `POST /api/behavior/config` : Mise à jour de la configuration

### Notifications WebSocket

Les résultats d'analyse sont diffusés en temps réel via WebSocket sur les topics :

- `/topic/behavior` : Tous les résultats d'analyse
- `/topic/behavior/unusual` : Uniquement les comportements inhabituels

## Extension et personnalisation

### Ajout de nouveaux types de comportements

Pour ajouter de nouveaux types de comportements :

1. Ajouter une nouvelle valeur dans l'énumération `BehaviorType`
2. Créer un nouveau pattern dans le fichier `patterns.json`

### Ajout de nouveaux facteurs d'anomalie

Pour ajouter de nouveaux facteurs d'anomalie :

1. Créer une nouvelle méthode de calcul dans la classe `AnomalyDetector`
2. Ajouter ce facteur dans la méthode `getAnomalyFactors()`

## Configuration

La configuration se fait via le fichier `application.properties` avec les paramètres suivants :

```properties
# Configuration de l'analyseur de comportements
angel.behavior.patternsDefinitionPath=classpath:behavior/patterns.json
angel.behavior.timeWindowSec=3600
angel.behavior.confidenceThreshold=0.65
angel.behavior.historySize=100
angel.behavior.analysisIntervalMs=5000
angel.behavior.continuousAnalysis=true
angel.behavior.anomalyDetectionEnabled=true
angel.behavior.anomalyThreshold=2.0
angel.behavior.fusionStrategy=WEIGHTED
angel.behavior.minActivitiesForAnalysis=3
```