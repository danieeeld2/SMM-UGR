/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package sm.dav.graficos;

import sm.dav.events.LienzoListener;
import sm.dav.events.LienzoEvent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author daniel
 */
public class Lienzo2D extends javax.swing.JPanel {
    private List<MiShape> listaFiguras = new ArrayList();
    private MiShape forma = new DLinea(0, 0, 0, 0);
    private Color color = Color.black;
    private boolean relleno = false;
    private boolean mover = false;
    public enum posiblesTipos {
        LINEA,
        RECTANGULO,
        ELIPSE,
        FANTASMA
    };
    private posiblesTipos tipo = posiblesTipos.LINEA;
    private Point2D pPressed = new Point2D.Float(0, 0);
    private boolean transparencia = false;
    private boolean alisar = false;
    Stroke trazo = new BasicStroke(1f);
    private BufferedImage img;
    ArrayList<LienzoListener> lienzoEventListeners = new ArrayList<>();
    /**
     * Creates new form Lienzo
     */
    public Lienzo2D() {
        initComponents();
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        if (img != null) {
            g2d.drawImage(img, 0, 0, this);
            g2d.clip(new Rectangle2D.Float(0, 0, img.getWidth(), img.getHeight()));
            // Dibujar limite de recorte
            float[] dashPattern = {5, 5};
            BasicStroke dashedStroke = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, dashPattern, 0);
            g2d.setStroke(dashedStroke);
            g2d.setColor(Color.BLACK);
            g2d.draw(new Line2D.Float(0.0f, img.getHeight(), img.getWidth(), img.getHeight()));
            g2d.draw(new Line2D.Float(img.getWidth(), 0.0f, img.getWidth(), img.getHeight()));
        }
        for (MiShape s : listaFiguras) {
            s.draw(g2d);
        }
    }
    
    /**
     * Método que devuelve la figura seleccionada dado un punto. Comprueba si el punto pasado como argumento
     * pertenece a alguna figura y, en caso afirmativo, devuelve la primera coincidencia
     * @param p punto a comprobar
     * @return La primera figura que cumpla que contiene al punto, null en caso de ninguna
     */
    private MiShape figuraSeleccionada(Point2D p){
        for(MiShape s:listaFiguras)
            if(s.contains(p)) return s;
        return null;
    }

    /**
     * Función para indicar a la forma seleccionada que se termina el modo edición
     */
    public void SalirModoEditar() {
        if(forma != null){
            forma.setEditar(false);
            this.repaint();
        }
    }
    
    /**
     * Función para limpiar el panel
     */
    public void limpiarPanel() {
        listaFiguras.clear();
        this.repaint();
    }

    /**
     * Función que establece si está o no el modo edición activado
     * @return true en caso de estar activado, false en caso contrario
     */
    public boolean isMover() {
        return mover;
    }

    /** 
     * Función que habilita y deshabilita el modo edición en el lienzo
     * @param mover habilitar/deshabilitar
     */
    public void setMover(boolean mover) {
        this.mover = mover;
    }

    /**
     * Función que devuelve el color interno establecido en el lienzo
     * @return el color establecido
     */
    public Color getColor() {
        return color;
    }

    /**
     * Función que establece el color interno en el lienzo
     * @param color color a establecer
     */
    public void setColor(Color color) {
        if(mover){
            if(forma != null) {
                forma.setColor(color);
                this.repaint();
            }
        }
        this.color = color;
    }

    /**
     * Función que indica si esta habilitado o deshabilitado el modo de relleno
     * @return true en caso afirmativo, false en caso contrario
     */
    public boolean isRelleno() {
        return relleno;
    }

    /**
     * Establece el modo de relleno en el lienzo
     * @param relleno habilitar/deshabilitar
     */
    public void setRelleno(boolean relleno) {
        if(mover){
            if(forma != null) {
                forma.setRelleno(relleno);
                this.repaint();
            }
        }
        this.relleno = relleno;
    }

    public posiblesTipos getTipo() {
        return tipo;
    }

    public void setTipo(posiblesTipos tipo) {
        this.tipo = tipo;
    }

    public boolean isTransparencia() {
        return transparencia;
    }

    public void setTransparencia(boolean transparencia) {
        if(mover){
            if(forma != null) {
                forma.setTransparencia(transparencia);
                this.repaint();
            }
        }
        this.transparencia = transparencia;
    }

    public boolean isAlisar() {
        return alisar;
    }

    public void setAlisar(boolean alisar) {
        if(mover){
            if(forma != null) {
                forma.setAlisar(alisar);
                this.repaint();
            }
        }
        this.alisar = alisar;
    }

    public Stroke getTrazo() {
        return trazo;
    }

    public void setTrazo(int grosor) {
        if(mover){
            if(forma != null) {
                forma.setTrazo(new BasicStroke((float) grosor));
                this.repaint();
            }
        }
        this.trazo = new BasicStroke((float) grosor);
    }
    
    public void setImage(BufferedImage img) {
        this.img = img;
        if (img != null) {
            setPreferredSize(new Dimension(img.getWidth(), img.getHeight()));
        }
    }

    public BufferedImage getImage() {
        return img;
    }
    
    public BufferedImage getPaintedImage() {
        BufferedImage imgout = new BufferedImage(img.getWidth(),
                img.getHeight(),
                img.getType());
        Graphics2D g2dImagen = imgout.createGraphics();
        if(img!=null) g2dImagen.drawImage(img,0,0,this);
        for (MiShape s : listaFiguras) {
            s.draw(g2dImagen);
        }
        return imgout;
    }
    
    public void VolcarSeleccion(){
        if (mover) { // Modo edición activo
            if (forma != null && forma.isEditar()) { //Hay una forma que ha sido seleccionada en este modo
                BufferedImage imgout = new BufferedImage(img.getWidth(),
                        img.getHeight(),
                        img.getType());
                Graphics2D g2dImagen = imgout.createGraphics();
                if (img != null) {
                    g2dImagen.drawImage(img, 0, 0, this);
                }
                forma.setEditar(false);
                forma.draw(g2dImagen);
                listaFiguras.remove(forma);
                this.img = imgout;
                this.repaint();
            }
        }

    }
    
    public void addLienzoListener(LienzoListener listener) {
        if(listener != null) {
            lienzoEventListeners.add(listener);
        }
    }
    
    private void notifyShapeSelectedEvent(LienzoEvent evt) {
        if(!lienzoEventListeners.isEmpty()) {
            for(LienzoListener listener : lienzoEventListeners) {
                listener.shapeSelected(evt);
            }
        }
    }
    
    private void notifyShapeAddedEvent(LienzoEvent evt) {
        if(!lienzoEventListeners.isEmpty()) {
            for(LienzoListener listener : lienzoEventListeners) {
                listener.shapeAdded(evt);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Función que gestiona el evento MouseDragged del lienzo. Comprueba si está habilitado o no
     * el modo edición. En caso de estar habilitado, mueve la forma seleccionada con el cursor. En caso contrario,
     * redimensiona la figura recién creada hasta donde desplacemos el cursor
     * @param evt 
     */
    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        if(mover){
            if(forma != null){
                forma.SetLocation(evt.getPoint());
            }
        } else {
            forma.SetFrameFromDiagonal(pPressed, evt.getPoint());
        }
        this.repaint();
    }//GEN-LAST:event_formMouseDragged

    /**
     * Función que gestiona el evento de MousePressed en el lienzo. Esta función comprueba si está habilitado
     * el modo edición o no. Si no está habilitado, comprueba el tipo de forma seleccionada y crea una nueva forma
     * de ese tipo, la cual añade al lienzo. En caso contrario, selecciona la forma que contenga al punto y le habilita el
     * modo edición
     * @param evt 
     */
    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        if (forma != null) { // Le decimos que saque del modo edición a la última forma
            forma.setEditar(false);
        }
        if(mover){
            forma = figuraSeleccionada(evt.getPoint());
            if(forma != null) {
                forma.setEditar(true);
                notifyShapeSelectedEvent(new LienzoEvent(this, forma));
            }
            this.repaint();
        } else {
            switch (tipo){
            case LINEA -> {
                // forma = new Line2D.Float(evt.getPoint(),evt.getPoint());
                forma = new DLinea(evt.getPoint(), evt.getPoint());
                }
            case ELIPSE -> {
                // forma = new Ellipse2D.Float(evt.getPoint().x,evt.getPoint().y,0,0);
                forma = new DElipse(evt.getPoint().x, evt.getPoint().y, 0, 0);
                }
            case RECTANGULO -> {
                // forma = new Rectangle(evt.getPoint());
                forma = new DRectangulo(evt.getPoint().x, evt.getPoint().y, 0, 0);
                }
            case FANTASMA -> {
                forma = new DFantasma(evt.getPoint().x, evt.getPoint().y);
                }
            }
            pPressed = evt.getPoint();
            forma.setColor(color);
            forma.setAlisar(alisar);
            forma.setRelleno(relleno);
            forma.setTransparencia(transparencia);
            forma.setTrazo(trazo);
            forma.setEditar(false);
            listaFiguras.add(forma);
            notifyShapeAddedEvent(new LienzoEvent(this,forma));
        }
    }//GEN-LAST:event_formMousePressed

    /**
     * Evento MouseClicked del lienzo. Comprueba si está habilitado el modo edición y, en caso afirmativo, toma
     * la forma que contenga al punto donde se hizo click y le habilita el modo edición
     * @param evt 
     */
    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        if(mover){
            if(forma != null) { // Le decimos que saque del modo edición a la última forma
                forma.setEditar(false);
            }
            forma = figuraSeleccionada(evt.getPoint());
            if(forma != null) {
                forma.setEditar(true);
            }
            this.repaint();
        }
    }//GEN-LAST:event_formMouseClicked
                              

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
