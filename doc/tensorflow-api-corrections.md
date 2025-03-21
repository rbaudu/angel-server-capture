# Documentation des corrections d'API TensorFlow et OpenCV

## Problèmes identifiés

Les erreurs de compilation identifiées dans le projet étaient principalement liées à des incompatibilités d'API entre les versions utilisées dans le code et les versions spécifiées dans les dépendances :

1. **Incompatibilités avec l'API TensorFlow** :
   - Utilisation d'API ndarray incompatibles avec la version 0.5.0 (comme `createFloatNdArray` et `asRawTensor`)
   - Méthodes de lecture de buffer incompatibles (`read()` avec `FloatBuffer`)
   - Méthode `asFloatBuffer()` inexistante dans l'API ByteDataBuffer
   - Méthode `Tensor.create(long[], FloatBuffer)` inexistante

2. **Incompatibilités avec l'API OpenCV/JavaCV** :
   - Méthode `copyTo` appelée sur `FloatPointer` avec `Mat` comme argument
   - Utilisation incorrecte de `HOGDescriptor.detectMultiScale` avec `MatVector` au lieu de `RectVector`
   - Méthode `getFloatArray` non trouvée dans `BytePointer`
   - Incompatibilité entre `FloatPointer` et les méthodes `setSVMDetector` qui attendent un `Mat`

## Solutions apportées

### 1. Mise à jour du POM

- Rétrogradation de la version TensorFlow de 0.5.0 à 0.4.0 pour compatibilité
- Ajout explicite de la dépendance `ndarray` pour assurer la disponibilité de toutes les classes nécessaires
- Ajout d'un repository pour JitPack (pour TarsosDSP)

### 2. Corrections dans VideoUtils.java

- Suppression des références à `NdArrays.createFloatNdArray` et `ndArray.asRawTensor()`
- Remplacement de la méthode d'extraction de données des pixels en utilisant `ptr()` et `get()`
- Utilisation de `Tensor.create()` avec le bon format pour créer des tenseurs avec DataType

Exemple de code modifié :
```java
// Avant
Shape shape = Shape.of(1, height, width, 3);
FloatNdArray ndArray = NdArrays.createFloatNdArray(shape);
ndArray.read(floatBuffer);
return Tensor.of(shape, ndArray.asRawTensor().data());

// Après
long[] shape = {1, height, width, 3};
return Tensor.create(shape, DataType.FLOAT, FloatBuffer.wrap(pixelData));
```

### 3. Corrections dans AudioUtils.java

- Même approche qu'avec VideoUtils, simplification de l'API Tensor
- Utilisation de l'API standard `Tensor.create()` compatible avec la version 0.4.0

### 4. Corrections dans PresenceDetector.java

- Correction de l'utilisation de `HOGDescriptor.detectMultiScale` pour utiliser correctement `RectVector`
- Remplacement de l'accès aux données du tensor par une méthode plus simple :

```java
// Avant
resultBuffer = (FloatBuffer) resultTensor.asRawTensor().data().asFloatBuffer();

// Après
float[] resultArray = new float[1 * 100 * 7];
resultTensor.copyTo(resultArray);
```

- Simplification de la configuration du HOGDescriptor en évitant les conflits de types

### 5. Corrections dans AudioPatternDetector.java et VisualActivityClassifier.java

- Utilisation directe de `copyTo` pour extraire les données des tenseurs dans des tableaux
- Suppression des références aux méthodes qui n'existent pas dans la version 0.4.0
- Simplification de la manipulation des données pour éviter les problèmes d'API

```java
// Avant
resultBuffer = (FloatBuffer) resultTensor.asRawTensor().data().asFloatBuffer();
resultBuffer.rewind();
resultBuffer.get(audioClasses);

// Après
resultTensor.copyTo(audioClasses);
```

## Impacts sur le fonctionnement

Ces corrections permettent de maintenir les mêmes fonctionnalités tout en assurant la compatibilité avec les versions des bibliothèques utilisées. Les modifications n'affectent pas la logique métier, mais seulement l'interface avec les bibliothèques TensorFlow et OpenCV.

## Recommandations pour le futur

1. **Gestion des versions** : Définir des versions précises et verrouillées pour les dépendances critiques comme TensorFlow et OpenCV.
   
2. **Tests d'intégration** : Mettre en place des tests qui vérifient la compatibilité avec les APIs des bibliothèques externes.

3. **Migration future** : Si une migration vers TensorFlow 0.5.0 ou supérieur est souhaitée, planifier une refactorisation complète des interfaces avec TensorFlow en se basant sur les patterns utilisés dans cette correction.

4. **Documentation des APIs** : Maintenir une documentation des usages spécifiques des APIs externes pour faciliter les futurs changements de version.

## Résumé des fichiers corrigés

1. `pom.xml`
2. `src/main/java/com/rbaudu/angel/analyzer/util/VideoUtils.java`
3. `src/main/java/com/rbaudu/angel/analyzer/util/AudioUtils.java`
4. `src/main/java/com/rbaudu/angel/analyzer/service/video/PresenceDetector.java`
5. `src/main/java/com/rbaudu/angel/analyzer/service/audio/AudioPatternDetector.java`
6. `src/main/java/com/rbaudu/angel/analyzer/service/video/VisualActivityClassifier.java`

## Guide des bonnes pratiques avec TensorFlow Java

Pour éviter des problèmes similaires à l'avenir, voici quelques bonnes pratiques pour l'utilisation de TensorFlow Java :

1. **Créer des tenseurs** :
   ```java
   // Méthode préférée pour créer des tenseurs à partir de tableaux
   float[] data = new float[size];
   // Remplir le tableau...
   long[] shape = {dim1, dim2, ...};
   Tensor tensor = Tensor.create(shape, DataType.FLOAT, FloatBuffer.wrap(data));
   ```

2. **Extraire des données** :
   ```java
   // Méthode préférée pour extraire des données d'un tensor
   float[] results = new float[size];
   tensor.copyTo(results);
   ```

3. **Exécuter l'inférence** :
   ```java
   List<Tensor> outputs = model.session().runner()
       .feed("input_name", inputTensor)
       .fetch("output_name")
       .run();
   Tensor resultTensor = outputs.get(0);
   ```

4. **Libérer les ressources** :
   ```java
   // Toujours fermer les tenseurs et les modèles quand ils ne sont plus nécessaires
   tensor.close();
   model.close();
   ```
