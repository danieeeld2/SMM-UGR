/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sm.dav.imagen;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import sm.image.BufferedImageOpAdapter;

/**
 *
 * @author daniel
 */
public class CambiarColorOp extends BufferedImageOpAdapter {

    private Color c1;
    private Color c2;
    private int umbral;

    public CambiarColorOp(Color c1, Color c2, int umbral) {
        this.c1 = c1;
        this.c2 = c2;
        this.umbral = umbral;
    }
    
    private float distancia(float h1, float h2){
        if(Math.abs(h1-h2) <= 180) {
            return Math.abs(h1-h2);
        } else {
            return 360-Math.abs(h1-h2);
        }
    }

    @Override
    public BufferedImage filter(BufferedImage bi, BufferedImage bi1) {
        if (bi == null) {
            throw new NullPointerException("src image is null");
        } else {
            ColorModel colorModel = bi.getColorModel();
            if(colorModel.getColorSpace().getType() != ColorSpace.TYPE_RGB){
                throw new NullPointerException("src image is not RGB");
            }
        }
        if(c1 == null || c2 == null) {
            throw new NullPointerException("Choose correct colors");
        }
        
        if (bi1 == null) {
            bi1 = createCompatibleDestImage(bi, null);
        }
        
        WritableRaster srcRaster = bi.getRaster();
        WritableRaster destRaster = bi1.getRaster();
        int[] pixelComp = new int[srcRaster.getNumBands()];
        int[] pixelCompDest = new int[srcRaster.getNumBands()];
        float[] hsbvals = new float[3];
        
        // Obtener el tono de C1 y C2
        float[] hsvc1 = new float[3];
        float[] hsvc2 = new float[3];
        Color.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue(), hsvc1);
        Color.RGBtoHSB(c2.getRed(), c2.getGreen(), c2.getBlue(), hsvc2);
        hsvc1[0] = hsvc1[0]*360;
        hsvc2[0] = hsvc2[0]*360;
        
        for (int x = 0; x < bi.getWidth(); x++) {
            for (int y = 0; y < bi.getHeight(); y++) {
                srcRaster.getPixel(x, y, pixelComp);
                // Pasamos de RGB (rango 0 a 255) a HSV (rango 0 a 1)
                Color.RGBtoHSB(pixelComp[0], pixelComp[1], pixelComp[2], hsbvals);
                // Multiplicamos H por 360 para que esté en el rango adecuado
                hsbvals[0] = hsbvals[0]*360;
                // Cambiamos el tono del pixel si se parece al de C1, sino lo dejamos igual
                if(distancia(hsbvals[0], hsvc1[0]) <= umbral){
                    hsbvals[0] = hsvc2[0];
                }
                // Pasamos de HSV (rango 0 a 360) a HSV (rango 0 a 1) a RGB (codificado como entero)
                hsbvals[0] = hsbvals[0]/360;
                int pixel = Color.HSBtoRGB(hsbvals[0], hsbvals[1], hsbvals[2]);
                // Pasamos de entero a RGB (rango 0 a 255)
                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;
                // Asignamos al pixel de destino
                pixelCompDest[0] = r; pixelCompDest[1] = g; pixelCompDest[2] = b;
                destRaster.setPixel(x, y, pixelCompDest);
            }
        }

        return bi1;
    }

}
