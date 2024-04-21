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
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author daniel
 */
public class DFantasma extends MiShape {
    private float x;
    private float y;
    private Area area;

    public DFantasma(double x, double y) {
        this.x = (float) x;
        this.y = (float) y;
        area = new Area();
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
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
        
        Area fantasma = new Area();
        
        // Añadimos cabeza
        double w = 60;
        double h = 34;
        Ellipse2D.Float cabeza = new Ellipse2D.Float(x,y,(float)w,(float)h);
        Area Acabeza = new Area(cabeza);
        
        // Ojos
        Ellipse2D.Float ojo1 = new Ellipse2D.Float((float)(x+w/4)-8, (float)(y+h/4)+8, 16, 16);
        Ellipse2D.Float ojo2 = new Ellipse2D.Float((float)(x+3*w/4)-8,(float)(y+h/4)+8, 16, 16);
        
        Area Aojo1 = new Area(ojo1);
        Area Aojo2 = new Area(ojo2);
        
        // Añadimos cuerpo
        Rectangle2D.Float cuerpo = new Rectangle2D.Float(x, (float)(y+h/2), (float)w, (float)(2*h));
        Area Acuerpo = new Area(cuerpo);
        
        fantasma.add(Acabeza);
        fantasma.add(Acuerpo);
        fantasma.subtract(Aojo1);
        fantasma.subtract(Aojo2);
        
        this.area = fantasma;
        
        if(this.isRelleno()){
            g2d.fill(fantasma);
        }else{
            g2d.draw(fantasma);
        }
        
        if(editar){
            g2d.setColor(Color.RED);
            float[] dashPattern = {5, 2}; // Patrón de guiones: 5 píxeles sólidos, 2 píxeles de espacio
            Stroke stroke = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, dashPattern, 0);
            g2d.setStroke(stroke);
            Ellipse2D.Float circulo = new Ellipse2D.Float(x-10, y-10, 20, 20);
            g2d.draw(circulo);
        } 
    }

    @Override
    public void SetLocation(Point2D pos) {
        this.x = (float) pos.getX();
        this.y = (float) pos.getY();
    }

    @Override
    public void SetFrameFromDiagonal(Point2D p1, Point2D p2) {
        
    }

    @Override
    public boolean contains(Point2D p) {
        return area.contains(p);
    }
    
}
