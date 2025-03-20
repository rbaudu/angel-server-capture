package com.rbaudu.angel.model;

import java.time.Instant;
import java.util.Objects;

import org.bytedeco.opencv.opencv_core.Mat;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Représente une trame vidéo capturée.
 * Cette classe encapsule les données d'une trame vidéo et ses métadonnées.
 */
public class VideoFrame {
    
    /**
     * Données d'image encodées en Base64.
     */
    private String imageData;
    
    /**
     * Horodatage de capture de la trame.
     */
    private Instant timestamp;
    
    /**
     * Numéro de séquence de la trame.
     */
    private long sequenceNumber;
    
    /**
     * Largeur de l'image en pixels.
     */
    private int width;
    
    /**
     * Hauteur de l'image en pixels.
     */
    private int height;
    
    /**
     * Format de l'image (ex: JPEG, PNG, etc.).
     */
    private String format;
    
    /**
     * Indique si un mouvement a été détecté dans cette trame.
     */
    private boolean motionDetected;
    
    /**
     * Indique si une personne a été détectée dans cette trame.
     */
    private boolean personDetected;
    
    /**
     * Position X du centre de la personne détectée (si applicable).
     */
    private Integer personX;
    
    /**
     * Position Y du centre de la personne détectée (si applicable).
     */
    private Integer personY;
    
    /**
     * Matrice OpenCV de la trame.
     * Ce champ est ignoré lors de la sérialisation/désérialisation JSON.
     */
    @JsonIgnore
    private transient Mat frameMat;

    /**
     * Constructeur par défaut
     */
    public VideoFrame() {
    }

    /**
     * Constructeur avec tous les champs
     */
    public VideoFrame(String imageData, Instant timestamp, long sequenceNumber, int width, int height,
                     String format, boolean motionDetected, boolean personDetected,
                     Integer personX, Integer personY, Mat frameMat) {
        this.imageData = imageData;
        this.timestamp = timestamp;
        this.sequenceNumber = sequenceNumber;
        this.width = width;
        this.height = height;
        this.format = format;
        this.motionDetected = motionDetected;
        this.personDetected = personDetected;
        this.personX = personX;
        this.personY = personY;
        this.frameMat = frameMat;
    }

    /**
     * Getters et Setters
     */
    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public boolean isMotionDetected() {
        return motionDetected;
    }

    public void setMotionDetected(boolean motionDetected) {
        this.motionDetected = motionDetected;
    }

    public boolean isPersonDetected() {
        return personDetected;
    }

    public void setPersonDetected(boolean personDetected) {
        this.personDetected = personDetected;
    }

    public Integer getPersonX() {
        return personX;
    }

    public void setPersonX(Integer personX) {
        this.personX = personX;
    }

    public Integer getPersonY() {
        return personY;
    }

    public void setPersonY(Integer personY) {
        this.personY = personY;
    }

    public Mat getFrameMat() {
        return frameMat;
    }

    public void setFrameMat(Mat frameMat) {
        this.frameMat = frameMat;
    }

    /**
     * Méthode equals pour la comparaison des objets
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VideoFrame that = (VideoFrame) o;
        return sequenceNumber == that.sequenceNumber &&
               width == that.width &&
               height == that.height &&
               motionDetected == that.motionDetected &&
               personDetected == that.personDetected &&
               Objects.equals(imageData, that.imageData) &&
               Objects.equals(timestamp, that.timestamp) &&
               Objects.equals(format, that.format) &&
               Objects.equals(personX, that.personX) &&
               Objects.equals(personY, that.personY);
        // frameMat n'est pas inclus car il peut causer des problèmes de comparaison
    }

    /**
     * Méthode hashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(imageData, timestamp, sequenceNumber, width, height, format,
                           motionDetected, personDetected, personX, personY);
        // frameMat n'est pas inclus car il peut causer des problèmes de hachage
    }

    /**
     * Méthode toString
     */
    @Override
    public String toString() {
        return "VideoFrame{" +
               "timestamp=" + timestamp +
               ", sequenceNumber=" + sequenceNumber +
               ", width=" + width +
               ", height=" + height +
               ", format='" + format + '\'' +
               ", motionDetected=" + motionDetected +
               ", personDetected=" + personDetected +
               ", personX=" + personX +
               ", personY=" + personY +
               '}';
        // imageData et frameMat sont exclus pour éviter une sortie trop verbale
    }

    /**
     * Builder statique pour créer des instances de VideoFrame
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String imageData;
        private Instant timestamp;
        private long sequenceNumber;
        private int width;
        private int height;
        private String format;
        private boolean motionDetected;
        private boolean personDetected;
        private Integer personX;
        private Integer personY;
        private Mat frameMat;

        public Builder imageData(String imageData) {
            this.imageData = imageData;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder sequenceNumber(long sequenceNumber) {
            this.sequenceNumber = sequenceNumber;
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder height(int height) {
            this.height = height;
            return this;
        }

        public Builder format(String format) {
            this.format = format;
            return this;
        }

        public Builder motionDetected(boolean motionDetected) {
            this.motionDetected = motionDetected;
            return this;
        }

        public Builder personDetected(boolean personDetected) {
            this.personDetected = personDetected;
            return this;
        }

        public Builder personX(Integer personX) {
            this.personX = personX;
            return this;
        }

        public Builder personY(Integer personY) {
            this.personY = personY;
            return this;
        }

        public Builder frameMat(Mat frameMat) {
            this.frameMat = frameMat;
            return this;
        }

        public VideoFrame build() {
            return new VideoFrame(imageData, timestamp, sequenceNumber, width, height,
                                format, motionDetected, personDetected, personX, personY, frameMat);
        }
    }
}