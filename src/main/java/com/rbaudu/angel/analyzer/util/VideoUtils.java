package com.rbaudu.angel.analyzer.util;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.springframework.stereotype.Component;
import org.tensorflow.Tensor;
import org.tensorflow.ndarray.FloatNdArray;
import org.tensorflow.ndarray.NdArrays;
import org.tensorflow.ndarray.Shape;
import java.nio.FloatBuffer;

/**
 * Utilitaire pour traiter les images vidéo.
 */
@Component
public class VideoUtils {
    
    /**
     * Redimensionne une image pour l'entrée du modèle
     * @param frame Image à redimensionner
     * @param width Largeur cible
     * @param height Hauteur cible
     * @return Image redimensionnée
     */
    public Mat resizeFrame(Mat frame, int width, int height) {
        Mat resized = new Mat();
        Size size = new Size(width, height);
        opencv_imgproc.resize(frame, resized, size);
        return resized;
    }
    
    /**
     * Convertit une image BGR en RGB
     * @param frame Image BGR
     * @return Image RGB
     */
    public Mat bgrToRgb(Mat frame) {
        Mat rgb = new Mat();
        opencv_imgproc.cvtColor(frame, rgb, opencv_imgproc.COLOR_BGR2RGB);
        return rgb;
    }
    
    /**
     * Normalise les valeurs de pixels (0-255 vers 0-1)
     * @param frame Image à normaliser
     * @return Image normalisée
     */
    public Mat normalizeFrame(Mat frame) {
        Mat normalized = new Mat();
        frame.convertTo(normalized, frame.type(), 1.0/255, 0);
        return normalized;
    }
    
    /**
     * Convertit une image OpenCV en Tensor TensorFlow
     * @param frame Image OpenCV (RGB normalisée)
     * @param height Hauteur du tensor
     * @param width Largeur du tensor
     * @return Tensor TensorFlow
     */
    public Tensor matToTensor(Mat frame, int height, int width) {
        // Suppose que l'image est déjà en RGB et normalisée entre 0-1
        
        // Créer un tampon pour les pixels de l'image
        // Format: [1, height, width, 3]
        FloatBuffer floatBuffer = FloatBuffer.allocate(1 * height * width * 3);
        
        // Remplir le tampon avec les valeurs de pixels
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float[] pixel = new float[3];
                frame.ptr(y, x).getFloatArray(0, pixel, 0, 3);
                floatBuffer.put(pixel[0]); // R
                floatBuffer.put(pixel[1]); // G
                floatBuffer.put(pixel[2]); // B
            }
        }
        
        floatBuffer.rewind();
        
        // Créer le tensor à partir du tampon
        Shape shape = Shape.of(1, height, width, 3);
        FloatNdArray ndArray = NdArrays.createFloatNdArray(shape);
        ndArray.read(floatBuffer);
        return Tensor.of(shape, ndArray.asRawTensor().data());
    }
    
    /**
     * Prépare une image OpenCV pour l'entrée du modèle TensorFlow
     * @param frame Image source
     * @param targetWidth Largeur cible
     * @param targetHeight Hauteur cible
     * @return Tensor prêt pour l'inférence
     */
    public Tensor prepareImageForModel(Mat frame, int targetWidth, int targetHeight) {
        // Chaîne de prétraitement
        Mat resized = resizeFrame(frame, targetWidth, targetHeight);
        Mat rgb = bgrToRgb(resized);
        Mat normalized = normalizeFrame(rgb);
        
        // Conversion en tensor
        return matToTensor(normalized, targetHeight, targetWidth);
    }
}