/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sm.dav.graficos;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;

/**
 *
 * @author daniel
 */
abstract class MiShape {
    protected Color color;
    protected Stroke trazo;
    protected boolean relleno;
    protected boolean alisar;
    protected boolean transparencia;

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Stroke getTrazo() {
        return trazo;
    }

    public void setTrazo(Stroke trazo) {
        this.trazo = trazo;
    }

    public boolean isRelleno() {
        return relleno;
    }

    public void setRelleno(boolean relleno) {
        this.relleno = relleno;
    }

    public boolean isAlisar() {
        return alisar;
    }

    public void setAlisar(boolean alisar) {
        this.alisar = alisar;
    }

    public boolean isTransparencia() {
        return transparencia;
    }

    public void setTransparencia(boolean transparencia) {
        this.transparencia = transparencia;
    }
    
    public abstract void draw(Graphics2D g2d);
    
    public abstract void SetLocation(Point2D pos);
    
    public abstract void SetFrameFromDiagonal(Point2D p1, Point2D p2);
    
    public abstract boolean contains(Point2D p);
}
