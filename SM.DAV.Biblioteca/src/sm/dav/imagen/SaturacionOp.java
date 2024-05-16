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
 * Permite mejorar el contraste y la viveza de una imagen al ajustar dinámicamente la saturación de los píxeles.
 * @author daniel
 */
public class SaturacionOp extends BufferedImageOpAdapter {
    
    private float umbral;

    public SaturacionOp(float umbral) {
        this.umbral = umbral;
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
        
        if (bi1 == null) {
            bi1 = createCompatibleDestImage(bi, null);
        }
        
        WritableRaster srcRaster = bi.getRaster();
        WritableRaster destRaster = bi1.getRaster();
        int[] pixelComp = new int[srcRaster.getNumBands()];
        int[] pixelCompDest = new int[srcRaster.getNumBands()];
        float[] hsbvals = new float[3];
        
        for (int x = 0; x < bi.getWidth(); x++) {
            for (int y = 0; y < bi.getHeight(); y++) {
                srcRaster.getPixel(x, y, pixelComp);
                // Pasamos de RGB (rango 0 a 255) a HSV (rango 0 a 1)
                Color.RGBtoHSB(pixelComp[0], pixelComp[1], pixelComp[2], hsbvals);
                
                if(hsbvals[1] < umbral) {
                    hsbvals[1] = umbral;
                }
                
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
