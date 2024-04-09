/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package practica4;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

/**
 *
 * @author daniel
 */
public class MiElipse extends Ellipse2D.Float {
    public MiElipse(Point2D p, float w, float h) {
        super((float)p.getX(), (float)p.getY(), w, h);
    }
    
    public void setLocation(Point2D pos) {
        this.x = (float) pos.getX();
        this.y = (float) pos.getY();
    }
}

