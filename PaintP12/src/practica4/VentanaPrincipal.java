/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package practica4;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BandCombineOp;
import java.awt.image.BufferedImage;
import java.awt.image.ByteLookupTable;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.DataBuffer;
import java.awt.image.Kernel;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.RescaleOp;
import java.awt.image.WritableRaster;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.filechooser.FileNameExtensionFilter;
import sm.dav.graficos.Lienzo2D;
import sm.dav.events.LienzoAdapter;
import sm.dav.events.LienzoEvent;
import sm.dav.graficos.MiShape;
import sm.dav.imagen.CambiarColorOp;
import sm.dav.imagen.PosterizarOp;
import sm.dav.imagen.RojoOp;
import sm.dav.imagen.SaturacionOp;
import sm.image.EqualizationOp;
import sm.image.KernelProducer;
import sm.image.LookupTableProducer;
import sm.image.SepiaOp;
import sm.image.TintOp;

/**
 *
 * @author daniel
 */
public class VentanaPrincipal extends javax.swing.JFrame {

    private ManejadorVentanaInterna manejador;
    private BufferedImage imgFuente;

    /**
     * Creates new form VentanaPrincipal
     */
    public VentanaPrincipal() {
        initComponents();
        setSize(1500, 960);
        // 2) Creamos objeto manejador
        manejador = new ManejadorVentanaInterna();
    }

    /**
     * Devuelve el lienzo asociado a un InternalFrame del Escritorio
     *
     * @return Lienzo2D de una ventana interna
     */
    private Lienzo2D getSelectedLienzo() {
        VentanaInterna vi;
        vi = (VentanaInterna) escritorio.getSelectedFrame();
        return vi != null ? vi.getLienzo2D() : null;
    }

    /**
     * Método que, dado un lienzo, toma los atributos de dicho lienzo y los
     * coloca en la barra de herramientas, para que esta esté siempre
     * actualizada y en sintonía con el lienzo que estamos trabajando
     *
     * @param l Lienzo2D del que se van a tomar los atributos
     */
    private void setAtributosSelectedLienzo(Lienzo2D l) {
        if (l != null) {
            this.Alisar.setSelected(l.isAlisar());
            this.Mover.setSelected(l.isMover());
            this.Transparencia.setSelected(l.isTransparencia());
            this.Relleno.setSelected(l.isRelleno());
            this.Paleta.setBackground(l.getColor());
            this.Grosor.setValue(l.getTrazo().hashCode());
            l.setTipo(Lienzo2D.posiblesTipos.LINEA);
            this.BotonLinea.setSelected(true);
        }
    }

    /**
     * Método que, dado una forma, toma los atributos de dicha forma y los
     * coloca en la barra de herramientas, para que esta esté siempre
     * actualizada y en sintonía
     *
     * @param forma MiShape del cual se van a tomar los atributos
     */
    private void setAtributosSelectedForma(MiShape forma) {
        if (forma != null) {
            this.Alisar.setSelected(forma.isAlisar());
            this.Mover.setSelected(forma.isEditar());
            this.Transparencia.setSelected(forma.isTransparencia());
            this.Relleno.setSelected(forma.isRelleno());
            this.Paleta.setBackground(forma.getColor());
            this.Grosor.setValue(forma.getTrazo().hashCode());
        }
    }

    // 1) Definimos clase manejadora
    /**
     * Clase manejadora para eventos de la ventana interna activa desde la
     * ventana principal
     */
    private class ManejadorVentanaInterna extends InternalFrameAdapter {

