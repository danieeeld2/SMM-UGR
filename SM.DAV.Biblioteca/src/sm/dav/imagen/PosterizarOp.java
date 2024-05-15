/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sm.dav.imagen;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import sm.image.BufferedImageOpAdapter;

/**
 * Clase propia para reducir el número de colores de una imagen a un número específico
 * de niveles (Efecto Posterizar)
 * @author daniel
 */
public class PosterizarOp extends BufferedImageOpAdapter {

    private int niveles;

    public PosterizarOp(int niveles) {
        this.niveles = niveles;
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
        int sample;
        float K = 256/niveles;
        for (int x = 0; x < bi.getWidth(); x++) {
            for (int y = 0; y < bi.getHeight(); y++) {
                for (int band = 0; band < srcRaster.getNumBands(); band++) {
                    sample = srcRaster.getSample(x, y, band);
                    
                    sample = (int) (K*((int)(sample/K)));
                    
                    destRaster.setSample(x, y, band, sample);
                }
            }
        }
        return bi1;
    }

}
