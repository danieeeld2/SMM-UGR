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
 * Clase abstracta que representa los atributos y métodos comunes de las formas que se 
 * pueden dibujar en el lienzo
 * @author daniel
 */
public abstract class MiShape {
    protected Color color;
    protected Stroke trazo;
    protected boolean relleno;
    protected boolean alisar;
    protected boolean transparencia;
    protected boolean editar;

    /**
     * Devuelve el color de la forma
     * @return Color de la forma
     */
    public Color getColor() {
        return color;
    }

    /**
     * Establece el color de la forma
     * @param color Color a establecer en la forma
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Devuelve el trazo de la forma
     * @return trazo de la forma
     */
    public Stroke getTrazo() {
        return trazo;
    }

    /**
     * Establece el trazo de la forma
     * @param trazo Stroke a establecer en la forma
     */
    public void setTrazo(Stroke trazo) {
        this.trazo = trazo;
    }

    /**
     * Devuelve el estado de relleno de la forma
     * @return True si la forma se pinta rellenada, False en caso contrario
     */
    public boolean isRelleno() {
        return relleno;
    }

    /**
     * Establece el estado de relleno de la forma
     * @param relleno booleano que establece el estado
     */
    public void setRelleno(boolean relleno) {
        this.relleno = relleno;
    }

    /**
     * Indica si la forma ha de pintarse con alisado o no
     * @return True en caso afirmativo. False en caso contrario
     */
    public boolean isAlisar() {
        return alisar;
    }

    /**
     * Establece si la forma ha de pintarse o no con alisado
     * @param alisar booleano que establece el estado
     */
    public void setAlisar(boolean alisar) {
        this.alisar = alisar;
    }

    /**
     * Indica si la forma ha de pintarse con trasparencia o no
     * @return Devuelve True en caso afirmativo, False en caso contrario
     */
    public boolean isTransparencia() {
        return transparencia;
    }

    /**
     * Establece si la forma ha de pintarse con transparencia o no
     * @param transparencia booleano que establece el estado
     */
    public void setTransparencia(boolean transparencia) {
        this.transparencia = transparencia;
    }

    /**
     * Establece si la forma está en modo edición o no
     * @return Devuelve True en caso afirmativo, False en caso contrario
     */
    public boolean isEditar() {
        return editar;
    }

    /**
     * Estabece si la forma está en modo edición o no
     * @param editar booleano que establece el estado
     */
    public void setEditar(boolean editar) {
        this.editar = editar;
    }
    
    /**
     * Método que, dado un Graphics2D, toma los atributos relacionados con la forma en la que se pintará la figura
     * y la dibuja
     * @param g2d instancia de Graphics2D
     */
    public abstract void draw(Graphics2D g2d);
    
    /**
     * Método que, dado un punto, establece el origen de la figura a ese nuevo punto
     * @param pos punto donde se quiere colocar la figura
     */
    public abstract void SetLocation(Point2D pos);
    
    /**
     * Método que establece la figura a partir de la diagonal creada por dos puntos
     * @param p1 punto de partida
     * @param p2 punto final
     */
    public abstract void SetFrameFromDiagonal(Point2D p1, Point2D p2);
    
    /**
     * Método que comprueba si un punto está contenido dentro de la figura seleccionada
     * @param p punto a comprobar
     * @return true en caso de contener el punto, false en caso contrario
     */
    public abstract boolean contains(Point2D p);
}
