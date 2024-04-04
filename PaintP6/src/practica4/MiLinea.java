/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package practica4;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 *
 * @author daniel
 */
public class MiLinea extends Line2D.Float {
    public MiLinea(Point2D p1, Point2D p2) {
        super(p1,p2);
    }
    
    public boolean isNear(Point2D p) {
        if(this.getP1().equals(this.getP2())) // p1=p2, linea deformada a punto
            return this.getP1().distance(p) <= 2.0; // distancia de p1 a p
        return this.ptLineDist(p) <= 2.0; // distancia perpendicular minima de la recta a p
    }
    
    @Override
    public boolean contains(Point2D p) {
        return isNear(p);
    }
    
    public void setLocation(Point2D pos) {
        // Calculas diferencia del punto de evento al origen de la recta, respecto su longitud
        // para desplazarla hasta click del evento con su longitud original
        double dx = pos.getX()-this.getX1();
        double dy = pos.getY()-this.getY1();
        Point2D newp2 = new Point2D.Double(this.getX2()+dx, this.getY2()+dy);
        this.setLine(pos,newp2);
    }
}
