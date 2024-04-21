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
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author daniel
 */
public class DRectangulo extends MiShape {
    private float x;
    private float y;
    private float width;
    private float height;

    public DRectangulo(float x, float y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = (float) width;
        this.height = (float) height;
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

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
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
        
        Rectangle2D.Float linea = new Rectangle2D.Float(x, y, width, height);
        if(this.isRelleno()){
            g2d.fill(linea);
        }else{
            g2d.draw(linea);
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
        Rectangle2D.Float linea = new Rectangle2D.Float();
        linea.setFrameFromDiagonal(p1, p2);
        x = linea.x;
        y = linea.y;
        width = linea.width;
        height = linea.height;
    }

    @Override
    public boolean contains(Point2D p) {
        Rectangle2D.Float linea = new Rectangle2D.Float(x, y, width, height);
        return linea.contains(p);
    }
    
}
