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
 * Operador para resaltar el color rojo dejando el resto en niveles de grises
 * @author daniel
 */
public class RojoOp extends BufferedImageOpAdapter {

    private int umbral;

    public RojoOp(int umbral) {
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
        for (int x = 0; x < bi.getWidth(); x++) {
            for (int y = 0; y < bi.getHeight(); y++) {
                srcRaster.getPixel(x, y, pixelComp);
                
                // R-G-B
                int valor = pixelComp[0] - pixelComp[1] - pixelComp[2];
                if(valor >= umbral){
                    pixelCompDest = pixelComp;
                } else {
                    int media = (pixelComp[0]+pixelComp[1]+pixelComp[2])/3;
                    pixelCompDest[0] = media;
                    pixelCompDest[1] = media;
                    pixelCompDest[2] = media;
                }
                
                destRaster.setPixel(x, y, pixelCompDest);
            }
        }
        return bi1;
    }

}
