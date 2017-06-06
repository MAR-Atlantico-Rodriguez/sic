package sic.vista.swing;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import sic.RestClient;
import sic.modelo.EmpresaActiva;
import sic.modelo.PaginaRespuestaRest;
import sic.modelo.Producto;
import sic.modelo.Proveedor;
import sic.modelo.Rubro;
import sic.util.RenderTabla;
import sic.util.Utilidades;

public class ProductosGUI extends JInternalFrame {

    private ModeloTabla modeloTablaResultados = new ModeloTabla();
    private List<Producto> productos = new ArrayList<>();
    private List<Producto> productosAux = new ArrayList<>();
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final Dimension sizeInternalFrame = new Dimension(880, 600);
    private static int totalBusqueda;
    private static int NUMERO_PAGINA = 0;
    private String CRITERIA_SORT = "descripcion";
    private static final int TAMANIO_PAGINA = 100;

    public ProductosGUI() {
        this.initComponents();
        sp_Resultados.getVerticalScrollBar().addAdjustmentListener((AdjustmentEvent e) -> {
            int extent = sp_Resultados.getVerticalScrollBar().getModel().getExtent() + 250;
            int tamanioActual = sp_Resultados.getVerticalScrollBar().getValue();
            int tamanioMaximo = sp_Resultados.getVerticalScrollBar().getMaximum();
            if (extent != 0 && tamanioMaximo != 0 && ((tamanioActual + extent) >= tamanioMaximo)) {
                if (productos.size() >= TAMANIO_PAGINA) {
                    NUMERO_PAGINA += 1;
                    buscar();
                    cargarResultadosAlTable();
                }
            }
        });
        tbl_Resultados.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int numeroColumna = tbl_Resultados.columnAtPoint(e.getPoint());
                switch(numeroColumna){
                    case 0 : CRITERIA_SORT = "codigo";
                    break;
                    case 1 : CRITERIA_SORT = "descripcion";
                    break;
                    case 2 : CRITERIA_SORT = "cantidad";
                    break;
                    case 3 : CRITERIA_SORT = "cantMinima";
                    break;
                    case 4 : CRITERIA_SORT = "ilimitado";
                    break;
                    case 5 : CRITERIA_SORT = "medida.nombre";
                    break;
                    case 6 : CRITERIA_SORT = "precioCosto";
                    break;
                    case 7 : CRITERIA_SORT = "ganancia_porcentaje";
                    break;
                    case 8 : CRITERIA_SORT = "ganancia_neto";
                    break;
                    case 9 : CRITERIA_SORT = "precioVentaPublico";
                    break;
                    case 10 : CRITERIA_SORT = "iva_porcentaje";
                    break;
                    case 11 : CRITERIA_SORT = "iva_neto";
                    break;
                    case 12 : CRITERIA_SORT = "precioLista";
                    break;
                    case 13 : CRITERIA_SORT = "rubro.nombre";
                    break;
                    case 14 : CRITERIA_SORT = "fechaUltimaModificacion";
                    break;
                    case 15 : CRITERIA_SORT = "estanteria";
                    break;
                    case 16 : CRITERIA_SORT = "estante";
                    break;
                    case 17 : CRITERIA_SORT = "proveedor.razonSocial";
                    break;
                    case 18 : CRITERIA_SORT = "fechaAlta";
                    break;
                    case 19 : CRITERIA_SORT = "fechaVencimiento";
                    break;
                    case 20 : CRITERIA_SORT = "nota";
                    break;
                    default: CRITERIA_SORT = "descripcion";
                    break;
                }
                resetScroll();
                buscar();
                limpiarJTable();
                cargarResultadosAlTable();
            }
        });
    }

    private void cargarRubros() {
        try {
            List<Rubro> rubros = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/rubros/empresas/" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(),
                            Rubro[].class)));
            cmb_Rubro.removeAllItems();
            rubros.stream().forEach((r) -> {
                cmb_Rubro.addItem(r);
            });
            if (cmb_Rubro.getSelectedItem() != null && rubros.contains((Rubro) cmb_Rubro.getSelectedItem())) {
                cmb_Rubro.setSelectedItem(cmb_Rubro.getSelectedItem());
            }
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarProveedores() {
        try {
            List<Proveedor> proveedores = new ArrayList(Arrays.asList(RestClient.getRestTemplate()
                    .getForObject("/proveedores/empresas/" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa(),
                            Proveedor[].class)));
            cmb_Proveedor.removeAllItems();
            proveedores.stream().forEach((p) -> {
                cmb_Proveedor.addItem(p);
            });
            if (cmb_Proveedor.getSelectedItem() != null && proveedores.contains((Proveedor) cmb_Proveedor.getSelectedItem())) {
                cmb_Proveedor.setSelectedItem(cmb_Proveedor.getSelectedItem());
            }
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setColumnas() {
        // sorting
        tbl_Resultados.setAutoCreateRowSorter(true);

        //nombres de columnas
        String[] encabezados = new String[21];
        encabezados[0] = "Codigo";
        encabezados[1] = "Descripcion";
        encabezados[2] = "Cantidad";
        encabezados[3] = "Cant. Minima";
        encabezados[4] = "Sin Límite";
        encabezados[5] = "Medida";
        encabezados[6] = "Precio Costo";
        encabezados[7] = "% Ganancia";
        encabezados[8] = "Ganancia";
        encabezados[9] = "PVP";
        encabezados[10] = "% IVA";
        encabezados[11] = "IVA";
        encabezados[12] = "Precio Lista";
        encabezados[13] = "Rubro";
        encabezados[14] = "Fecha U. Modificacion";
        encabezados[15] = "Estanteria";
        encabezados[16] = "Estante";
        encabezados[17] = "Proveedor";
        encabezados[18] = "Fecha Alta";
        encabezados[19] = "Fecha Vencimiento";
        encabezados[20] = "Nota";
        modeloTablaResultados.setColumnIdentifiers(encabezados);
        tbl_Resultados.setModel(modeloTablaResultados);

        //tipo de dato columnas
        Class[] tipos = new Class[modeloTablaResultados.getColumnCount()];
        tipos[0] = String.class;
        tipos[1] = String.class;
        tipos[2] = Double.class;
        tipos[3] = Double.class;
        tipos[4] = Boolean.class;
        tipos[5] = String.class;
        tipos[6] = Double.class;
        tipos[7] = Double.class;
        tipos[8] = Double.class;
        tipos[9] = Double.class;
        tipos[10] = Double.class;
        tipos[11] = Double.class;
        tipos[12] = Double.class;
        tipos[13] = String.class;
        tipos[14] = Date.class;
        tipos[15] = String.class;
        tipos[16] = String.class;
        tipos[17] = String.class;
        tipos[18] = Date.class;
        tipos[19] = Date.class;
        tipos[20] = String.class;
        modeloTablaResultados.setClaseColumnas(tipos);
        tbl_Resultados.getTableHeader().setReorderingAllowed(false);
        tbl_Resultados.getTableHeader().setResizingAllowed(true);

        //render para los tipos de datos
        tbl_Resultados.setDefaultRenderer(Double.class, new RenderTabla());

        //Tamanios de columnas
        tbl_Resultados.getColumnModel().getColumn(0).setPreferredWidth(150);
        tbl_Resultados.getColumnModel().getColumn(1).setPreferredWidth(400);
        tbl_Resultados.getColumnModel().getColumn(2).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(3).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(4).setPreferredWidth(80);
        tbl_Resultados.getColumnModel().getColumn(5).setPreferredWidth(150);
        tbl_Resultados.getColumnModel().getColumn(6).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(7).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(8).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(9).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(10).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(11).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(12).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(13).setPreferredWidth(100);
        tbl_Resultados.getColumnModel().getColumn(14).setPreferredWidth(150);
        tbl_Resultados.getColumnModel().getColumn(15).setPreferredWidth(200);
        tbl_Resultados.getColumnModel().getColumn(16).setPreferredWidth(200);
        tbl_Resultados.getColumnModel().getColumn(17).setPreferredWidth(200);
        tbl_Resultados.getColumnModel().getColumn(18).setPreferredWidth(125);
        tbl_Resultados.getColumnModel().getColumn(19).setPreferredWidth(125);
        tbl_Resultados.getColumnModel().getColumn(20).setPreferredWidth(400);

    }

    private void cargarResultadosAlTable() {
        productosAux.stream().map((producto) -> {
            Object[] fila = new Object[23];
            fila[0] = producto.getCodigo();
            fila[1] = producto.getDescripcion();
            fila[2] = producto.getCantidad();
            fila[3] = producto.getCantMinima();
            fila[4] = producto.isIlimitado();
            fila[5] = producto.getMedida().getNombre();
            fila[6] = producto.getPrecioCosto();
            fila[7] = producto.getGanancia_porcentaje();
            fila[8] = producto.getGanancia_neto();
            fila[9] = producto.getPrecioVentaPublico();
            fila[10] = producto.getIva_porcentaje();
            fila[11] = producto.getIva_neto();
            fila[12] = producto.getPrecioLista();
            fila[13] = producto.getRubro().getNombre();
            fila[14] = producto.getFechaUltimaModificacion();
            fila[15] = producto.getEstanteria();
            fila[16] = producto.getEstante();
            fila[17] = producto.getProveedor().getRazonSocial();
            fila[18] = producto.getFechaAlta();
            fila[19] = producto.getFechaVencimiento();
            fila[20] = producto.getNota();
            return fila;
        }).forEach((fila) -> {
            modeloTablaResultados.addRow(fila);
        });
        tbl_Resultados.setModel(modeloTablaResultados);
        lbl_cantResultados.setText(totalBusqueda + " productos encontrados");
    }
    
    private void resetScroll(){
        NUMERO_PAGINA = 0;
        productos.clear();
        productosAux.clear();
        Point p = new Point(0, 0);
        sp_Resultados.getViewport().setViewPosition(p);
    }

    private void limpiarJTable() {
        modeloTablaResultados = new ModeloTabla();
        tbl_Resultados.setModel(modeloTablaResultados);
        this.setColumnas();
    }

    private void buscar() {
        try {
            long idEmpresa = EmpresaActiva.getInstance().getEmpresa().getId_Empresa();
            String criteriaBusqueda = "/productos/busqueda/criteria?idEmpresa=" + idEmpresa;
            String criteriaCosto = "/productos/valor-stock/criteria?idEmpresa=" + idEmpresa;
            if (chk_Codigo.isSelected()) {
                criteriaBusqueda += "&codigo=" + txt_Codigo.getText().trim();
                criteriaCosto += "&codigo=" + txt_Codigo.getText().trim();
            }
            if (chk_Descripcion.isSelected()) {
                criteriaBusqueda += "&descripcion=" + txt_Descripcion.getText().trim();
                criteriaCosto += "&descripcion=" + txt_Descripcion.getText().trim();
            }
            if (chk_Rubro.isSelected()) {
                criteriaBusqueda += "&idRubro=" + ((Rubro) cmb_Rubro.getSelectedItem()).getId_Rubro();
                criteriaCosto += "&idRubro=" + ((Rubro) cmb_Rubro.getSelectedItem()).getId_Rubro();
            }
            if (chk_Proveedor.isSelected()) {
                criteriaBusqueda += "&idProveedor=" + ((Proveedor) cmb_Proveedor.getSelectedItem()).getId_Proveedor();
                criteriaCosto += "&idProveedor=" + ((Proveedor) cmb_Proveedor.getSelectedItem()).getId_Proveedor();
            }
            if (chk_Disponibilidad.isSelected()) {
                criteriaBusqueda += "&soloFantantes=" + rb_Faltantes.isSelected();
                criteriaCosto += "&soloFantantes=" + rb_Faltantes.isSelected();
            }
            criteriaBusqueda += "&pagina=" + NUMERO_PAGINA + "&tamanio=" + TAMANIO_PAGINA + "&criteriaSort=" + CRITERIA_SORT;
            PaginaRespuestaRest<Producto> response = RestClient.getRestTemplate()
                    .exchange(criteriaBusqueda, HttpMethod.GET, null,
                            new ParameterizedTypeReference<PaginaRespuestaRest<Producto>>() {})
                    .getBody();
            productosAux = response.getContent();
            productos.addAll(productosAux);
            totalBusqueda = response.getTotalElements();
            txt_ValorStock.setValue(RestClient.getRestTemplate().getForObject(criteriaCosto, Double.class));            
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

    private List<Producto> getSeleccionMultipleDeProductos(int[] indices) {
        List<Producto> productosSeleccionados = new ArrayList<>();
        for (int i = 0; i < indices.length; i++) {
            productosSeleccionados.add(productos.get(indices[i]));
        }
        return productosSeleccionados;
    }

    private void lanzarReporteListaDePrecios() {
        if (productos != null) {
            if (Desktop.isDesktopSupported()) {
                try {
                    String uriReporteListaProductosCriteria = "/productos/reporte/criteria?"
                            + "&idEmpresa=" + EmpresaActiva.getInstance().getEmpresa().getId_Empresa();
                    if (chk_Codigo.isSelected()) {
                        uriReporteListaProductosCriteria += "&codigo=" + txt_Codigo.getText().trim();
                    }
                    if (chk_Descripcion.isSelected()) {
                        uriReporteListaProductosCriteria += "&descripcion=" + txt_Descripcion.getText().trim();
                    }
                    if (chk_Rubro.isSelected()) {
                        uriReporteListaProductosCriteria += "&idRubro=" + ((Rubro) cmb_Rubro.getSelectedItem()).getId_Rubro();
                    }
                    if (chk_Proveedor.isSelected()) {
                        uriReporteListaProductosCriteria += "&idProveedor=" + ((Proveedor) cmb_Proveedor.getSelectedItem()).getId_Proveedor();
                    }
                    if (chk_Disponibilidad.isSelected()) {
                        uriReporteListaProductosCriteria += "&soloFantantes=" + rb_Faltantes.isSelected();
                    }
                    byte[] reporte = RestClient.getRestTemplate()
                            .getForObject(uriReporteListaProductosCriteria,
                                    byte[].class);
                    File f = new File(System.getProperty("user.home") + "/ListaPrecios.pdf");
                    Files.write(f.toPath(), reporte);
                    Desktop.getDesktop().open(f);
                } catch (IOException ex) {
                    LOGGER.error(ex.getMessage());
                    JOptionPane.showMessageDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_error_IOException"),
                            "Error", JOptionPane.ERROR_MESSAGE);
                } catch (RestClientResponseException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch (ResourceAccessException ex) {
                    LOGGER.error(ex.getMessage());
                    JOptionPane.showMessageDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_plataforma_no_soportada"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bg_TiposDeListados = new javax.swing.ButtonGroup();
        panelFiltros = new javax.swing.JPanel();
        chk_Codigo = new javax.swing.JCheckBox();
        txt_Codigo = new javax.swing.JTextField();
        chk_Proveedor = new javax.swing.JCheckBox();
        cmb_Proveedor = new javax.swing.JComboBox();
        btn_Buscar = new javax.swing.JButton();
        txt_Descripcion = new javax.swing.JTextField();
        chk_Descripcion = new javax.swing.JCheckBox();
        chk_Rubro = new javax.swing.JCheckBox();
        cmb_Rubro = new javax.swing.JComboBox();
        lbl_cantResultados = new javax.swing.JLabel();
        chk_Disponibilidad = new javax.swing.JCheckBox();
        rb_Todos = new javax.swing.JRadioButton();
        rb_Faltantes = new javax.swing.JRadioButton();
        panelResultados = new javax.swing.JPanel();
        sp_Resultados = new javax.swing.JScrollPane();
        tbl_Resultados = new javax.swing.JTable();
        btn_Nuevo = new javax.swing.JButton();
        btn_Modificar = new javax.swing.JButton();
        btn_Eliminar = new javax.swing.JButton();
        btn_ReporteListaPrecios = new javax.swing.JButton();
        txt_ValorStock = new javax.swing.JFormattedTextField();
        lbl_ValorStock = new javax.swing.JLabel();

        setClosable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Administrar Productos");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Product_16x16.png"))); // NOI18N
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        panelFiltros.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtros"));

        chk_Codigo.setText("Código:");
        chk_Codigo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_CodigoItemStateChanged(evt);
            }
        });

        txt_Codigo.setEnabled(false);

        chk_Proveedor.setText("Proveedor:");
        chk_Proveedor.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_ProveedorItemStateChanged(evt);
            }
        });

        cmb_Proveedor.setEnabled(false);

        btn_Buscar.setForeground(java.awt.Color.blue);
        btn_Buscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Search_16x16.png"))); // NOI18N
        btn_Buscar.setText("Buscar");
        btn_Buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_BuscarActionPerformed(evt);
            }
        });

        txt_Descripcion.setEnabled(false);

        chk_Descripcion.setText("Descripción:");
        chk_Descripcion.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_DescripcionItemStateChanged(evt);
            }
        });

        chk_Rubro.setText("Rubro:");
        chk_Rubro.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_RubroItemStateChanged(evt);
            }
        });

        cmb_Rubro.setEnabled(false);

        lbl_cantResultados.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        chk_Disponibilidad.setText("Disponibilidad:");
        chk_Disponibilidad.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chk_DisponibilidadItemStateChanged(evt);
            }
        });

        bg_TiposDeListados.add(rb_Todos);
        rb_Todos.setText("Todos");
        rb_Todos.setEnabled(false);

        bg_TiposDeListados.add(rb_Faltantes);
        rb_Faltantes.setText("Faltantes");
        rb_Faltantes.setEnabled(false);

        javax.swing.GroupLayout panelFiltrosLayout = new javax.swing.GroupLayout(panelFiltros);
        panelFiltros.setLayout(panelFiltrosLayout);
        panelFiltrosLayout.setHorizontalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelFiltrosLayout.createSequentialGroup()
                        .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chk_Rubro)
                            .addComponent(chk_Descripcion)
                            .addComponent(chk_Codigo)
                            .addComponent(chk_Proveedor))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txt_Descripcion, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmb_Rubro, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_Codigo, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmb_Proveedor, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(chk_Disponibilidad)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(rb_Faltantes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(rb_Todos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panelFiltrosLayout.createSequentialGroup()
                        .addComponent(btn_Buscar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbl_cantResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelFiltrosLayout.setVerticalGroup(
            panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFiltrosLayout.createSequentialGroup()
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Codigo)
                    .addComponent(txt_Codigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chk_Disponibilidad)
                    .addComponent(rb_Todos))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Descripcion)
                    .addComponent(txt_Descripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rb_Faltantes))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Rubro)
                    .addComponent(cmb_Rubro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(chk_Proveedor)
                    .addComponent(cmb_Proveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelFiltrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btn_Buscar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_cantResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelResultados.setBorder(javax.swing.BorderFactory.createTitledBorder("Resultados"));

        tbl_Resultados.setModel(new javax.swing.table.DefaultTableModel(
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
        tbl_Resultados.setToolTipText("Mantener presionado Ctrl ó Shift para seleccionar varios productos");
        tbl_Resultados.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tbl_Resultados.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        sp_Resultados.setViewportView(tbl_Resultados);

        btn_Nuevo.setForeground(java.awt.Color.blue);
        btn_Nuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/AddProduct_16x16.png"))); // NOI18N
        btn_Nuevo.setText("Nuevo");
        btn_Nuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_NuevoActionPerformed(evt);
            }
        });

        btn_Modificar.setForeground(java.awt.Color.blue);
        btn_Modificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/EditProduct_16x16.png"))); // NOI18N
        btn_Modificar.setText("Modificar");
        btn_Modificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ModificarActionPerformed(evt);
            }
        });

        btn_Eliminar.setForeground(java.awt.Color.blue);
        btn_Eliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/DeleteProduct_16x16.png"))); // NOI18N
        btn_Eliminar.setText("Eliminar");
        btn_Eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_EliminarActionPerformed(evt);
            }
        });

        btn_ReporteListaPrecios.setForeground(new java.awt.Color(0, 0, 255));
        btn_ReporteListaPrecios.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sic/icons/Printer_16x16.png"))); // NOI18N
        btn_ReporteListaPrecios.setText("Imprimir");
        btn_ReporteListaPrecios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ReporteListaPreciosActionPerformed(evt);
            }
        });

        txt_ValorStock.setEditable(false);
        txt_ValorStock.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getCurrencyInstance())));
        txt_ValorStock.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        lbl_ValorStock.setText("Valor del Stock:");

        javax.swing.GroupLayout panelResultadosLayout = new javax.swing.GroupLayout(panelResultados);
        panelResultados.setLayout(panelResultadosLayout);
        panelResultadosLayout.setHorizontalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sp_Resultados, javax.swing.GroupLayout.DEFAULT_SIZE, 838, Short.MAX_VALUE)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addComponent(btn_Nuevo)
                .addGap(0, 0, 0)
                .addComponent(btn_Modificar)
                .addGap(0, 0, 0)
                .addComponent(btn_Eliminar)
                .addGap(0, 0, 0)
                .addComponent(btn_ReporteListaPrecios)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbl_ValorStock)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_ValorStock, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_Eliminar, btn_Modificar, btn_Nuevo});

        panelResultadosLayout.setVerticalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelResultadosLayout.createSequentialGroup()
                .addComponent(sp_Resultados, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_ValorStock)
                    .addComponent(txt_ValorStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_ReporteListaPrecios, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_Eliminar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_Modificar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_Nuevo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        panelResultadosLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_Eliminar, btn_Modificar, btn_Nuevo, btn_ReporteListaPrecios});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(panelFiltros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelResultados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void chk_CodigoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_CodigoItemStateChanged
        if (chk_Codigo.isSelected() == true) {
            txt_Codigo.setEnabled(true);
            txt_Codigo.requestFocus();
        } else {
            txt_Codigo.setEnabled(false);
        }
    }//GEN-LAST:event_chk_CodigoItemStateChanged

    private void chk_ProveedorItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_ProveedorItemStateChanged
        if (chk_Proveedor.isSelected() == true) {
            cmb_Proveedor.setEnabled(true);
            this.cargarProveedores();
            cmb_Proveedor.requestFocus();
        } else {
            cmb_Proveedor.removeAllItems();
            cmb_Proveedor.setEnabled(false);
        }
    }//GEN-LAST:event_chk_ProveedorItemStateChanged

    private void btn_BuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_BuscarActionPerformed
        try {
            this.resetScroll();
            this.limpiarJTable();
            this.buscar();
            this.cargarResultadosAlTable();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_BuscarActionPerformed

    private void btn_NuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_NuevoActionPerformed
        DetalleProductoGUI gui_DetalleProducto = new DetalleProductoGUI();
        gui_DetalleProducto.setModal(true);
        gui_DetalleProducto.setLocationRelativeTo(this);
        gui_DetalleProducto.setVisible(true);
        try {
            this.resetScroll();
            this.buscar();
            this.limpiarJTable();
            this.cargarResultadosAlTable();
        } catch (RestClientResponseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_NuevoActionPerformed

    private void btn_EliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_EliminarActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1) {
            int respuesta = JOptionPane.showInternalConfirmDialog(this,
                    ResourceBundle.getBundle("Mensajes").getString("mensaje_pregunta_eliminar_productos"),
                    "Eliminar", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                try {
                    int[] indicesProductos = Utilidades.getSelectedRowsModelIndices(tbl_Resultados);
                    long[] idsProductos = new long[indicesProductos.length];
                    for (int i = 0; i < indicesProductos.length; i++) {
                        idsProductos[i] = this.productos.get(indicesProductos[i]).getId_Producto();
                    }
                    RestClient.getRestTemplate().delete("/productos?idProducto="
                            + Arrays.toString(idsProductos).substring(1, Arrays.toString(idsProductos).length() - 1));
                    this.resetScroll();
                    this.buscar();
                    this.limpiarJTable();
                    this.cargarResultadosAlTable();
                } catch (RestClientResponseException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch (ResourceAccessException ex) {
                    LOGGER.error(ex.getMessage());
                    JOptionPane.showMessageDialog(this,
                            ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_btn_EliminarActionPerformed

    private void btn_ModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ModificarActionPerformed
        if (tbl_Resultados.getSelectedRow() != -1) {
            if (tbl_Resultados.getSelectedRowCount() > 1) {
                //seleccion multiple
                ModificacionProductosBulkGUI gui_ModificacionProductosBulk = new ModificacionProductosBulkGUI(
                        this.getSeleccionMultipleDeProductos(Utilidades.getSelectedRowsModelIndices(tbl_Resultados)));
                gui_ModificacionProductosBulk.setModal(true);
                gui_ModificacionProductosBulk.setLocationRelativeTo(this);
                gui_ModificacionProductosBulk.setVisible(true);
            } else {
                //seleccion unica
                DetalleProductoGUI gui_DetalleProducto = new DetalleProductoGUI(
                        productos.get(Utilidades.getSelectedRowModelIndice(tbl_Resultados)));
                gui_DetalleProducto.setModal(true);
                gui_DetalleProducto.setLocationRelativeTo(this);
                gui_DetalleProducto.setVisible(true);
            }
            try {
                this.resetScroll();
                this.buscar();
                this.limpiarJTable();
                this.cargarResultadosAlTable();
            } catch (RestClientResponseException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ResourceAccessException ex) {
                LOGGER.error(ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        ResourceBundle.getBundle("Mensajes").getString("mensaje_error_conexion"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btn_ModificarActionPerformed

    private void chk_DescripcionItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_DescripcionItemStateChanged
        if (chk_Descripcion.isSelected() == true) {
            txt_Descripcion.setEnabled(true);
            txt_Descripcion.requestFocus();
        } else {
            txt_Descripcion.setEnabled(false);
        }
    }//GEN-LAST:event_chk_DescripcionItemStateChanged

    private void chk_RubroItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_RubroItemStateChanged
        if (chk_Rubro.isSelected() == true) {
            cmb_Rubro.setEnabled(true);
            this.cargarRubros();
            cmb_Rubro.requestFocus();
        } else {
            cmb_Rubro.removeAllItems();
            cmb_Rubro.setEnabled(false);
        }
    }//GEN-LAST:event_chk_RubroItemStateChanged

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        this.setSize(sizeInternalFrame);
        rb_Todos.setSelected(true);
        this.setColumnas();
        try {
            this.setMaximum(true);
        } catch (PropertyVetoException ex) {
            String mensaje = "Se produjo un error al intentar maximizar la ventana.";
            LOGGER.error(mensaje + " - " + ex.getMessage());
            JOptionPane.showInternalMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
    }//GEN-LAST:event_formInternalFrameOpened

    private void btn_ReporteListaPreciosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ReporteListaPreciosActionPerformed
        this.lanzarReporteListaDePrecios();
    }//GEN-LAST:event_btn_ReporteListaPreciosActionPerformed

    private void chk_DisponibilidadItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chk_DisponibilidadItemStateChanged
        if (chk_Disponibilidad.isSelected() == true) {
            rb_Todos.setEnabled(true);
            rb_Faltantes.setEnabled(true);
        } else {
            rb_Todos.setEnabled(false);
            rb_Faltantes.setEnabled(false);
        }
    }//GEN-LAST:event_chk_DisponibilidadItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bg_TiposDeListados;
    private javax.swing.JButton btn_Buscar;
    private javax.swing.JButton btn_Eliminar;
    private javax.swing.JButton btn_Modificar;
    private javax.swing.JButton btn_Nuevo;
    private javax.swing.JButton btn_ReporteListaPrecios;
    private javax.swing.JCheckBox chk_Codigo;
    private javax.swing.JCheckBox chk_Descripcion;
    private javax.swing.JCheckBox chk_Disponibilidad;
    private javax.swing.JCheckBox chk_Proveedor;
    private javax.swing.JCheckBox chk_Rubro;
    private javax.swing.JComboBox cmb_Proveedor;
    private javax.swing.JComboBox cmb_Rubro;
    private javax.swing.JLabel lbl_ValorStock;
    private javax.swing.JLabel lbl_cantResultados;
    private javax.swing.JPanel panelFiltros;
    private javax.swing.JPanel panelResultados;
    private javax.swing.JRadioButton rb_Faltantes;
    private javax.swing.JRadioButton rb_Todos;
    private javax.swing.JScrollPane sp_Resultados;
    private javax.swing.JTable tbl_Resultados;
    private javax.swing.JTextField txt_Codigo;
    private javax.swing.JTextField txt_Descripcion;
    private javax.swing.JFormattedTextField txt_ValorStock;
    // End of variables declaration//GEN-END:variables
}
