package com.rbaudu.angel.analyzer.util;

import org.springframework.stereotype.Component;
import org.tensorflow.Tensor;
import org.tensorflow.types.TFloat32;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Utilitaires pour le traitement audio.
 */
@Component
public class AudioUtils {
    
    /**
     * Conversion d'un format audio à un autre
     * @param audioIn Flux audio d'entrée
     * @param targetFormat Format cible
     * @return Flux audio converti
     * @throws IOException Si une erreur se produit pendant la conversion
     */
    public AudioInputStream convertAudioFormat(AudioInputStream audioIn, 
                                             AudioFormat targetFormat) throws IOException {
        if (audioIn.getFormat().matches(targetFormat)) {
            return audioIn;
        }
        return AudioSystem.getAudioInputStream(targetFormat, audioIn);
    }
    
    /**
     * Découpage d'un segment audio
     * @param audioIn Flux audio d'entrée
     * @param startFrame Frame de début
     * @param numFrames Nombre de frames à extraire
     * @return Segment audio extrait
     * @throws IOException Si une erreur se produit pendant l'extraction
     */
    public AudioInputStream extractAudioSegment(AudioInputStream audioIn, 
                                              long startFrame, 
                                              long numFrames) throws IOException {
        
        AudioFormat format = audioIn.getFormat();
        int frameSize = format.getFrameSize();
        
        // Positionnement au début du segment
        audioIn.skip(startFrame * frameSize);
        
        // Lecture des données du segment
        byte[] buffer = new byte[(int)(numFrames * frameSize)];
        int bytesRead = audioIn.read(buffer);
        
        // Création d'un nouveau flux audio à partir du segment
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer, 0, bytesRead);
        return new AudioInputStream(bais, format, bytesRead / frameSize);
    }
    
    /**
     * Combinaison de plusieurs segments audio
     * @param segments Segments audio à combiner
     * @return Flux audio combiné
     * @throws IOException Si une erreur se produit pendant la combinaison
     */
    public AudioInputStream concatAudioSegments(AudioInputStream[] segments) throws IOException {
        if (segments.length == 0) {
            return null;
        }
        
        AudioFormat format = segments[0].getFormat();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        // Buffer temporaire pour la lecture
        byte[] buffer = new byte[4096];
        int bytesRead;
        
        // Concaténation des segments
        for (AudioInputStream segment : segments) {
            while ((bytesRead = segment.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
        
        // Création d'un nouveau flux audio à partir des données combinées
        byte[] combinedData = out.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(combinedData);
        return new AudioInputStream(bais, format, combinedData.length / format.getFrameSize());
    }
    
    /**
     * Convertit des échantillons audio PCM en MFCC (Mel-Frequency Cepstral Coefficients)
     * Note: Cette implémentation est simplifiée et utiliserait normalement TarsosDSP
     * @param audioData Données audio PCM
     * @param sampleRate Taux d'échantillonnage
     * @param numCoefficients Nombre de coefficients MFCC à extraire
     * @return Tableau des coefficients MFCC
     */
    public float[] extractMFCC(byte[] audioData, float sampleRate, int numCoefficients) {
        // Cette méthode serait normalement implémentée avec TarsosDSP
        // Exemple simplifié pour l'illustration
        
        // Convertir les bytes en échantillons flottants
        float[] samples = pcmToFloat(audioData);
        
        // Ici, nous utiliserions TarsosDSP pour extraire les MFCC
        // Pour l'instant, retournons des valeurs fictives
        float[] mfcc = new float[numCoefficients];
        for (int i = 0; i < numCoefficients; i++) {
            mfcc[i] = 0.0f; // À remplacer par l'implémentation réelle
        }
        
        return mfcc;
    }
    
    /**
     * Convertit des données PCM brutes en échantillons flottants
     * @param pcmData Données PCM (16 bits)
     * @return Tableau d'échantillons normalisés entre -1.0 et 1.0
     */
    public float[] pcmToFloat(byte[] pcmData) {
        float[] floatData = new float[pcmData.length / 2];
        ByteBuffer bb = ByteBuffer.wrap(pcmData);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        
        for (int i = 0; i < floatData.length; i++) {
            short sample = bb.getShort();
            floatData[i] = sample / 32768.0f; // Normalisation entre -1.0 et 1.0
        }
        
        return floatData;
    }
    
    /**
     * Convertit des données audio en Tensor pour TensorFlow
     * @param audioData Données audio en format flottant
     * @return Tensor pour l'entrée du modèle
     */
    public Tensor<TFloat32> audioToTensor(float[] audioData) {
        // Format: [batch_size, audio_length]
        FloatBuffer floatBuffer = FloatBuffer.allocate(1 * audioData.length);
        
        for (float sample : audioData) {
            floatBuffer.put(sample);
        }
        
        floatBuffer.rewind();
        
        return TFloat32.tensorOf(org.tensorflow.Shape.of(1, audioData.length), floatBuffer);
    }
}
