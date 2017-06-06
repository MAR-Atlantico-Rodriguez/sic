package sic.vista.swing;

import java.awt.event.AdjustmentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.EmpresaActiva;
import sic.modelo.Movimiento;
import sic.modelo.Producto;
import sic.modelo.RenglonFactura;
import sic.modelo.PaginaRespuestaRest;
import sic.modelo.TipoDeComprobante;
import sic.util.RenderTabla;
import sic.util.Utilidades;

public class BuscarProductosGUI extends JDialog {

    private final TipoDeComprobante tipoComprobante;
    private ModeloTabla modeloTablaResultados = new ModeloTabla();
    private List<Producto> productos = new ArrayList<>();
    private List<Producto> productosAux = new ArrayList<>();
    private final List<RenglonFactura> renglonesFactura;
    private Producto prodSeleccionado;
    private RenglonFactura renglon;
    private boolean debeCargarRenglon;    
    private final Movimiento tipoMovimiento;
    private final HotKeysHandler keyHandler = new HotKeysHandler();
    private int NUMERO_PAGINA = 0;
    private String CRITERIA_SORT = "descripcion";
    private static final int TAMANIO_PAGINA = 100;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    
    public BuscarProductosGUI(JDialog parent, boolean modal, List<RenglonFactura> renglones,
            TipoDeComprobante tipoDeComprobante, Movimiento movimiento) {
        
        super(parent, modal);        
        this.initComponents();
        this.setIcon();
        renglonesFactura = renglones;
        tipoMovimiento = movimiento;
        this.tipoComprobante = tipoDeComprobante;
        this.setSize(parent.getWidth() - 100, parent.getHeight() - 200);
        this.setLocationRelativeTo(parent);
        this.setColumnas();        
        txt_CampoBusqueda.addKeyListener(keyHandler);
        btn_Buscar.addKeyListener(keyHandler);
        tbl_Resultado.addKeyListener(keyHandler);
        ta_ObservacionesProducto.addKeyListener(keyHandler);
        txt_Cantidad.addKeyListener(keyHandler);
        txt_UnidadMedida.addKeyListener(keyHandler);
        txt_PorcentajeDescuento.addKeyListener(keyHandler);
        btn_Aceptar.addKeyListener(keyHandler);        
        Timer timer = new Timer(false);
        txt_CampoBusqueda.addKeyListener(new KeyAdapter() {
            private TimerTask task;
            @Override
            public void keyTyped(KeyEvent e) {
                if (task != null) {
                    task.cancel();
                }
                task = new TimerTask() {
                    @Override
                    public void run() {
                        NUMERO_PAGINA = 0;
                        productos.clear();
                        buscar();
                        actualizarProductosCargadosEnFactura();
                        limpiarJTable();
                        cargarResultadosAlTable();
                    }
                };
                timer.schedule(task, 450);
            }
        });        
        sp_Resultado.getVerticalScrollBar().addAdjustmentListener((AdjustmentEvent e) -> {
            int extent = sp_Resultado.getVerticalScrollBar().getModel().getExtent() + 250;
            int tamanioActual = sp_Resultado.getVerticalScrollBar().getValue();
            int tamanioMaximo = sp_Resultado.getVerticalScrollBar().getMaximum();
            if (extent != 0 && tamanioMaximo != 0 && ((tamanioActual + extent) >= tamanioMaximo)) {
                if (productos.size() >= TAMANIO_PAGINA) {
                    NUMERO_PAGINA += 1;
                    buscar();
                    actualizarProductosCargadosEnFactura();
                    cargarResultadosAlTable();
                }
            }
        });
        tbl_Resultado.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int numeroColumna = tbl_Resultado.columnAtPoint(e.getPoint());
                String name = tbl_Resultado.getColumnName(numeroColumna);
                switch(numeroColumna){
                    case 0 : CRITERIA_SORT = "codigo";
                    break;
                    case 1 : CRITERIA_SORT = "descripcion";
                    break;
                    case 2 : CRITERIA_SORT = "cantidad";
                    break;
                    case 3 : CRITERIA_SORT = "ilimitado";
                    break;
                    case 4 : CRITERIA_SORT = "medida.nombre";
                    break;
                    case 5 : CRITERIA_SORT = "precioLista";
                    break;
                    default: CRITERIA_SORT = "descripcion";
                    break;
                }
                NUMERO_PAGINA = 0;
                productos.clear();
                buscar();
                actualizarProductosCargadosEnFactura();
                limpiarJTable();
                cargarResultadosAlTable();
            }
        });
    }

    private void setIcon() {
        ImageIcon iconoVentana = new ImageIcon(BuscarProductosGUI.class.getResource("/sic/icons/Product_16x16.png"));
        this.setIconImage(iconoVentana.getImage());
    }

    public boolean debeCargarRenglon() {
        return debeCargarRenglon;
    }

    public RenglonFactura getRenglon() {
        return renglon;
    }
    
    public Producto getProductoSeleccionado(){
        return  this.prodSeleccionado;
    }

    private void prepararComponentes() {
        txt_Cantidad.setValue(1.00);
        txt_PorcentajeDescuento.setValue(0.0);
    }

    private void buscar() {
        try {
            String uri = "descripcion=" + txt_CampoBusqueda.getText().trim()
                    + "&codigo=" + txt_CampoBusqueda.getText().trim()
                    + "&idEmpresa=" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa()
                    + "&pagina=" + NUMERO_PAGINA + "&tamanio=" + TAMANIO_PAGINA + "&criteriaSort=" + CRITERIA_SORT;                        
            PaginaRespuestaRest<Producto> response = RestClient.getRestTemplate()
                    .exchange("/productos/busqueda/criteria?" + uri, HttpMethod.GET, null,
                            new ParameterizedTypeReference<PaginaRespuestaRest<Producto>>() {})
                    .getBody();
            productosAux = response.getContent();
            productos.addAll(productosAux);
            prodSeleccionado = null;
            txt_UnidadMedida.setText("");
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
    }

    private void aceptarProducto() {
        this.actualizarEstadoSeleccion();
        if (prodSeleccionado != null) {
            try {
                renglon = RestClient.getRestTemplate().getForObject("/facturas/renglon?"
                        + "idProducto=" + prodSeleccionado.getId_Producto()
                        + "&tipoDeComprobante=" + this.tipoComprobante.name()
                        + "&movimiento=" + tipoMovimiento
                        + "&cantidad=" + txt_Cantidad.getValue().toString()
                        + "&descuentoPorcentaje=" + txt_PorcentajeDescuento.getValue().toString(),
                        RenglonFactura.class);
                boolean existeStock = RestClient.getRestTemplate().getForObject("/productos/"
                        + prodSeleccionado.getId_Producto()
                        + "/stock/disponibilidad?cantidad=" + renglon.getCantidad(),
                        boolean.class);
                if (existeStock || tipoMovimiento == Movimiento.PEDIDO || tipoMovimiento == Movimiento.COMPRA) {
                    debeCargarRenglon = true;
                    this.dispose();
                } else if (!existeStock) {
                    JOptionPane.showMessageDialog(this, ResourceBundle.getBundle("Mensajes")
                            .getString("mensaje_producto_sin_stock_suficiente"), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (RestClientResponseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ResourceAccessException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            debeCargarRenglon = false;
            this.dispose();
        }
    }

    private void actualizarProductosCargadosEnFactura() {
        if (!(tipoMovimiento == Movimiento.PEDIDO || tipoMovimiento == Movimiento.COMPRA)) {
            renglonesFactura.stream().forEach((renglonFactura) -> {
                productos.stream().filter((p) -> (renglonFactura.getDescripcionItem()
                        .equals(p.getDescripcion()) && p.isIlimitado() == false)).forEach((producto) -> {
                            producto.setCantidad(producto.getCantidad() - renglonFactura.getCantidad());
                        });
            });
        }
    }

    private void actualizarEstadoSeleccion() {
        if (txt_Cantidad.isEditValid() && txt_PorcentajeDescuento.isEditValid()) {
            try {
                txt_Cantidad.commitEdit();
                txt_PorcentajeDescuento.commitEdit();
            } catch (ParseException ex) {
                String msjError = "Se produjo un error analizando los campos.";
                LOGGER.error(msjError + " - " + ex.getMessage());
            }
        }
    }

    private void seleccionarProductoEnTable() {
        int fila = tbl_Resultado.getSelectedRow();
        if (fila != -1) {
            prodSeleccionado = productos.get(fila);
            txt_UnidadMedida.setText(prodSeleccionado.getMedida().getNombre());
            ta_ObservacionesProducto.setText(prodSeleccionado.getNota());
            this.actualizarEstadoSeleccion();
        }
    }

    private void cargarResultadosAlTable() {
        productosAux.stream().map((p) -> {
            Object[] fila = new Object[6];
            fila[0] = p.getCodigo();
            fila[1] = p.getDescripcion();
            fila[2] = p.getCantidad();
            fila[3] = p.isIlimitado();
            fila[4] = p.getMedida();
            double precio = (tipoMovimiento == Movimiento.VENTA) ? p.getPrecioLista()
                    : (tipoMovimiento == Movimiento.PEDIDO) ? p.getPrecioLista()
                    : (tipoMovimiento == Movimiento.COMPRA) ? p.getPrecioCosto() : 0.0;
            fila[5] = precio;
            return fila;
        }).forEach((fila) -> {
            modeloTablaResultados.addRow(fila);
        });
        tbl_Resultado.setModel(modeloTablaResultados);
    }

    private void limpiarJTable() {
        modeloTablaResultados = new ModeloTabla();
        tbl_Resultado.setModel(modeloTablaResultados);
        this.setColumnas();
    }

    private void setColumnas() {
        tbl_Resultado.setAutoCreateRowSorter(true);
        //nombres de columnas
        String[] encabezados = new String[6];
        encabezados[0] = "Codigo";
        encabezados[1] = "Descripción";
        encabezados[2] = "Cantidad";
        encabezados[3] = "Sin Límite";
        encabezados[4] = "Unidad";
        String encabezadoPrecio = (tipoMovimiento == Movimiento.VENTA) ? "P. Lista"
                : (tipoMovimiento == Movimiento.PEDIDO) ? "P. Lista"
                : (tipoMovimiento == Movimiento.COMPRA) ? "P.Costo" : "";
        encabezados[5] = encabezadoPrecio;
        modeloTablaResultados.setColumnIdentifiers(encabezados);
        tbl_Resultado.setModel(modeloTablaResultados);

        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaResultados.getColumnCount()];
        tipos[0] = String.class;
        tipos[1] = String.class;
        tipos[2] = Double.class;
        tipos[3] = Boolean.class;
        tipos[4] = String.class;
        tipos[5] = Double.class;
        modeloTablaResultados.setClaseColumnas(tipos);
        tbl_Resultado.getTableHeader().setReorderingAllowed(false);
        tbl_Resultado.getTableHeader().setResizingAllowed(true);

        //render para los tipos de datos
        tbl_Resultado.setDefaultRenderer(Double.class, new RenderTabla());

        //Size de columnas
        //Codigo
        tbl_Resultado.getColumnModel().getColumn(0).setPreferredWidth(130);
        tbl_Resultado.getColumnModel().getColumn(0).setMaxWidth(130);

        //Descripcion
        tbl_Resultado.getColumnModel().getColumn(1).setPreferredWidth(380);
        //Cantidad
        tbl_Resultado.getColumnModel().getColumn(2).setPreferredWidth(67);
        tbl_Resultado.getColumnModel().getColumn(2).setMaxWidth(67);

        //Sin limite
        tbl_Resultado.getColumnModel().getColumn(3).setPreferredWidth(70);
        tbl_Resultado.getColumnModel().getColumn(3).setMaxWidth(70);

        //Medida
        tbl_Resultado.getColumnModel().getColumn(4).setPreferredWidth(70);
        tbl_Resultado.getColumnModel().getColumn(4).setMaxWidth(70);

        //Precio
        tbl_Resultado.getColumnModel().getColumn(5).setPreferredWidth(80);
        tbl_Resultado.getColumnModel().getColumn(5).setMaxWidth(80);
    }

    /**
     * Clase interna para manejar las hotkeys
     */
    class HotKeysHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent evt) {
            if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
                dispose();
            }
        }
    };

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelFondo = new javax.swing.JPanel();
        txt_CampoBusqueda = new javax.swing.JTextField();
        btn_Buscar = new javax.swing.JButton();
        sp_Resultado = new javax.swing.JScrollPane();
        tbl_Resultado = new javax.swing.JTable();
        btn_Aceptar = new javax.swing.JButton();
        txt_UnidadMedida = new javax.swing.JTextField();
        lbl_Cantidad = new javax.swing.JLabel();
        lbl_Descuento = new javax.swing.JLabel();
        txt_Cantidad = new javax.swing.JFormattedTextField();
        txt_PorcentajeDescuento = new javax.swing.JFormattedTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        ta_ObservacionesProducto = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Productos");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        txt_CampoBusqueda.setFont(new java.awt.Font("DejaVu Sans", 0, 17)); // NOI18N
        txt_CampoBusqueda.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txt_CampoBusquedaKeyTyped(evt);
            }
        });

        btn_Buscar.setForeground(java.awt.Color.blue);
        btn_Buscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/22x22_LupaBuscar.png"))); // NOI18N
        btn_Buscar.setFocusable(false);
        btn_Buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_BuscarActionPerformed(evt);
            }
        });

        tbl_Resultado.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tbl_Resultado.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tbl_Resultado.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tbl_Resultado.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbl_Resultado.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbl_ResultadoFocusGained(evt);
            }
        });
        tbl_Resultado.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_ResultadoMouseClicked(evt);
            }
        });
        tbl_Resultado.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbl_ResultadoKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbl_ResultadoKeyReleased(evt);
            }
        });
        sp_Resultado.setViewportView(tbl_Resultado);

        btn_Aceptar.setForeground(java.awt.Color.blue);
        btn_Aceptar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/22x22_FlechaGO.png"))); // NOI18N
        btn_Aceptar.setFocusable(false);
        btn_Aceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_AceptarActionPerformed(evt);
            }
        });

        txt_UnidadMedida.setEditable(false);
        txt_UnidadMedida.setFont(new java.awt.Font("DejaVu Sans", 0, 17)); // NOI18N
        txt_UnidadMedida.setFocusable(false);

        lbl_Cantidad.setText("Cantidad:");

        lbl_Descuento.setText("Descuento (%):");

        txt_Cantidad.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txt_Cantidad.setFont(new java.awt.Font("DejaVu Sans", 0, 17)); // NOI18N
        txt_Cantidad.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_CantidadFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_CantidadFocusLost(evt);
            }
        });
        txt_Cantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_CantidadKeyPressed(evt);
            }
        });

        txt_PorcentajeDescuento.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#,##0.##"))));
        txt_PorcentajeDescuento.setText("0");
        txt_PorcentajeDescuento.setFont(new java.awt.Font("DejaVu Sans", 0, 17)); // NOI18N
        txt_PorcentajeDescuento.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txt_PorcentajeDescuentoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_PorcentajeDescuentoFocusLost(evt);
            }
        });
        txt_PorcentajeDescuento.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_PorcentajeDescuentoKeyReleased(evt);
            }
        });

        ta_ObservacionesProducto.setColumns(20);
        ta_ObservacionesProducto.setEditable(false);
        ta_ObservacionesProducto.setLineWrap(true);
        ta_ObservacionesProducto.setRows(4);
        ta_ObservacionesProducto.setFocusable(false);
        jScrollPane1.setViewportView(ta_ObservacionesProducto);

        javax.swing.GroupLayout panelFondoLayout = new javax.swing.GroupLayout(panelFondo);
        panelFondo.setLayout(panelFondoLayout);
        panelFondoLayout.setHorizontalGroup(
            panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFondoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sp_Resultado)
                    .addGroup(panelFondoLayout.createSequentialGroup()
                        .addComponent(txt_CampoBusqueda)
                        .addGap(0, 0, 0)
                        .addComponent(btn_Buscar))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFondoLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lbl_Descuento)
                            .addComponent(lbl_Cantidad))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txt_PorcentajeDescuento)
                            .addComponent(txt_Cantidad, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_UnidadMedida, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(btn_Aceptar, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panelFondoLayout.setVerticalGroup(
            panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFondoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_Buscar)
                    .addComponent(txt_CampoBusqueda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sp_Resultado, javax.swing.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFondoLayout.createSequentialGroup()
                        .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lbl_Cantidad)
                            .addComponent(txt_Cantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_UnidadMedida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFondoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lbl_Descuento)
                            .addComponent(txt_PorcentajeDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(btn_Aceptar, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        panelFondoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_Buscar, txt_CampoBusqueda});

        panelFondoLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txt_Cantidad, txt_PorcentajeDescuento, txt_UnidadMedida});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelFondo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelFondo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_AceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_AceptarActionPerformed
        this.aceptarProducto();
    }//GEN-LAST:event_btn_AceptarActionPerformed

    private void btn_BuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_BuscarActionPerformed
        NUMERO_PAGINA = 0;
        productos.clear();
        this.buscar();
        this.actualizarProductosCargadosEnFactura();
        this.limpiarJTable();
        this.cargarResultadosAlTable();
    }//GEN-LAST:event_btn_BuscarActionPerformed

    private void tbl_ResultadoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_ResultadoMouseClicked
        this.seleccionarProductoEnTable();
    }//GEN-LAST:event_tbl_ResultadoMouseClicked

    private void txt_CantidadFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_CantidadFocusLost
        this.actualizarEstadoSeleccion();
    }//GEN-LAST:event_txt_CantidadFocusLost

    private void txt_PorcentajeDescuentoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_PorcentajeDescuentoFocusLost
        this.actualizarEstadoSeleccion();
    }//GEN-LAST:event_txt_PorcentajeDescuentoFocusLost

    private void tbl_ResultadoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbl_ResultadoKeyPressed
        if (evt.getKeyCode() == 10) {
            this.aceptarProducto();
        }
        if (evt.getKeyCode() == 9) {
            txt_Cantidad.requestFocus();
        }
    }//GEN-LAST:event_tbl_ResultadoKeyPressed

    private void tbl_ResultadoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbl_ResultadoKeyReleased
        this.seleccionarProductoEnTable();
    }//GEN-LAST:event_tbl_ResultadoKeyReleased

    private void txt_PorcentajeDescuentoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_PorcentajeDescuentoKeyReleased
        if (evt.getKeyCode() == 10) {
            this.aceptarProducto();
        } else {
            this.actualizarEstadoSeleccion();
        }
    }//GEN-LAST:event_txt_PorcentajeDescuentoKeyReleased

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        prodSeleccionado = null;
        NUMERO_PAGINA = 0;
    }//GEN-LAST:event_formWindowClosing

    private void txt_CampoBusquedaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_CampoBusquedaKeyTyped
        evt.setKeyChar(Utilidades.convertirAMayusculas(evt.getKeyChar()));
    }//GEN-LAST:event_txt_CampoBusquedaKeyTyped

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.prepararComponentes();
        this.buscar();
        this.actualizarProductosCargadosEnFactura();
        this.limpiarJTable();
        this.cargarResultadosAlTable();
    }//GEN-LAST:event_formWindowOpened

    private void txt_CantidadFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_CantidadFocusGained
        this.seleccionarProductoEnTable();
        SwingUtilities.invokeLater(() -> {
            txt_Cantidad.selectAll();
        });
    }//GEN-LAST:event_txt_CantidadFocusGained

    private void txt_CantidadKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_CantidadKeyPressed
        if (evt.getKeyCode() == 10) {
            this.aceptarProducto();
        } else {
            this.actualizarEstadoSeleccion();
        }
    }//GEN-LAST:event_txt_CantidadKeyPressed

    private void txt_PorcentajeDescuentoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_PorcentajeDescuentoFocusGained
        SwingUtilities.invokeLater(() -> {
            txt_PorcentajeDescuento.selectAll();
        });
    }//GEN-LAST:event_txt_PorcentajeDescuentoFocusGained

    private void tbl_ResultadoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbl_ResultadoFocusGained
        if ((tbl_Resultado.getSelectedRow() == -1) && (tbl_Resultado.getRowCount() != 0)) {
            tbl_Resultado.setRowSelectionInterval(0, 0);
        }
    }//GEN-LAST:event_tbl_ResultadoFocusGained

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_Aceptar;
    private javax.swing.JButton btn_Buscar;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbl_Cantidad;
    private javax.swing.JLabel lbl_Descuento;
    private javax.swing.JPanel panelFondo;
    private javax.swing.JScrollPane sp_Resultado;
    private javax.swing.JTextArea ta_ObservacionesProducto;
    private javax.swing.JTable tbl_Resultado;
    private javax.swing.JTextField txt_CampoBusqueda;
    private javax.swing.JFormattedTextField txt_Cantidad;
    private javax.swing.JFormattedTextField txt_PorcentajeDescuento;
    private javax.swing.JTextField txt_UnidadMedida;
    // End of variables declaration//GEN-END:variables
}