        /**
         * Evento que toma los atributos del lienzo de la ventana interna activa
         * y se encarga de colocar dichos valores en la barra de herramientas de
         * la aplicación, para que esta esté actualizada
         *
         * @param evt
         */
        public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            VentanaInterna vi = (VentanaInterna) evt.getInternalFrame();
            setAtributosSelectedLienzo(vi.getLienzo2D());
        }
    }

    /**
     * Clase manejadora para manejar eventos de las formas asociadas a un lienzo
     * desde la ventana principañ
     */
    public class MiManejadorLienzo extends LienzoAdapter {

        /**
         * Método que imprime por terminal una forma cuando esta es añadida a
         * uno de los lienzos
         *
         * @param evt
         */
        @Override
        public void shapeAdded(LienzoEvent evt) {
            System.out.println("Figura " + evt.getForma() + " añadida");
        }

        /**
         * Método que establece los atributos de una forma seleccionada en la
         * barra de herramientas
         *
         * @param evt
         */
        @Override
        public void shapeSelected(LienzoEvent evt) {
            setAtributosSelectedForma(evt.getForma());
        }
    }
    
    /**
     * Clase manejadora para eventos de mouseMoved sobre un lienzo. Sirve para coger el color RGB del pixel e indicarlo
     * en la barra de estado
     */
    public class LienzoMove extends MouseMotionAdapter {

        @Override
        public void mouseMoved(MouseEvent e) {
            PointerInfo pointerInfo = MouseInfo.getPointerInfo();
            Point point = pointerInfo.getLocation();
            int x = (int) point.getX();
            int y = (int) point.getY();
            try {
                Robot robot = new Robot();
                Color color = robot.getPixelColor(x, y);
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();
                RGBLabel.setText("(R: " + red + ", G: " + green + ", B: " + blue + ")");
                // System.out.println("RGB: " + red + ", " + green + ", " + blue);
            } catch (Exception ex) {
                ex.printStackTrace();
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

        TipoDibujo = new javax.swing.ButtonGroup();
        jToolBar1 = new javax.swing.JToolBar();
        Nuevo2 = new javax.swing.JButton();
        Abrir2 = new javax.swing.JButton();
        Guardar2 = new javax.swing.JButton();
        Duplicar = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        BotonLinea = new javax.swing.JToggleButton();
        BotonRectangulo = new javax.swing.JToggleButton();
        BotonElipse = new javax.swing.JToggleButton();
        Fantasma = new javax.swing.JToggleButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        Mover = new javax.swing.JToggleButton();
        jPanel1 = new javax.swing.JPanel();
        Paleta = new javax.swing.JButton();
        Relleno = new javax.swing.JToggleButton();
        Transparencia = new javax.swing.JToggleButton();
        Alisar = new javax.swing.JToggleButton();
        Grosor = new javax.swing.JSlider();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        Volcar = new javax.swing.JButton();
        Estado = new javax.swing.JLabel();
        escritorio = new javax.swing.JDesktopPane();
        jToolBar2 = new javax.swing.JToolBar();
        jLabel1 = new javax.swing.JLabel();
        Brillo = new javax.swing.JSlider();
        jLabel2 = new javax.swing.JLabel();
        Contraste = new javax.swing.JSlider();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        Filtros = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        Cometa = new javax.swing.JSlider();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        ContrasteNormal = new javax.swing.JButton();
        ContrasteIluminacion = new javax.swing.JButton();
        ContrasteOscurecimiento = new javax.swing.JButton();
        OscurecerClaros = new javax.swing.JButton();
        TLinealGraph = new javax.swing.JButton();
        TLineal = new javax.swing.JSlider();
        Negativo = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        Rotar180 = new javax.swing.JButton();
        Aumentar = new javax.swing.JButton();
        Decrementar = new javax.swing.JButton();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        Bandas = new javax.swing.JButton();
        EspacioColor = new javax.swing.JComboBox<>();
        jSeparator8 = new javax.swing.JToolBar.Separator();
        CombinarBandas = new javax.swing.JButton();
        Tintar = new javax.swing.JButton();
        TintarDeslizador = new javax.swing.JSlider();
        Sepia = new javax.swing.JButton();
        Ecualizacion = new javax.swing.JButton();
        Rojo = new javax.swing.JButton();
        RojoDeslizador = new javax.swing.JSlider();
        PosterizarIcon = new javax.swing.JButton();
        Posterizar = new javax.swing.JSlider();
        jSeparator9 = new javax.swing.JToolBar.Separator();
        jPanel2 = new javax.swing.JPanel();
        ColorCambio1 = new javax.swing.JButton();
        CambiarColor = new javax.swing.JSlider();
        jPanel3 = new javax.swing.JPanel();
        ColorCambio2 = new javax.swing.JButton();
        jSeparator10 = new javax.swing.JToolBar.Separator();
        S = new javax.swing.JLabel();
        SaturacionSlider = new javax.swing.JSlider();
        RGBLabel = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        Nuevo = new javax.swing.JMenuItem();
        Abrir = new javax.swing.JMenuItem();
        Guardar = new javax.swing.JMenuItem();
        MenuImagenes = new javax.swing.JMenu();
        rescalar = new javax.swing.JMenuItem();
        convolucion = new javax.swing.JMenuItem();
        tranfAfin = new javax.swing.JMenuItem();
        lookUp = new javax.swing.JMenuItem();
        combBanda = new javax.swing.JMenuItem();
        colorConv = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("PaintBásico");

        jToolBar1.setRollover(true);

        Nuevo2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practica4/nuevo.png"))); // NOI18N
        Nuevo2.setFocusable(false);
        Nuevo2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Nuevo2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Nuevo2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Nuevo2ActionPerformed(evt);
            }
        });
        jToolBar1.add(Nuevo2);

        Abrir2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practica4/abrir.png"))); // NOI18N
        Abrir2.setFocusable(false);
        Abrir2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Abrir2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Abrir2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Abrir2ActionPerformed(evt);
            }
        });
        jToolBar1.add(Abrir2);

        Guardar2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practica4/guardar.png"))); // NOI18N
        Guardar2.setFocusable(false);
        Guardar2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Guardar2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Guardar2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Guardar2ActionPerformed(evt);
            }
        });
        jToolBar1.add(Guardar2);

        Duplicar.setText("x2");
        Duplicar.setFocusable(false);
        Duplicar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Duplicar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Duplicar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DuplicarActionPerformed(evt);
            }
        });
        jToolBar1.add(Duplicar);
        jToolBar1.add(jSeparator4);

        TipoDibujo.add(BotonLinea);
        BotonLinea.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practica4/linea.png"))); // NOI18N
        BotonLinea.setFocusable(false);
        BotonLinea.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        BotonLinea.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        BotonLinea.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonLineaActionPerformed(evt);
            }
        });
        jToolBar1.add(BotonLinea);

        TipoDibujo.add(BotonRectangulo);
        BotonRectangulo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practica4/rectangulo.png"))); // NOI18N
        BotonRectangulo.setFocusable(false);
        BotonRectangulo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        BotonRectangulo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        BotonRectangulo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonRectanguloActionPerformed(evt);
            }
        });
        jToolBar1.add(BotonRectangulo);

        TipoDibujo.add(BotonElipse);
        BotonElipse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practica4/elipse.png"))); // NOI18N
        BotonElipse.setFocusable(false);
        BotonElipse.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        BotonElipse.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        BotonElipse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonElipseActionPerformed(evt);
            }
        });
        jToolBar1.add(BotonElipse);

        TipoDibujo.add(Fantasma);
        Fantasma.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practica4/fantasma.png"))); // NOI18N
        Fantasma.setFocusable(false);
        Fantasma.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Fantasma.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Fantasma.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FantasmaActionPerformed(evt);
            }
        });
        jToolBar1.add(Fantasma);
        jToolBar1.add(jSeparator1);

        TipoDibujo.add(Mover);
        Mover.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practica4/seleccion.png"))); // NOI18N
        Mover.setFocusable(false);
        Mover.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Mover.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Mover.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MoverActionPerformed(evt);
            }
        });
        jToolBar1.add(Mover);

        jPanel1.setMaximumSize(new java.awt.Dimension(40, 40));
        jPanel1.setPreferredSize(new java.awt.Dimension(30, 30));
        jPanel1.setLayout(new java.awt.BorderLayout());

        Paleta.setBackground(new java.awt.Color(0, 0, 0));
        Paleta.setForeground(new java.awt.Color(0, 0, 0));
        Paleta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PaletaActionPerformed(evt);
            }
        });
        jPanel1.add(Paleta, java.awt.BorderLayout.CENTER);

        jToolBar1.add(jPanel1);

        Relleno.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practica4/rellenar.png"))); // NOI18N
        Relleno.setFocusable(false);
        Relleno.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Relleno.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Relleno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RellenoActionPerformed(evt);
            }
        });
        jToolBar1.add(Relleno);

        Transparencia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practica4/transparencia.png"))); // NOI18N
        Transparencia.setFocusable(false);
        Transparencia.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Transparencia.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Transparencia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TransparenciaActionPerformed(evt);
            }
        });
        jToolBar1.add(Transparencia);

        Alisar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practica4/alisar.png"))); // NOI18N
        Alisar.setFocusable(false);
        Alisar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Alisar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Alisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AlisarActionPerformed(evt);
            }
        });
        jToolBar1.add(Alisar);

        Grosor.setMaximum(50);
        Grosor.setMinimum(1);
        Grosor.setValue(1);
        Grosor.setMaximumSize(new java.awt.Dimension(100, 16));
        Grosor.setPreferredSize(new java.awt.Dimension(100, 16));
        Grosor.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                GrosorStateChanged(evt);
            }
        });
        jToolBar1.add(Grosor);
        jToolBar1.add(jSeparator2);

        Volcar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practica4/volcado.png"))); // NOI18N
        Volcar.setFocusable(false);
        Volcar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Volcar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Volcar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                VolcarActionPerformed(evt);
            }
        });
        jToolBar1.add(Volcar);

        Estado.setText("Barra de Estado");

        javax.swing.GroupLayout escritorioLayout = new javax.swing.GroupLayout(escritorio);
        escritorio.setLayout(escritorioLayout);
        escritorioLayout.setHorizontalGroup(
            escritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1503, Short.MAX_VALUE)
        );
        escritorioLayout.setVerticalGroup(
            escritorioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 299, Short.MAX_VALUE)
        );

        jToolBar2.setRollover(true);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practica4/brillo.png"))); // NOI18N
        jToolBar2.add(jLabel1);

        Brillo.setMaximum(255);
        Brillo.setMinimum(-255);
        Brillo.setValue(0);
        Brillo.setMaximumSize(new java.awt.Dimension(70, 16));
        Brillo.setMinimumSize(new java.awt.Dimension(15, 16));
        Brillo.setPreferredSize(new java.awt.Dimension(60, 16));
        Brillo.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                BrilloStateChanged(evt);
            }
        });
        Brillo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                BrilloFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                BrilloFocusLost(evt);
            }
        });
        jToolBar2.add(Brillo);

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practica4/contraste.png"))); // NOI18N
        jToolBar2.add(jLabel2);

        Contraste.setMaximum(20);
        Contraste.setMinimum(1);
        Contraste.setValue(10);
        Contraste.setMaximumSize(new java.awt.Dimension(70, 16));
        Contraste.setMinimumSize(new java.awt.Dimension(15, 16));
        Contraste.setPreferredSize(new java.awt.Dimension(60, 16));
        Contraste.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ContrasteStateChanged(evt);
            }
        });
        Contraste.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                ContrasteFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                ContrasteFocusLost(evt);
            }
        });
        jToolBar2.add(Contraste);
        jToolBar2.add(jSeparator3);

        Filtros.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Media", "Binomial", "Enfoque", "Relieve", "Laplaciano", "Media_5x5", "Media_7x7", "Emborronamiento_Iluminado_3x3", "Emborronamiento_Iluminado_5x5" }));
        Filtros.setMaximumSize(new java.awt.Dimension(85, 32767));
        Filtros.setPreferredSize(new java.awt.Dimension(80, 25));
        Filtros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FiltrosActionPerformed(evt);
            }
        });
        jToolBar2.add(Filtros);

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practica4/cometa.png"))); // NOI18N
        jToolBar2.add(jLabel3);

        Cometa.setMaximum(30);
        Cometa.setMinimum(1);
        Cometa.setValue(1);
        Cometa.setMaximumSize(new java.awt.Dimension(70, 16));
        Cometa.setPreferredSize(new java.awt.Dimension(65, 16));
        Cometa.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                CometaStateChanged(evt);
            }
        });
        Cometa.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                CometaFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                CometaFocusLost(evt);
            }
        });
        jToolBar2.add(Cometa);
        jToolBar2.add(jSeparator5);

        ContrasteNormal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practica4/contraste2.png"))); // NOI18N
        ContrasteNormal.setFocusable(false);
        ContrasteNormal.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ContrasteNormal.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        ContrasteNormal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ContrasteNormalActionPerformed(evt);
            }
        });
        jToolBar2.add(ContrasteNormal);

        ContrasteIluminacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practica4/iluminar.png"))); // NOI18N
        ContrasteIluminacion.setFocusable(false);
        ContrasteIluminacion.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ContrasteIluminacion.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        ContrasteIluminacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ContrasteIluminacionActionPerformed(evt);
            }
        });
        jToolBar2.add(ContrasteIluminacion);

        ContrasteOscurecimiento.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practica4/ocurecer.png"))); // NOI18N
        ContrasteOscurecimiento.setFocusable(false);
        ContrasteOscurecimiento.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ContrasteOscurecimiento.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        ContrasteOscurecimiento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ContrasteOscurecimientoActionPerformed(evt);
            }
        });
        jToolBar2.add(ContrasteOscurecimiento);

        OscurecerClaros.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practica4/operador1.png"))); // NOI18N
        OscurecerClaros.setFocusable(false);
        OscurecerClaros.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        OscurecerClaros.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        OscurecerClaros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OscurecerClarosActionPerformed(evt);
            }
        });
        jToolBar2.add(OscurecerClaros);

        TLinealGraph.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practica4/operador2.png"))); // NOI18N
        TLinealGraph.setFocusable(false);
        TLinealGraph.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        TLinealGraph.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(TLinealGraph);

        TLineal.setMaximum(255);
        TLineal.setValue(128);
        TLineal.setMaximumSize(new java.awt.Dimension(70, 16));
        TLineal.setPreferredSize(new java.awt.Dimension(65, 16));
        TLineal.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TLinealStateChanged(evt);
            }
        });
        TLineal.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                TLinealFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                TLinealFocusLost(evt);
            }
        });
        jToolBar2.add(TLineal);

        Negativo.setText("Neg-");
        Negativo.setToolTipText("");
        Negativo.setFocusable(false);
        Negativo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Negativo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Negativo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NegativoActionPerformed(evt);
            }
        });
        jToolBar2.add(Negativo);
        jToolBar2.add(jSeparator6);

        Rotar180.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practica4/rotar180.png"))); // NOI18N
        Rotar180.setFocusable(false);
        Rotar180.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Rotar180.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Rotar180.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Rotar180ActionPerformed(evt);
            }
        });
        jToolBar2.add(Rotar180);

        Aumentar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practica4/mas.png"))); // NOI18N
        Aumentar.setFocusable(false);
        Aumentar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Aumentar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Aumentar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AumentarActionPerformed(evt);
            }
        });
        jToolBar2.add(Aumentar);

        Decrementar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practica4/menos.png"))); // NOI18N
        Decrementar.setFocusable(false);
        Decrementar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Decrementar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Decrementar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DecrementarActionPerformed(evt);
            }
        });
        jToolBar2.add(Decrementar);
        jToolBar2.add(jSeparator7);

        Bandas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practica4/bandas.png"))); // NOI18N
        Bandas.setFocusable(false);
        Bandas.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Bandas.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Bandas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BandasActionPerformed(evt);
            }
        });
        jToolBar2.add(Bandas);

        EspacioColor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "RGB", "YCC", "GREY" }));
        EspacioColor.setMaximumSize(new java.awt.Dimension(100, 32767));
        EspacioColor.setPreferredSize(new java.awt.Dimension(70, 25));
        EspacioColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EspacioColorActionPerformed(evt);
            }
        });
        jToolBar2.add(EspacioColor);
        jToolBar2.add(jSeparator8);

        CombinarBandas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practica4/combinar.png"))); // NOI18N
        CombinarBandas.setFocusable(false);
        CombinarBandas.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        CombinarBandas.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        CombinarBandas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CombinarBandasActionPerformed(evt);
            }
        });
        jToolBar2.add(CombinarBandas);

        Tintar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practica4/tintar.png"))); // NOI18N
        Tintar.setFocusable(false);
        Tintar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Tintar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Tintar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TintarActionPerformed(evt);
            }
        });
        jToolBar2.add(Tintar);

        TintarDeslizador.setValue(0);
        TintarDeslizador.setMaximumSize(new java.awt.Dimension(70, 16));
        TintarDeslizador.setPreferredSize(new java.awt.Dimension(65, 16));
        TintarDeslizador.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TintarDeslizadorStateChanged(evt);
            }
        });
        TintarDeslizador.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                TintarDeslizadorFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                TintarDeslizadorFocusLost(evt);
            }
        });
        jToolBar2.add(TintarDeslizador);

        Sepia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practica4/sepia.png"))); // NOI18N
        Sepia.setFocusable(false);
        Sepia.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Sepia.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Sepia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SepiaActionPerformed(evt);
            }
        });
        jToolBar2.add(Sepia);

        Ecualizacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practica4/ecualizar.png"))); // NOI18N
        Ecualizacion.setFocusable(false);
        Ecualizacion.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Ecualizacion.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Ecualizacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EcualizacionActionPerformed(evt);
            }
        });
        jToolBar2.add(Ecualizacion);

        Rojo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practica4/rojo.png"))); // NOI18N
        Rojo.setFocusable(false);
        Rojo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Rojo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Rojo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RojoActionPerformed(evt);
            }
        });
        jToolBar2.add(Rojo);

        RojoDeslizador.setMaximum(255);
        RojoDeslizador.setMinimum(-510);
        RojoDeslizador.setValue(20);
        RojoDeslizador.setMaximumSize(new java.awt.Dimension(70, 16));
        RojoDeslizador.setPreferredSize(new java.awt.Dimension(65, 16));
        RojoDeslizador.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                RojoDeslizadorStateChanged(evt);
            }
        });
        RojoDeslizador.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                RojoDeslizadorFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                RojoDeslizadorFocusLost(evt);
            }
        });
        jToolBar2.add(RojoDeslizador);

        PosterizarIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/practica4/posterizar.png"))); // NOI18N
        PosterizarIcon.setFocusable(false);
        PosterizarIcon.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        PosterizarIcon.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(PosterizarIcon);

        Posterizar.setMaximum(20);
        Posterizar.setMinimum(2);
        Posterizar.setValue(2);
        Posterizar.setMaximumSize(new java.awt.Dimension(70, 16));
        Posterizar.setPreferredSize(new java.awt.Dimension(65, 16));
        Posterizar.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                PosterizarStateChanged(evt);
            }
        });
        Posterizar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                PosterizarFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                PosterizarFocusLost(evt);
            }
        });
        jToolBar2.add(Posterizar);
        jToolBar2.add(jSeparator9);

        jPanel2.setMaximumSize(new java.awt.Dimension(16, 16));
        jPanel2.setPreferredSize(new java.awt.Dimension(16, 16));
        jPanel2.setLayout(new java.awt.BorderLayout());

        ColorCambio1.setBackground(new java.awt.Color(204, 0, 0));
        ColorCambio1.setFocusable(false);
        ColorCambio1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ColorCambio1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        ColorCambio1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ColorCambio1ActionPerformed(evt);
            }
        });
        jPanel2.add(ColorCambio1, java.awt.BorderLayout.CENTER);

        jToolBar2.add(jPanel2);

        CambiarColor.setMaximum(360);
        CambiarColor.setValue(0);
        CambiarColor.setMaximumSize(new java.awt.Dimension(70, 16));
        CambiarColor.setPreferredSize(new java.awt.Dimension(65, 16));
        CambiarColor.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                CambiarColorStateChanged(evt);
            }
        });
        CambiarColor.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                CambiarColorFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                CambiarColorFocusLost(evt);
            }
        });
        jToolBar2.add(CambiarColor);

        jPanel3.setMaximumSize(new java.awt.Dimension(16, 16));
        jPanel3.setPreferredSize(new java.awt.Dimension(16, 16));
        jPanel3.setLayout(new java.awt.BorderLayout());

        ColorCambio2.setBackground(new java.awt.Color(255, 255, 51));
        ColorCambio2.setFocusable(false);
        ColorCambio2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ColorCambio2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        ColorCambio2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ColorCambio2ActionPerformed(evt);
            }
        });
        jPanel3.add(ColorCambio2, java.awt.BorderLayout.CENTER);

        jToolBar2.add(jPanel3);
        jToolBar2.add(jSeparator10);

        S.setText("Sat.");
        jToolBar2.add(S);

        SaturacionSlider.setValue(0);
        SaturacionSlider.setMaximumSize(new java.awt.Dimension(70, 16));
        SaturacionSlider.setPreferredSize(new java.awt.Dimension(65, 16));
        SaturacionSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                SaturacionSliderStateChanged(evt);
            }
        });
        SaturacionSlider.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                SaturacionSliderFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                SaturacionSliderFocusLost(evt);
            }
        });
        jToolBar2.add(SaturacionSlider);

        RGBLabel.setText("RGB");

        jMenu1.setText("Archivo");

        Nuevo.setText("Nuevo");
        Nuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NuevoActionPerformed(evt);
            }
        });
        jMenu1.add(Nuevo);

        Abrir.setText("Abrir");
        Abrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AbrirActionPerformed(evt);
            }
        });
        jMenu1.add(Abrir);

        Guardar.setText("Guardar");
        Guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GuardarActionPerformed(evt);
            }
        });
        jMenu1.add(Guardar);

        jMenuBar1.add(jMenu1);

        MenuImagenes.setText("Imagenes");

        rescalar.setText("RescaleOp");
        rescalar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rescalarActionPerformed(evt);
            }
        });
        MenuImagenes.add(rescalar);

        convolucion.setText("ConvolveOp");
        convolucion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                convolucionActionPerformed(evt);
            }
        });
        MenuImagenes.add(convolucion);

        tranfAfin.setText("AffineTransformOp");
        tranfAfin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tranfAfinActionPerformed(evt);
            }
        });
        MenuImagenes.add(tranfAfin);

        lookUp.setText("LookUpOp");
        lookUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lookUpActionPerformed(evt);
            }
        });
        MenuImagenes.add(lookUp);

        combBanda.setText("BandCombineOp");
        combBanda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combBandaActionPerformed(evt);
            }
        });
        MenuImagenes.add(combBanda);

        colorConv.setText("ColorConvertOp");
        colorConv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorConvActionPerformed(evt);
            }
        });
        MenuImagenes.add(colorConv);

        jMenuBar1.add(MenuImagenes);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 1503, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(Estado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(RGBLabel)
                .addContainerGap())
            .addComponent(escritorio)
            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(escritorio)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Estado, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(RGBLabel)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Evento Action del botón que permite crear una nueva ventana interna. El
     * método lanza un diálogo para preguntar por el ancho y altura de la imagen
     * del lienzo asociado. Si los valores son correctos, crea una imagen de ese
     * tamaño. En caso contrario,lo crea de 400x400. Además, crea la ventana
     * interna, la enlaza con el manejador y establece la imagen de fondo del
     * lienzo
     *
     * @param evt
     */
    private void NuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NuevoActionPerformed
        BufferedImage img;
        JPanel panel = new JPanel();
        JTextField textField1 = new JTextField(5);
        JTextField textField2 = new JTextField(5);
        panel.add(new JLabel("Introducir Ancho:"));
        panel.add(textField1);
        panel.add(Box.createHorizontalStrut(15));
        panel.add(new JLabel("Introducir Alto:"));
        panel.add(textField2);
        int result = JOptionPane.showConfirmDialog(null, panel, "Ingrese el tamaño de la imagen",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int valor1 = Integer.parseInt(textField1.getText());
                int valor2 = Integer.parseInt(textField2.getText());
                img = new BufferedImage(valor1, valor2, BufferedImage.TYPE_INT_ARGB);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Valores no válidos, se creará tamaño por defecto.", "Error", JOptionPane.ERROR_MESSAGE);
                img = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
            }
        } else {
            img = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
        }

        VentanaInterna vi = new VentanaInterna();
        escritorio.add(vi);
        vi.setVisible(true);
        setAtributosSelectedLienzo(vi.getLienzo2D());

        // 2) Enlazar generador con manejador
        vi.addInternalFrameListener(manejador);

        // Pintamos de blanco
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, img.getWidth(), img.getHeight());
        g2d.dispose();

        vi.getLienzo2D().setImage(img);
        
        // Enlazar con el manejador para obtener RGB
        vi.getLienzo2D().addMouseMotionListener(new LienzoMove());

        MiManejadorLienzo manejadorLienzo = new MiManejadorLienzo();
        vi.getLienzo2D().addLienzoListener(manejadorLienzo);
    }//GEN-LAST:event_NuevoActionPerformed

    /**
     * Evento Action del Boton Línea. Entra en el modo de dibujado de líneas
     *
     * @param evt
     */
    private void BotonLineaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonLineaActionPerformed
        Lienzo2D lienzo = getSelectedLienzo();
        if (lienzo != null) {
            lienzo.setMover(Mover.isSelected());
            lienzo.SalirModoEditar();
            lienzo.setTipo(Lienzo2D.posiblesTipos.LINEA);
            Estado.setText("Línea");
        }
    }//GEN-LAST:event_BotonLineaActionPerformed

    /**
     * Evento Action del Boton Rectangulo. Entra en el modo de dibujado de
     * rectangulos
     *
     * @param evt
     */
    private void BotonRectanguloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonRectanguloActionPerformed
        Lienzo2D lienzo = getSelectedLienzo();
        if (lienzo != null) {
            lienzo.setMover(Mover.isSelected());
            lienzo.SalirModoEditar();
            lienzo.setTipo(Lienzo2D.posiblesTipos.RECTANGULO);
            Estado.setText("Rectángulo");
        }
    }//GEN-LAST:event_BotonRectanguloActionPerformed

    /**
     * Evento Action del Boton Elipse. Entra en el modo de dibujado de elipses
     *
     * @param evt
     */
    private void BotonElipseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonElipseActionPerformed
        Lienzo2D lienzo = getSelectedLienzo();
        if (lienzo != null) {
            lienzo.setMover(Mover.isSelected());
            lienzo.SalirModoEditar();
            lienzo.setTipo(Lienzo2D.posiblesTipos.ELIPSE);
            Estado.setText("Elipse");
        }
    }//GEN-LAST:event_BotonElipseActionPerformed

    /**
     * Evento Action del Boton Abrir. Permite abrir una imagen, a través de un
     * diálodo, en formatos jpg, jpeg, png, gif y bmp. En caso de éxito, crea
     * una nueva ventana interna y crea una imagen con canal alfa a partir de la
     * original, la cual establece como fondo del lienzo asociado
     *
     * @param evt
     */
    private void AbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AbrirActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Imágenes", "jpg", "jpeg", "png", "gif", "bmp");
        fileChooser.setFileFilter(filter);
        int seleccion = fileChooser.showOpenDialog(this);
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            try {
                File f = fileChooser.getSelectedFile();
                BufferedImage img = ImageIO.read(f);

                // Verificar si la imagen original tiene canal alfa
                boolean hasAlpha = img.getColorModel().hasAlpha();

                // Crear una nueva imagen con canal alfa solo si la imagen original tiene canal alfa
                BufferedImage imgWithAlpha;
                
                // Lo dejo comentado porque me da problemas en la P11 con el operador para obtener las bandas
                /* 
                if (hasAlpha) {
                    imgWithAlpha = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = imgWithAlpha.createGraphics();
                    g.drawImage(img, 0, 0, null);
                    g.dispose();
                } else {
                    // Si la imagen original no tiene canal alfa, simplemente usarla sin cambios
                    imgWithAlpha = img;
                }
                */
                
                imgWithAlpha = img;

                VentanaInterna vi = new VentanaInterna();
                vi.getLienzo2D().setImage(imgWithAlpha);
                vi.addInternalFrameListener(manejador);
                MiManejadorLienzo manejadorLienzo = new MiManejadorLienzo();
                vi.getLienzo2D().addLienzoListener(manejadorLienzo);
                vi.getLienzo2D().addMouseMotionListener(new LienzoMove());
                this.escritorio.add(vi);
                vi.setTitle(f.getName());
                vi.setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al abrir la imagen: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_AbrirActionPerformed

    /**
     * Método que, dado un archivo, permite recuperar la extensión del mismo
     *
     * @param file fichero del cual se quiere extraer la extensión
     * @return extensión del fichero
     */
    private String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        }
        return "";
    }

    /**
     * Evento Action del botón Guardar. Permite volcar todo el contenido del
     * lienzo para guardarlo sobre un archivo en nuestro sistema con
     * extensiones: jpg, jpeg, png, gif y bmp (Por defecto jpg)
     *
     * @param evt
     */
    private void GuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GuardarActionPerformed
        VentanaInterna vi = (VentanaInterna) escritorio.getSelectedFrame();
        if (vi != null) {
            BufferedImage img = vi.getLienzo2D().getPaintedImage();
            if (img != null) {
                JFileChooser dlg = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Imágenes", "jpg", "jpeg", "png", "gif", "bmp");
                dlg.setFileFilter(filter);
                int resp = dlg.showSaveDialog(this);
                if (resp == JFileChooser.APPROVE_OPTION) {
                    try {
                        File f = dlg.getSelectedFile();
                        String formato = getFileExtension(f);
                        if (formato == null || formato.isEmpty()) {
                            formato = "jpg";
                            f = new File(f.getAbsolutePath() + ".jpg");
                        }
                        ImageIO.write(img, formato, f);
                        vi.setTitle(f.getName());
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Error al guardar la imagen: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
            }
        }
    }//GEN-LAST:event_GuardarActionPerformed

    /**
     * Evento Action del boton Fantasma. Entra en el modo de dibujado de
     * fantasmas
     *
     * @param evt
     */
    private void FantasmaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FantasmaActionPerformed
        Lienzo2D lienzo = getSelectedLienzo();
        if (lienzo != null) {
            lienzo.setTipo(Lienzo2D.posiblesTipos.FANTASMA);
            lienzo.SalirModoEditar();
            lienzo.setMover(Mover.isSelected());
            Estado.setText("Fantasma");
        }
    }//GEN-LAST:event_FantasmaActionPerformed

    /**
     * Evento Action del boton Relleno. Entra en el modo de dibujado con relleno
     *
     * @param evt
     */
    private void RellenoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RellenoActionPerformed
        Lienzo2D lienzo = getSelectedLienzo();
        if (lienzo != null) {
            lienzo.setRelleno(Relleno.isSelected());
        }
    }//GEN-LAST:event_RellenoActionPerformed

    /**
     * Evento Action del boton Mover. Entra en el modo edición, permitiendo
     * seleccionar figuras, moverlas y cambiarles sus atributos
     *
     * @param evt
     */
    private void MoverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MoverActionPerformed
        Lienzo2D lienzo = getSelectedLienzo();
        if (lienzo != null) {
            lienzo.setMover(Mover.isSelected());
        }
    }//GEN-LAST:event_MoverActionPerformed

    /**
     * Evento Action del boton Trasparencia. Entra en el modo de dibujado con
     * transparencia a 0.5
     *
     * @param evt
     */
    private void TransparenciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TransparenciaActionPerformed
        Lienzo2D lienzo = getSelectedLienzo();
        if (lienzo != null) {
            lienzo.setTransparencia(Transparencia.isSelected());
            // lienzo.repaint();
        }
    }//GEN-LAST:event_TransparenciaActionPerformed

    /**
     * Evento Action del boton Alisar. Entra en el modo de dibujado con alisado
     * para formas circulares
     *
     * @param evt
     */
    private void AlisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AlisarActionPerformed
        Lienzo2D lienzo = getSelectedLienzo();
        if (lienzo != null) {
            lienzo.setAlisar(Alisar.isSelected());
            // lienzo.repaint();
        }
    }//GEN-LAST:event_AlisarActionPerformed

    /**
     * Evento Action del boton Grosor. Establece el grosor al que se van a
     * dibujar las formas
     *
     * @param evt
     */
    private void GrosorStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_GrosorStateChanged
        Lienzo2D lienzo = getSelectedLienzo();
        if (lienzo != null) {
            lienzo.setTrazo((int) Grosor.getValue());
            // lienzo.repaint();
        }
    }//GEN-LAST:event_GrosorStateChanged

    /**
     * Evento Action del boton Paleta. Lanza un diálogo para elegir y establecer
     * el color del que se pintarán las formas
     *
     * @param evt
     */
    private void PaletaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PaletaActionPerformed
        Color color = JColorChooser.showDialog(this, "Elije un color", Color.RED);
        Lienzo2D lienzo = getSelectedLienzo();
        if (lienzo != null) {
            lienzo.setColor(color);
            Paleta.setBackground(color);
        }

    }//GEN-LAST:event_PaletaActionPerformed

    /**
     * Evento Action del boton Volcar. Permite volcar la forma seleccionada
     * sobre la imagen, sacándola del vector de formas seleccionables y quedando
     * ya "impresa" en el fondo
     *
     * @param evt
     */
    private void VolcarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_VolcarActionPerformed
        Lienzo2D lienzo = getSelectedLienzo();
        if (lienzo != null) {
            lienzo.VolcarSeleccion();
        }
    }//GEN-LAST:event_VolcarActionPerformed

    /**
     * Evento Action del boton auxiliar RescaleOp para probar dicha
     * funcionalidad de forma rapida
     *
     * @param evt
     */
    private void rescalarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rescalarActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo2D().getImage();
            if (img != null) {
                try {
                    RescaleOp rop = new RescaleOp(1.2F, 0.0F, null);
                    rop.filter(img, img);
                    vi.getLienzo2D().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_rescalarActionPerformed

    /**
     * Evento Action del boton auxiliar ConvolveOp para probar dicha
     * funcionalidad de forma rapida
     *
     * @param evt
     */
    private void convolucionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_convolucionActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo2D().getImage();
            if (img != null) {
                try {
                    float filtro[] = {0.1f, 0.1f, 0.1f, 0.1f, 0.2f, 0.1f, 0.1f, 0.1f, 0.1f};
                    Kernel k = new Kernel(3, 3, filtro);
                    ConvolveOp cop = new ConvolveOp(k);
                    BufferedImage imgdest = cop.filter(img, null);
                    vi.getLienzo2D().setImage(imgdest);
                    vi.getLienzo2D().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_convolucionActionPerformed

    /**
     * Evento FocusGain del deslizador Brillo. Crea una copia de la imagen del
     * lienzo seleccionado
     *
     * @param evt
     */
    private void BrilloFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_BrilloFocusGained
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            ColorModel cm = vi.getLienzo2D().getImage().getColorModel();
            WritableRaster raster = vi.getLienzo2D().getImage().copyData(null);
            boolean alfaPre = vi.getLienzo2D().getImage().isAlphaPremultiplied();
            imgFuente = new BufferedImage(cm, raster, alfaPre, null);
        }
    }//GEN-LAST:event_BrilloFocusGained

    /**
     * Evento FocusLost del deslizador Brillo. Elimina la copia de la imagen
     * creada cuando gana el foco
     *
     * @param evt
     */
    private void BrilloFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_BrilloFocusLost
        imgFuente = null;
    }//GEN-LAST:event_BrilloFocusLost

    /**
     * Evento StateChange del deslizador Brillo. Realiza un RescaleOp de
     * parámetros a=1 y b=Brillo.getValue sobre la copia realizada cuando
     * obtiene el foco. Además la establece de fondo del lienzo para ver los
     * cambios a tiempo real.
     *
     * @param evt
     */
    private void BrilloStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_BrilloStateChanged
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            RescaleOp rop = new RescaleOp(1.0F, Brillo.getValue(), null);
            rop.filter(imgFuente, vi.getLienzo2D().getImage());
            vi.getLienzo2D().repaint();
        }

        // Repintamos todo el escritorio para ver el efecto de las bandas
        escritorio.repaint();
    }//GEN-LAST:event_BrilloStateChanged

    /**
     * Evento FocusGain del deslizador Contraste. Crea una copia de la imagen
     * del lienzo seleccionado
     *
     * @param evt
     */
    private void ContrasteFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ContrasteFocusGained
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            ColorModel cm = vi.getLienzo2D().getImage().getColorModel();
            WritableRaster raster = vi.getLienzo2D().getImage().copyData(null);
            boolean alfaPre = vi.getLienzo2D().getImage().isAlphaPremultiplied();
            imgFuente = new BufferedImage(cm, raster, alfaPre, null);
        }
    }//GEN-LAST:event_ContrasteFocusGained

    /**
     * Evento FocusLost del deslizador Contraste. Elimina la copia de la imagen
     * creada cuando gana el foco
     *
     * @param evt
     */
    private void ContrasteFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_ContrasteFocusLost
        imgFuente = null;
    }//GEN-LAST:event_ContrasteFocusLost

    /**
     * Evento StateChange del deslizador Contraste. Realiza un RescaleOp de
     * parámetros a=Brillo.getValue y b=1 sobre la copia realizada cuando
     * obtiene el foco. Además la establece de fondo del lienzo para ver los
     * cambios a tiempo real.
     *
     * @param evt
     */
    private void ContrasteStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ContrasteStateChanged
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            RescaleOp rop = new RescaleOp(Contraste.getValue() / 10.0F, 0.0F, null);
            rop.filter(imgFuente, vi.getLienzo2D().getImage());
            vi.getLienzo2D().repaint();
        }
    }//GEN-LAST:event_ContrasteStateChanged

    /**
     * Evento Action de la lista desplegable de Filtros. Realiza una convolución
     * sobre la imagen del lienzo seleccionado, tomando como filtro el asociado
     * al indice seleccionado.
     *
     * @param evt
     */
    private void FiltrosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FiltrosActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo2D().getImage();
            Kernel k = getKernel(Filtros.getSelectedIndex());
            if (img != null && k != null) {
                try {
                    ConvolveOp cop = new ConvolveOp(k);
                    BufferedImage imgdest = cop.filter(img, null);
                    vi.getLienzo2D().setImage(imgdest);
                    vi.getLienzo2D().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_FiltrosActionPerformed

    /**
     * Evento FocusGain del deslizador Cometa. Crea una copia de la imagen del
     * lienzo seleccionado
     *
     * @param evt
     */
    private void CometaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_CometaFocusGained
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            ColorModel cm = vi.getLienzo2D().getImage().getColorModel();
            WritableRaster raster = vi.getLienzo2D().getImage().copyData(null);
            boolean alfaPre = vi.getLienzo2D().getImage().isAlphaPremultiplied();
            imgFuente = new BufferedImage(cm, raster, alfaPre, null);
        }
    }//GEN-LAST:event_CometaFocusGained

    /**
     * Evento FocusLost del deslizador Cometa. Elimina la copia de la imagen
     * creada cuando gana el foco
     *
     * @param evt
     */
    private void CometaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_CometaFocusLost
        imgFuente = null;
    }//GEN-LAST:event_CometaFocusLost

    /**
     * Evento StateChange del deslizador Cometa. Aplica una convolución de
     * emborrononamiento horizontal, con matriz de tamaño nxn, donde n=valor del
     * deslizador, n impar. La convolución se aplica sobre la copia de la imagen
     * creada cuando gana el foco y va estableciendo el resultado como fondo del
     * lienzo para ver los cambios a tiempo real.
     *
     * @param evt
     */
    private void CometaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_CometaStateChanged
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            int n = Cometa.getValue() % 2 == 0 ? Cometa.getValue() + -1 : Cometa.getValue();
            float filtro[] = getFiltroCometa(n);
            Kernel k = new Kernel(n, n, filtro);
            ConvolveOp cop = new ConvolveOp(k);
            BufferedImage imgdest = cop.filter(imgFuente, null);
            vi.getLienzo2D().setImage(imgdest);
            vi.getLienzo2D().repaint();

        }
    }//GEN-LAST:event_CometaStateChanged

    /**
     * Función Action del boton auxiliar para probar el operador de
     * AffineTransform
     *
     * @param evt
     */
    private void tranfAfinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tranfAfinActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo2D().getImage();
            if (img != null) {
                try {
                    AffineTransform at = AffineTransform.getScaleInstance(1.5, 1.5);
                    AffineTransformOp atop = new AffineTransformOp(at, null);
                    BufferedImage imgdest = atop.filter(img, null);
                    vi.getLienzo2D().setImage(imgdest);
                    vi.getLienzo2D().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_tranfAfinActionPerformed

    /**
     * Función Action del boton auxiliar para probar el operador de LookUp
     *
     * @param evt
     */
    private void lookUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lookUpActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo2D().getImage();
            if (img != null) {
                try {
                    byte funcionT[] = new byte[256];
                    for (int x = 0; x < 256; x++) {
                        funcionT[x] = (byte) (255 - x); // Negativo
                    }
                    LookupTable tabla = new ByteLookupTable(0, funcionT);
                    LookupOp lop = new LookupOp(tabla, null);
                    BufferedImage imgdest = lop.filter(img, null);
                    vi.getLienzo2D().setImage(imgdest);
                    vi.getLienzo2D().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_lookUpActionPerformed

    /**
     * Misma funcionalidad que el boton para crear una nueva Ventana Interna,
     * pero en la barra de herramientas para que sea más accesible
     *
     * @param evt
     */
    private void Nuevo2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Nuevo2ActionPerformed
        NuevoActionPerformed(evt);
    }//GEN-LAST:event_Nuevo2ActionPerformed

    /**
     * Misma funcionalidad que el boton para crear una nueva Ventana Interna,
     * pero en la barra de herramientas para que sea más accesible
     *
     * @param evt
     */
    private void Abrir2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Abrir2ActionPerformed
        AbrirActionPerformed(evt);
    }//GEN-LAST:event_Abrir2ActionPerformed

    /**
     * Misma funcionalidad que el boton para crear una nueva Ventana Interna,
     * pero en la barra de herramientas para que sea más accesible
     *
     * @param evt
     */
    private void Guardar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Guardar2ActionPerformed
        GuardarActionPerformed(evt);
    }//GEN-LAST:event_Guardar2ActionPerformed

    /**
     * Evento Action del boton ContrasteNormal. Hace uso de LookUpOp y de una
     * función de tipo S para crear un contraste "normal", es decir, los valores
     * por debajo de 128 los oscurece y, por arriba, los ilumina. Para crear la
     * funcion S se hace uso de la clase LookupTableProducer
     *
     * @param evt
     */
    private void ContrasteNormalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ContrasteNormalActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo2D().getImage();
            if (img != null) {
                try {
                    int type = LookupTableProducer.TYPE_SFUNCION;
                    LookupTable lt = LookupTableProducer.createLookupTable(type);
                    LookupOp lop = new LookupOp(lt, null);
                    lop.filter(img, img);
                    vi.getLienzo2D().repaint();
                } catch (Exception e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_ContrasteNormalActionPerformed

    /**
     * Evento Action del boton ContrasteIlumincacion. Hace uso de LookUpOp y de
     * una función del tipo raíz para crear un contraste con iluminación para
     * imágenes oscuras, es decir, aumenta proporcionalmente los valores de los
     * píxeles para recuperar la información en imágenes muy oscuras
     *
     * @param evt
     */
    private void ContrasteIluminacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ContrasteIluminacionActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo2D().getImage();
            if (img != null) {
                try {
                    // int type = LookupTableProducer.TYPE_LOGARITHM;
                    int type = LookupTableProducer.TYPE_ROOT;
                    LookupTable lt = LookupTableProducer.createLookupTable(type);
                    LookupOp lop = new LookupOp(lt, null);
                    lop.filter(img, img);
                    vi.getLienzo2D().repaint();
                } catch (Exception e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_ContrasteIluminacionActionPerformed

    /**
     * Evento Action del boton ContrasteOscurecimiento. Hace uso de LookUpOp y
     * de una función del tipo potencia para crear un contraste con
     * oscurecimiento para imágenes sobre-iluminadas, es decir, decrementa
     * proporcionalmente los valores de los píxeles para recuperar la
     * información en imágenes muy iluminadas
     *
     * @param evt
     */
    private void ContrasteOscurecimientoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ContrasteOscurecimientoActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo2D().getImage();
            if (img != null) {
                try {
                    int type = LookupTableProducer.TYPE_POWER;
                    LookupTable lt = LookupTableProducer.createLookupTable(type);
                    LookupOp lop = new LookupOp(lt, null);
                    lop.filter(img, img);
                    vi.getLienzo2D().repaint();
                } catch (Exception e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_ContrasteOscurecimientoActionPerformed

    /**
     * Evento Action del boton Rotar180. Permite rotar 180 grados la imagen.
     * Para ello, hace uso de la funcion AffineTransform, indicandole un angulo
     * de rotacion de 180 grados y el punto sobre el que se va a realizar la
     * transformacion (En este caso, el centro de la imagen)
     *
     * @param evt
     */
    private void Rotar180ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Rotar180ActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo2D().getImage();
            if (img != null) {
                try {
                    double r = Math.toRadians(180);
                    Point c = new Point(img.getWidth() / 2, img.getHeight() / 2);
                    AffineTransform at = AffineTransform.getRotateInstance(r, c.x, c.y);
                    AffineTransformOp atop;
                    atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
                    BufferedImage imgdest = atop.filter(img, null);
                    vi.getLienzo2D().setImage(imgdest);
                    vi.getLienzo2D().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_Rotar180ActionPerformed

    /**
     * Evento Action del boton Aumentar. Hace un escalado de x1.25 a la imagen,
     * mediante el uso de AffineTransformOp
     *
     * @param evt
     */
    private void AumentarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AumentarActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo2D().getImage();
            if (img != null) {
                try {
                    double factor = 1.25;
                    AffineTransform at = AffineTransform.getScaleInstance(factor, factor);
                    AffineTransformOp atop;
                    atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
                    BufferedImage imgdest = atop.filter(img, null);
                    vi.getLienzo2D().setImage(imgdest);
                    vi.getLienzo2D().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_AumentarActionPerformed

    /**
     * Evento Action del boton Decrementar. Hace un escalado de x0.75 a la
     * imagen, mediante el uso de AffineTransformOp
     *
     * @param evt
     */
    private void DecrementarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DecrementarActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo2D().getImage();
            if (img != null) {
                try {
                    double factor = 0.75;
                    AffineTransform at = AffineTransform.getScaleInstance(factor, factor);
                    AffineTransformOp atop;
                    atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
                    BufferedImage imgdest = atop.filter(img, null);
                    vi.getLienzo2D().setImage(imgdest);
                    vi.getLienzo2D().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_DecrementarActionPerformed

    /**
     * Evento Action del boton Duplicar. Permite generar una nueva ventana
     * interna con una copia de la imagen que había en la ventana activa
     *
     * @param evt
     */
    private void DuplicarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DuplicarActionPerformed
        VentanaInterna vi_copia = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi_copia != null) {
            BufferedImage img = vi_copia.getLienzo2D().getImage();
            if (img != null) {
                // Verificar si la imagen original tiene canal alfa
                boolean hasAlpha = img.getColorModel().hasAlpha();

                // Crear una nueva imagen con canal alfa solo si la imagen original tiene canal alfa
                BufferedImage imgWithAlpha;
                if (hasAlpha) {
                    imgWithAlpha = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = imgWithAlpha.createGraphics();
                    g.drawImage(img, 0, 0, null);
                    g.dispose();
                } else {
                    // Si la imagen original no tiene canal alfa, simplemente usarla sin cambios
                    imgWithAlpha = img;
                }

                VentanaInterna vi = new VentanaInterna();
                vi.getLienzo2D().setImage(imgWithAlpha);
                vi.addInternalFrameListener(manejador);
                MiManejadorLienzo manejadorLienzo = new MiManejadorLienzo();
                vi.getLienzo2D().addLienzoListener(manejadorLienzo);
                this.escritorio.add(vi);
                vi.setVisible(true);

                vi.setTitle(vi_copia.getTitle() + " copia");
            }
        }
    }//GEN-LAST:event_DuplicarActionPerformed

    /**
     * Evento FocusGain del deslizador TLineal. Crea una copia de la imagen del
     * lienzo seleccionado
     *
     * @param evt
     */
    private void TLinealFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_TLinealFocusGained
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            ColorModel cm = vi.getLienzo2D().getImage().getColorModel();
            WritableRaster raster = vi.getLienzo2D().getImage().copyData(null);
            boolean alfaPre = vi.getLienzo2D().getImage().isAlphaPremultiplied();
            imgFuente = new BufferedImage(cm, raster, alfaPre, null);
        }
    }//GEN-LAST:event_TLinealFocusGained

    /**
     * Evento FocusLost del deslizador TLineal. Elimina la copia creada durante
     * el FocusGain
     *
     * @param evt
     */
    private void TLinealFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_TLinealFocusLost
        imgFuente = null;
    }//GEN-LAST:event_TLinealFocusLost

    /**
     * Se trata de una funcion a trozos. Cada trozo es una recta. Si a=128
     * tenemos que las dos rectas son la misma y que la funcion T=id. Si a>128,
     * entonces la primera recta tiene mayor pendiente que la segunda, simulando
     * una función raíz o logaritmica, por lo que se va a producir un contraste
     * con oluminación, pero no tan preciso como si usasemos una función de otro
     * tipo como vimos anteriormente. En el caso extremo a=255 tendremos que los
     * valores de los pixeles por encima de 128 irán todos al 255 y, por debajo,
     * tendremos una recta de pendiente extrema que aumentará el valor de los
     * pixeles. Análogamente, si a es menor que 128, la primera recta tendra
     * menor pendiente que la segunda, simulando una funcion potencia o
     * exponencial, por lo que se producidad un contraste con oscurecimiento,
     * pero sin tanta precision como si usasemos alguna de las funciones vistas
     * en ejercicios anteriores. Si a=0, tenemos el caso extremo donde todo los
     * pixeles por debajo de 128 irán al 0 y, por arriba, tendremos una recta de
     * pendiente extrema que oscurecerá los pixeles
     *
     * @param evt
     */
    private void TLinealStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_TLinealStateChanged
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            int a = TLineal.getValue();
            try {
                byte funcionT[] = new byte[256];
                for (int x = 0; x < 256; x++) {
                    if (x < 128) {
                        funcionT[x] = (byte) ((a * x) / 128);
                    } else {
                        funcionT[x] = (byte) (((255 - a) * (x - 128) / 127) + a);
                    }
                }
                LookupTable tabla = new ByteLookupTable(0, funcionT);
                LookupOp lop = new LookupOp(tabla, null);
                BufferedImage imgdest = lop.filter(imgFuente, null);
                vi.getLienzo2D().setImage(imgdest);
                vi.getLienzo2D().repaint();
            } catch (IllegalArgumentException e) {
                System.err.println(e.getLocalizedMessage());
            }
        }
    }//GEN-LAST:event_TLinealStateChanged

    /**
     * Ver esbozo de la grafica en
     * https://drive.google.com/file/d/1NKB5vUjZOGsf1OvWPjl8N9ZZbTK2tEbu/view?usp=drive_link
     * He tomado f(x)=a*b^x y he impuesto que f(128)=128 y f(255)=255.
     * Resolvemos el sistema de ecuaciones y obtenemos la función deseada. Otra
     * posible opción para resolver este ejercicio es determinar la curva que
     * pasa por 3 puntos. Para ello, tomaríamos f(x)=ax^2+bx+c e impondríamos
     * que f(128)=128, f(255)=255. La tercera condición es libre, pero para que
     * la función resultante nos diese un buen resultado, lo propio sería tomar
     * x=128+(255-128)/2 (el punto medio del segundo intervalo de valores) y que
     * su imagen esté en la recta mediatriz del segmento (128,128) a (255,255) y
     * siempre por debajo de y=x y por encima de y=128. La representación de la
     * situación sería algo como lo siguiente:
     * https://drive.google.com/file/d/1SOAPvVbcqZHHcbQIJv7Kk0U9vF-CtTuo/view?usp=drive_link
     * Cuanto mas cerca tomemos el punto de (x,x), menos se apreciara el
     * resultado y, cuanto más esté hacia el otro extremo, se producirá una
     * mayor distorsión de los colores
     *
     * @param evt
     */
    private void OscurecerClarosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OscurecerClarosActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo2D().getImage();
            if (img != null) {
                float a = (float) (128.0F / ((255 / 128) * (Math.pow(255 / 128, 1 / 127))));
                float b = (float) (Math.pow(255 / 128, 1 / 127));
                try {
                    byte funcionT[] = new byte[256];
                    for (int x = 0; x < 256; x++) {
                        if (x < 128) {
                            funcionT[x] = (byte) (x);
                        } else {
                            funcionT[x] = (byte) (a * Math.pow(b, x));
                        }
                    }
                    LookupTable tabla = new ByteLookupTable(0, funcionT);
                    LookupOp lop = new LookupOp(tabla, null);
                    lop.filter(img, img);
                    vi.getLienzo2D().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_OscurecerClarosActionPerformed

    /**
     * Utiliza la función y=-x para obtener el negativo de una imagen
     *
     * @param evt
     */
    private void NegativoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NegativoActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo2D().getImage();
            if (img != null) {
                try {
                    byte funcionT[] = new byte[256];
                    for (int x = 0; x < 256; x++) {
                        funcionT[x] = (byte) (255 - x);
                    }
                    LookupTable tabla = new ByteLookupTable(0, funcionT);
                    LookupOp lop = new LookupOp(tabla, null);
                    lop.filter(img, img);
                    vi.getLienzo2D().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_NegativoActionPerformed

    /**
     * Función auxiliar para probar el operador BandCombine Op
     *
     * @param evt
     */
    private void combBandaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combBandaActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo2D().getImage();
            if (img != null) {
                try {
                    float[][] matriz = {
                        {0.0F, 0.5F, 0.5F, 0.0F},
                        {0.5F, 0.0F, 0.5F, 0.0F},
                        {0.5F, 0.5F, 0.0F, 0.0F},
                        {0.0F, 0.0F, 0.0F, 1.0F}
                    };
                    BandCombineOp bcop = new BandCombineOp(matriz, null);
                    bcop.filter(img.getRaster(), img.getRaster());
                    vi.getLienzo2D().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_combBandaActionPerformed

    /**
     * Función auxiliar para probar el operador ColorConvertOp
     *
     * @param evt
     */
    private void colorConvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorConvActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo2D().getImage();
            if (img != null) {
                try {
                    ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
                    ColorConvertOp op = new ColorConvertOp(cs, null);
                    BufferedImage imgdest = op.filter(img, null);
                    vi.getLienzo2D().setImage(imgdest);
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_colorConvActionPerformed

    /**
     * Evento Action del botón CombinarBandas. Utiliza el operador BandCombineOp
     * para establecer cada banda como la media de las otras dos. Hay que tener
     * en cuenta que tengo canal alpha, por lo que tengo 4 bandas en vez de 3
     * como indica el guión
     *
     * @param evt
     */
    private void CombinarBandasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CombinarBandasActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo2D().getImage();
            if (img != null) {
                float[][] matriz;
                try {
                    if(img.getColorModel().hasAlpha()){
                        matriz = new float[][] {
                            {0.0F, 0.5F, 0.5F, 0.0F},
                            {0.5F, 0.0F, 0.5F, 0.0F},
                            {0.5F, 0.5F, 0.0F, 0.0F},
                            {0.0F, 0.0F, 0.0F, 1.0F}
                        };
                    } else {
                        matriz = new float[][] {
                            {0.0F, 0.5F, 0.5F},
                            {0.5F, 0.0F, 0.5F},
                            {0.5F, 0.5F, 0.0F}
                        };
                    }
                    BandCombineOp bcop = new BandCombineOp(matriz, null);
                    bcop.filter(img.getRaster(), img.getRaster());
                    vi.getLienzo2D().repaint();
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getLocalizedMessage());
                }
            }
        }
    }//GEN-LAST:event_CombinarBandasActionPerformed

    /**
     * Evento Action del Boton Bandas. Sobre la ventana interna seleccionada,
     * genera nuevas ventanas internas, cada una con una imagen en escala de
     * grises del raster de cada banda que compone la imagen
     *
     * @param evt
     */
    private void BandasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BandasActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo2D().getImage();
            if (img != null) {
                int numeroBandas = img.getSampleModel().getNumBands();
                System.out.println(numeroBandas);
                for (int i = 0; i < numeroBandas; i++) {
                    BufferedImage imgBanda = getImageBand(img, i);
                    VentanaInterna viBand = new VentanaInterna();
                    viBand.getLienzo2D().setImage(imgBanda);
                    viBand.addInternalFrameListener(manejador);
                    MiManejadorLienzo manejadorLienzo = new MiManejadorLienzo();
                    viBand.getLienzo2D().addLienzoListener(manejadorLienzo);
                    this.escritorio.add(viBand);
                    viBand.setVisible(true);
                    viBand.setTitle(viBand.getTitle() + "[Banda " + i + "]");
                }
            }
        }
    }//GEN-LAST:event_BandasActionPerformed

    /**
     * Evento Action del boton EspacioColor. Permite cambiar el espacio de color
     * de la imagen asociada a la ventana interna seleccionada
     *
     * @param evt
     */
    private void EspacioColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EspacioColorActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo2D().getImage();
            if (img != null) {
                int index = EspacioColor.getSelectedIndex();
                ColorSpace cs = null;
                switch (index) {
                    case 0 ->
                        cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
                    case 1 ->
                        cs = ColorSpace.getInstance(ColorSpace.CS_PYCC);
                    case 2 ->
                        cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
                }
                ColorConvertOp cop = new ColorConvertOp(cs, null);
                BufferedImage imgOut = cop.filter(img, null);
                vi.getLienzo2D().setImage(imgOut);
                vi.getLienzo2D().repaint();
            }
        }
    }//GEN-LAST:event_EspacioColorActionPerformed

    /**
     * Evento Action del boton Tintar. Usa el operador TintOp del paquete sm.image para
     * tintar la imagen de un color que seleccione el usuario y con un grado de mezcla de 0.5
     * @param evt 
     */
    private void TintarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TintarActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo2D().getImage();
            if(img != null) {
                Color color = JColorChooser.showDialog(this, "Elije un color", Color.RED);
                TintOp tintado = new TintOp(color, 0.5f);
                tintado.filter(img,img);
                vi.getLienzo2D().repaint();
            }
        }
    }//GEN-LAST:event_TintarActionPerformed

    /**
     * Evento Action del boton Sepia. Este efecto utiliza el operador SepiaOp del paquete sm.image
     * para modificar el tono y saturación de la imagen, dando un aspecto de "fotografía antigua"
     * @param evt 
     */
    private void SepiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SepiaActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo2D().getImage();
            if(img != null) {
                SepiaOp sepia = new SepiaOp();
                sepia.filter(img,img);
                vi.getLienzo2D().repaint();
            }
        }
    }//GEN-LAST:event_SepiaActionPerformed

    /**
     * Evento Action del boton Ecualizacion. Este efecto emplea el operador EqualizationOP del paquete sm.image.
     * Si aplicamos la ecualización a la 3 bandas en RGB vemos que obtenemos un efecto extraño, donde se generan
     * variaciones de color y "parece que se quema la imagen". Al pasar al espacio YCC y aplicar el efecto solo
     * sobre el canal Y, obtenemos el efecto deseado, aunque tiene a iluminar en exceso en determinadas imágenes.
     * (Podríamos usar en su lugar la clase YCbCrColorSpace del paquete sm.image)
     * @param evt 
     */
    private void EcualizacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EcualizacionActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo2D().getImage();
            if(img != null) {
                ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_PYCC);
                ColorConvertOp cop = new ColorConvertOp(cs, null);
                BufferedImage imgOut = cop.filter(img, null);
                vi.getLienzo2D().setImage(imgOut);
                EqualizationOp ecualizacion = new EqualizationOp(0);
                BufferedImage imgOut2 = ecualizacion.filter(imgOut, null);
                vi.getLienzo2D().setImage(imgOut2);
                vi.getLienzo2D().repaint();
            }
        }
    }//GEN-LAST:event_EcualizacionActionPerformed

    /**
     * Evento FocusGain del deslizador Posterizar. Crea una copia interna de la imagen del lienzo seleccionado
     * @param evt 
     */
    private void PosterizarFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_PosterizarFocusGained
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            ColorModel cm = vi.getLienzo2D().getImage().getColorModel();
            WritableRaster raster = vi.getLienzo2D().getImage().copyData(null);
            boolean alfaPre = vi.getLienzo2D().getImage().isAlphaPremultiplied();
            imgFuente = new BufferedImage(cm, raster, alfaPre, null);
        }
    }//GEN-LAST:event_PosterizarFocusGained

    /**
     * Evecto FocusLost del deslizador Posterizar. Elimina la copia interna creada durante el FocusGain
     * @param evt 
     */
    private void PosterizarFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_PosterizarFocusLost
        imgFuente = null;
    }//GEN-LAST:event_PosterizarFocusLost

    /**
     * Evento StateChange del deslizador Posterizar. Reduce el número de colores de una imagen al número
     * de nieveles especificado por el desliazdor.
     * @param evt 
     */
    private void PosterizarStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_PosterizarStateChanged
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo2D().getImage();
            if(img != null) {
                PosterizarOp posterizar = new PosterizarOp(Posterizar.getValue());
                BufferedImage imgDest = posterizar.filter(imgFuente, null);
                vi.getLienzo2D().setImage(imgDest);
                vi.getLienzo2D().repaint();
            }
        }
    }//GEN-LAST:event_PosterizarStateChanged

    /**
     * Evento Action del boton Rojo. Usa el operador propio RojoOp para resaltar el color
     * rojo de una imagen, dejando el resto a escala de grises. He puesto el filtro por defecto
     * a 20
     * @param evt 
     */
    private void RojoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RojoActionPerformed
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo2D().getImage();
            if(img != null) {
                RojoOp rojo = new RojoOp(20);
                BufferedImage imgDest = rojo.filter(img, null);
                vi.getLienzo2D().setImage(imgDest);
                vi.getLienzo2D().repaint();
            }
        }
    }//GEN-LAST:event_RojoActionPerformed

    /**
     * Evento Action del boton ColorCambio1. Sirve para elergir el color C1 del operador CambiarColor
     * @param evt 
     */
    private void ColorCambio1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ColorCambio1ActionPerformed
        Color color = JColorChooser.showDialog(this, "Elije un color", Color.RED);
        ColorCambio1.setBackground(color);
    }//GEN-LAST:event_ColorCambio1ActionPerformed

    /**
     * Action del boton ColorCambio2. Sirve para elergir el color C2 del operador CambiarColor
     * @param evt 
     */
    private void ColorCambio2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ColorCambio2ActionPerformed
        Color color = JColorChooser.showDialog(this, "Elije un color", Color.YELLOW);
        ColorCambio2.setBackground(color);
    }//GEN-LAST:event_ColorCambio2ActionPerformed

    /**
     * Evento FocusGain del deslizador CambiarColor. Crea una copia interna de la imagen del lienzo seleccionado
     * @param evt 
     */
    private void CambiarColorFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_CambiarColorFocusGained
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            ColorModel cm = vi.getLienzo2D().getImage().getColorModel();
            WritableRaster raster = vi.getLienzo2D().getImage().copyData(null);
            boolean alfaPre = vi.getLienzo2D().getImage().isAlphaPremultiplied();
            imgFuente = new BufferedImage(cm, raster, alfaPre, null);
        }
    }//GEN-LAST:event_CambiarColorFocusGained

    /**
     * Evento FocusLost del deslizador CambiarColor. Elimina la copia creada durante el FocusGain
     * @param evt 
     */
    private void CambiarColorFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_CambiarColorFocusLost
        imgFuente = null;
    }//GEN-LAST:event_CambiarColorFocusLost

    /**
     * Evento StateChange del deslizador CambiarColor. Realiza la operación de cambio de color
     * estableciendo el umbral al valor indicado por el propio desliazdor.
     * @param evt 
     */
    private void CambiarColorStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_CambiarColorStateChanged
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo2D().getImage();
            if(img != null) {
                CambiarColorOp cambiarcolor = new CambiarColorOp(ColorCambio1.getBackground(), ColorCambio2.getBackground(), CambiarColor.getValue());
                BufferedImage imgDest = cambiarcolor.filter(imgFuente, null);
                vi.getLienzo2D().setImage(imgDest);
                vi.getLienzo2D().repaint();
            }
        }
    }//GEN-LAST:event_CambiarColorStateChanged

    /**
     * Evento FocusGain del deslizador TintarDeslizador. Crea una copia interna de la imagen del lienzo seleccionado
     * @param evt 
     */
    private void TintarDeslizadorFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_TintarDeslizadorFocusGained
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            ColorModel cm = vi.getLienzo2D().getImage().getColorModel();
            WritableRaster raster = vi.getLienzo2D().getImage().copyData(null);
            boolean alfaPre = vi.getLienzo2D().getImage().isAlphaPremultiplied();
            imgFuente = new BufferedImage(cm, raster, alfaPre, null);
        }
    }//GEN-LAST:event_TintarDeslizadorFocusGained

    /**
     * Evento FocusLost del deslizador TintarDeslizador. Elimina la copia interna creada durante el FocusLost.
     * @param evt 
     */
    private void TintarDeslizadorFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_TintarDeslizadorFocusLost
        imgFuente = null;
    }//GEN-LAST:event_TintarDeslizadorFocusLost

    /**
     * Evento StateChanged del deslizador TintarDeslizador. Toma el valor deslizador para determinar el valor de
     * alpha en la operación de tintado (Usa el color rojo por defecto para el tintado)
     * @param evt 
     */
    private void TintarDeslizadorStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_TintarDeslizadorStateChanged
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo2D().getImage();
            if(img != null) {
                float value = TintarDeslizador.getValue()/100.0f;
                TintOp tintar = new TintOp(Color.RED, value);
                BufferedImage imgDest = tintar.filter(imgFuente, null);
                vi.getLienzo2D().setImage(imgDest);
                vi.getLienzo2D().repaint();
            }
        }
    }//GEN-LAST:event_TintarDeslizadorStateChanged

    /**
     * Evento FocusGain del deslizador RojoDeslizador. Crea una copia interna de la imagen del lienzo seleccionado
     * @param evt 
     */
    private void RojoDeslizadorFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_RojoDeslizadorFocusGained
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            ColorModel cm = vi.getLienzo2D().getImage().getColorModel();
            WritableRaster raster = vi.getLienzo2D().getImage().copyData(null);
            boolean alfaPre = vi.getLienzo2D().getImage().isAlphaPremultiplied();
            imgFuente = new BufferedImage(cm, raster, alfaPre, null);
        }
    }//GEN-LAST:event_RojoDeslizadorFocusGained

    /**
     * Evento FocusLost del desliazdor RojoDeslizador. Elimina la copia interna creada durante el FocusGain
     * @param evt 
     */
    private void RojoDeslizadorFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_RojoDeslizadorFocusLost
        imgFuente = null;
    }//GEN-LAST:event_RojoDeslizadorFocusLost

    /**
     * Evento StateChanged del deslizador RojoDeslizador. Toma el valor del desliazdor para determinar el valor
     * del umbral de la operacion RojoOp.
     * @param evt 
     */
    private void RojoDeslizadorStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_RojoDeslizadorStateChanged
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo2D().getImage();
            if(img != null) {
                RojoOp rojo = new RojoOp(RojoDeslizador.getValue());
                BufferedImage imgDest = rojo.filter(imgFuente, null);
                vi.getLienzo2D().setImage(imgDest);
                vi.getLienzo2D().repaint();
            }
        }
    }//GEN-LAST:event_RojoDeslizadorStateChanged

    /**
     * Evento FocusGain del deslizador SaturacionSlider. Crea una copia de la imagen del frame seleecionado
     * @param evt 
     */
    private void SaturacionSliderFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_SaturacionSliderFocusGained
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            ColorModel cm = vi.getLienzo2D().getImage().getColorModel();
            WritableRaster raster = vi.getLienzo2D().getImage().copyData(null);
            boolean alfaPre = vi.getLienzo2D().getImage().isAlphaPremultiplied();
            imgFuente = new BufferedImage(cm, raster, alfaPre, null);
        }
    }//GEN-LAST:event_SaturacionSliderFocusGained

    /**
     * Evento FocusLost del del desliazdor SaturacionSlider. Elimina la copia creada durante el FocusGain
     * @param evt 
     */
    private void SaturacionSliderFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_SaturacionSliderFocusLost
        imgFuente = null;
    }//GEN-LAST:event_SaturacionSliderFocusLost

    /**
     * Evento StateChange del deslizador SaturacionSlider. Hace uso del operador propio SaturacionOp
     * para alterar los valores de S por debajo del umbral establecido por el valor del deslizador
     * @param evt 
     */
    private void SaturacionSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_SaturacionSliderStateChanged
        VentanaInterna vi = (VentanaInterna) (escritorio.getSelectedFrame());
        if (vi != null) {
            BufferedImage img = vi.getLienzo2D().getImage();
            if(img != null) {
                SaturacionOp saturar = new SaturacionOp(SaturacionSlider.getValue()/100f);
                BufferedImage imgDest = saturar.filter(imgFuente, null);
                vi.getLienzo2D().setImage(imgDest);
                vi.getLienzo2D().repaint();
            }
        }
    }//GEN-LAST:event_SaturacionSliderStateChanged

    /**
     * Método que permite obtener las máscaras de la convolución para el
     * FiltroActionPerformed
     *
     * @param seleccion indice seleccionado en el filtro
     * @return la máscara a usar para la convolución
     */
    private Kernel getKernel(int seleccion) {
        Kernel k = null;
        float filtro[] = null;
        switch (seleccion) {
            case 0 ->
                k = KernelProducer.createKernel(KernelProducer.TYPE_MEDIA_3x3);
            case 1 ->
                k = KernelProducer.createKernel(KernelProducer.TYPE_BINOMIAL_3x3);
            case 2 ->
                k = KernelProducer.createKernel(KernelProducer.TYPE_ENFOQUE_3x3);
            case 3 ->
                k = KernelProducer.createKernel(KernelProducer.TYPE_RELIEVE_3x3);
            case 4 ->
                k = KernelProducer.createKernel(KernelProducer.TYPE_LAPLACIANA_3x3);
            case 5 -> {
                filtro = crearFiltroMedia(5);
                k = new Kernel(5, 5, filtro);
            }
            case 6 -> {
                filtro = crearFiltroMedia(7);
                k = new Kernel(7, 7, filtro);
            }
            case 7 -> {
                filtro = getEmborronamientoIluminado(3);
                k = new Kernel(3, 3, filtro);
            }
            case 8 -> {
                filtro = getEmborronamientoIluminado(5);
                k = new Kernel(5, 5, filtro);
            }
        }

        return k;
    }

    /**
     * Método auxiliar que crea el filtro para la media de tamaño n*n
     *
     * @param n tamaño a usar (se creará de n*n)
     * @return filtro de la convolución
     */
    private float[] crearFiltroMedia(int n) {
        float[] filtro = new float[n * n];
        float valor = 1.0F / (n * n);

        for (int i = 0; i < n * n; i++) {
            filtro[i] = valor;
        }

        return filtro;
    }

    /**
     * Método auxiliar que crea el filtro para el emborronamiento horizontal
     * (cometa) de tamaño n*n
     *
     * @param n tamaño a usar (se creará de n*n)
     * @return filtro de la convolución
     */
    private float[] getFiltroCometa(int n) {
        float[] filtro = new float[n * n];
        int medio = n / 2;
        float suma = 0.0f;
        int z = 0;

        if (n == 1) {
            filtro[0] = 1.0f;
            return filtro;
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == medio && j >= medio) { // Está a la derecha en la misma línea del pixeñ
                    filtro[z] = 1.0f / j;
                } else {
                    filtro[z] = 0.0f;
                }
                suma += filtro[z];
                z++;
            }
        }

        System.out.print("Filtro size" + n + "->[");
        for (int i = 0; i < n * n; i++) {
            filtro[i] /= suma;
            System.out.print(filtro[i] + ", ");
        }
        System.out.print("] \n");

        return filtro;
    }

    /**
     * Dado que todos los valores de la máscara son iguales, se produce por un
     * lado, un suavizado como en la media, pero, al ser 2 el numerador en lugar
     * de 1, implpica que los pesos suman 2, por lo que, al realizar la
     * convolución, la suma ponderada de los pixeles vecinos será mayor que el
     * valor original del pixel que estamos procesando, resultando en un aumento
     * del valor del pixel. Como esto ocurre para cada pixel procesado,
     * conducirá a un aumento del brillo de la imagen.
     *
     * @param n Tamaño de la máscara (La máscara será nxn)
     * @return Devuelve el filtro de un emborronamiento iluminado
     */
    private float[] getEmborronamientoIluminado(int n) {
        float[] filtro = new float[n * n];
        for (int i = 0; i < n * n; i++) {
            filtro[i] = 2.0F / (n * n);
        }
        return filtro;
    }

    /**
     * Método que dado una imagen y un índice de banda nos devuelve una imagen
     * nueva en escala de grises con los valores de la banda indicada
     *
     * @param img imagen de entrada
     * @param banda banda seleccionada
     * @return imagen en escala de grises con los valores de la banda
     */
    private BufferedImage getImageBand(BufferedImage img, int banda) {
        //Creamos el modelo de color de la nueva imagen basado en un espcio de color GRAY
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        ComponentColorModel cm = new ComponentColorModel(cs, false, false,
                Transparency.OPAQUE,
                DataBuffer.TYPE_BYTE);
        //Creamos el nuevo raster a partir del raster de la imagen original
        int vband[] = {banda};
        WritableRaster bRaster = (WritableRaster) img.getRaster().createWritableChild(0, 0,
                img.getWidth(), img.getHeight(), 0, 0, vband);
        //Creamos una nueva imagen que contiene como raster el correspondiente a la banda
        return new BufferedImage(cm, bRaster, false, null);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem Abrir;
    private javax.swing.JButton Abrir2;
    private javax.swing.JToggleButton Alisar;
    private javax.swing.JButton Aumentar;
    private javax.swing.JButton Bandas;
    private javax.swing.JToggleButton BotonElipse;
    private javax.swing.JToggleButton BotonLinea;
    private javax.swing.JToggleButton BotonRectangulo;
    private javax.swing.JSlider Brillo;
    private javax.swing.JSlider CambiarColor;
    private javax.swing.JButton ColorCambio1;
    private javax.swing.JButton ColorCambio2;
    private javax.swing.JButton CombinarBandas;
    private javax.swing.JSlider Cometa;
    private javax.swing.JSlider Contraste;
    private javax.swing.JButton ContrasteIluminacion;
    private javax.swing.JButton ContrasteNormal;
    private javax.swing.JButton ContrasteOscurecimiento;
    private javax.swing.JButton Decrementar;
    private javax.swing.JButton Duplicar;
    private javax.swing.JButton Ecualizacion;
    private javax.swing.JComboBox<String> EspacioColor;
    private javax.swing.JLabel Estado;
    private javax.swing.JToggleButton Fantasma;
    private javax.swing.JComboBox<String> Filtros;
    private javax.swing.JSlider Grosor;
    private javax.swing.JMenuItem Guardar;
    private javax.swing.JButton Guardar2;
    private javax.swing.JMenu MenuImagenes;
    private javax.swing.JToggleButton Mover;
    private javax.swing.JButton Negativo;
    private javax.swing.JMenuItem Nuevo;
    private javax.swing.JButton Nuevo2;
    private javax.swing.JButton OscurecerClaros;
    private javax.swing.JButton Paleta;
    private javax.swing.JSlider Posterizar;
    private javax.swing.JButton PosterizarIcon;
    private javax.swing.JLabel RGBLabel;
    private javax.swing.JToggleButton Relleno;
    private javax.swing.JButton Rojo;
    private javax.swing.JSlider RojoDeslizador;
    private javax.swing.JButton Rotar180;
    private javax.swing.JLabel S;
    private javax.swing.JSlider SaturacionSlider;
    private javax.swing.JButton Sepia;
    private javax.swing.JSlider TLineal;
    private javax.swing.JButton TLinealGraph;
    private javax.swing.JButton Tintar;
    private javax.swing.JSlider TintarDeslizador;
    private javax.swing.ButtonGroup TipoDibujo;
    private javax.swing.JToggleButton Transparencia;
    private javax.swing.JButton Volcar;
    private javax.swing.JMenuItem colorConv;
    private javax.swing.JMenuItem combBanda;
    private javax.swing.JMenuItem convolucion;
    private javax.swing.JDesktopPane escritorio;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator10;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JToolBar.Separator jSeparator7;
    private javax.swing.JToolBar.Separator jSeparator8;
    private javax.swing.JToolBar.Separator jSeparator9;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JMenuItem lookUp;
    private javax.swing.JMenuItem rescalar;
    private javax.swing.JMenuItem tranfAfin;
    // End of variables declaration//GEN-END:variables
}
