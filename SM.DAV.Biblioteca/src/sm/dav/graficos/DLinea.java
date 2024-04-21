/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sm.dav.graficos;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 *
 * @author daniel
 */
public class DLinea extends MiShape {
    private float x1;
    private float y1;
    private float x2;
    private float y2;

    public DLinea(double x1, double y1, double x2, double y2) {
        this.x1 = (float) x1;
        this.y1 = (float) y1;
        this.x2 = (float) x2;
        this.y2 = (float) y2;
    }
    
    public DLinea(Point2D p1, Point2D p2) {
        this.x1 = (float) p1.getX();
        this.x2 = (float) p2.getX();
        this.y1 = (float) p1.getY();
        this.y2 = (float) p2.getY();
    }

    public float getX1() {
        return x1;
    }

    public void setX1(float x1) {
        this.x1 = x1;
    }

    public float getY1() {
        return y1;
    }

    public void setY1(float y1) {
        this.y1 = y1;
    }

    public float getX2() {
        return x2;
    }

    public void setX2(float x2) {
        this.x2 = x2;
    }

    public float getY2() {
        return y2;
    }

    public void setY2(float y2) {
        this.y2 = y2;
    }
    
    

    @Override
    public void draw(Graphics2D g2d) {        
        g2d.setColor(this.getColor());
        g2d.setStroke(this.getTrazo());
        
        RenderingHints render;
        if(this.isAlisar()){
            render = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        } else {
            render = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }
        g2d.setRenderingHints(render);
        
        Composite comp;
        if(this.isTransparencia()){
            comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
        }else{
            comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
        }
        g2d.setComposite(comp);
        
        Line2D.Float linea = new Line2D.Float(x1, y1, x2, y2);
        g2d.draw(linea);
        
        if(editar){
            g2d.setColor(Color.RED);
            float[] dashPattern = {5, 2}; // Patrón de guiones: 5 píxeles sólidos, 2 píxeles de espacio
            Stroke stroke = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, dashPattern, 0);
            g2d.setStroke(stroke);
            Ellipse2D.Float circulo = new Ellipse2D.Float(x1-10, y1-10, 20, 20);
            g2d.draw(circulo);
        }
    }

    @Override
    public void SetLocation(Point2D pos) {
        // Calculas diferencia del punto de evento al origen de la recta, respecto su longitud
        // para desplazarla hasta click del evento con su longitud original
        double dx = pos.getX()-this.getX1();
        double dy = pos.getY()-this.getY1();
        Point2D newp2 = new Point2D.Double(this.getX2()+dx, this.getY2()+dy);
        Line2D.Float recta = new Line2D.Float();
        recta.setLine(pos,newp2);
        this.x1 = recta.x1;
        this.x2 = recta.x2;
        this.y1 = recta.y1;
        this.y2 = recta.y2;
    }

    @Override
    public void SetFrameFromDiagonal(Point2D p1, Point2D p2) {
        this.x1 = (float) p1.getX();
        this.x2 = (float) p2.getX();
        this.y1 = (float) p1.getY();
        this.y2 = (float) p2.getY();
    }
    
    private boolean isNear(Point2D p) {
        Point2D.Float p1 = new Point2D.Float(x1, y1);
        Point2D.Float p2 = new Point2D.Float(x2, y2);
        if(p1.equals(p2)) // p1=p2, linea deformada a punto
            return p1.distance(p) <= 2.0; // distancia de p1 a p
        Line2D.Float recta = new Line2D.Float(p1, p2);
        return recta.ptLineDist(p) <= 2.0; // distancia perpendicular minima de la recta a p
    }

    @Override
    public boolean contains(Point2D p) {
        return isNear(p);
    }
    
}
