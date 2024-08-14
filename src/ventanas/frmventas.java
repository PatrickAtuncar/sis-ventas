/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package ventanas;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import datos.cliente;
import datos.conexion;
import datos.detalle_ventas;
import funcionamiento.cliente_func;
import datos.producto;
import datos.ventas;
import funcionamiento.producto_func;
import funcionamiento.ventas_func;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author aldai
 */
public class frmventas extends javax.swing.JInternalFrame {

    cliente_func cf = new cliente_func();
    producto_func pf = new producto_func();
    producto p1 = new producto();
    ventas v = new ventas();
    ventas_func vf = new ventas_func();
    detalle_ventas dv = new detalle_ventas();
    cliente c = new cliente();

    DefaultTableModel modelo = new DefaultTableModel();
    PreparedStatement ps;
    ResultSet rs;
    Connection conn;

    int idp;
    int cant;
    double tpagar;
    double prec;
    double igv;

    public frmventas() {
        super("Ventas", true, true, true, true);
        initComponents();
        generarSerie();
        fecha();
    }

    void fecha() {
        Calendar calendar = new GregorianCalendar();
        txtfecha.setText("" + calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DAY_OF_MONTH));
    }

    void generarSerie() {
        String serie = vf.nroSerie();
        if (serie == null) {
            txtserie.setText("000001");
        } else {
            int incrementar = Integer.parseInt(serie);
            incrementar = incrementar + 1;
            txtserie.setText(String.format("%06d", incrementar));
        }
    }

    void buscarCliente() {
        int r;

        String cod = txtcodcliente.getText();
        if (txtcodcliente.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el dni del cliente");
        } else {
            c = cf.listarId(cod);
            if (c.getDni() != null) {
                txtcliente.setText(c.getNombres());
                txtcodproducto.requestFocus();
            } else {
                r = JOptionPane.showConfirmDialog(this, "El cliente no esta registrado,¿Desea registrarlo?");
                if (r == 0) {
                    frmcliente form = new frmcliente();
                    frmmenu.ventanaPrincipal.add(form);
                    form.setVisible(true);
                }
            }
        }
    }

    void buscarProducto() {
        String nombre = txtcodproducto.getText();

        if (txtcodproducto.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el nombre del prodcuto");
        } else {
            p1 = pf.listarNombre(nombre);
            if (p1.getIdproducto() != 0) {
                txtproducto.setText(" " + p1.getIdproducto());
                txtprecio.setText("" + p1.getPrecio());
                txtstock.setText("" + p1.getStock());
                txtcodproducto.requestFocus();
            } else {
                JOptionPane.showMessageDialog(this, "El producto no esta registrado");
                txtcodproducto.requestFocus();
            }
        }
    }

    void agregarProducto() {
        Double total;
        int nro = 0;
        modelo = (DefaultTableModel) tblistado.getModel();
        nro = nro + 1;
        idp = p1.getIdproducto();
        String nom = txtcodproducto.getText();
        prec = Double.parseDouble(txtprecio.getText());
        cant = Integer.parseInt(spcantidad.getValue().toString());
        int stock = Integer.parseInt(txtstock.getText());
        total = cant * prec;
        ArrayList lista = new ArrayList();

        if (stock > 0) {
            lista.add(nro);
            lista.add(idp);
            lista.add(nom);
            lista.add(cant);
            lista.add(prec);
            lista.add(total);
            Object[] ob = new Object[6];
            ob[0] = lista.get(0);
            ob[1] = lista.get(1);
            ob[2] = lista.get(2);
            ob[3] = lista.get(3);
            ob[4] = lista.get(4);
            ob[5] = lista.get(5);
            modelo.addRow(ob);
            tblistado.setModel(modelo);
            calcularTotal();
        } else {
            JOptionPane.showMessageDialog(this, "No hay stock del producto seleccionado");
        }
    }

    void calcularTotal() {
        tpagar = 0;
        for (int i = 0; i < tblistado.getRowCount(); i++) {
            cant = Integer.parseInt(tblistado.getValueAt(i, 3).toString());
            prec = Double.parseDouble(tblistado.getValueAt(i, 4).toString());
            tpagar = (tpagar + (cant * prec));
        }
        igv = tpagar * 0.18;
        tpagar = tpagar + igv;
        BigDecimal totalRounded = new BigDecimal(tpagar).setScale(2, RoundingMode.HALF_UP);
        txttotalpagar.setText(totalRounded.toString());
    }

    void guardarVenta() {
        int idc = c.getIdcliente();
        int idv = 2;
        String serie = txtserie.getText();
        String fecha = txtfecha.getText();
        double monto = tpagar;
        String estado = "1";

        v.setIdcliente(idc);
        v.setIdvendedor(idv);
        v.setNumserie(serie);
        v.setFecha(fecha);
        v.setMonto(monto);
        v.setEstado(estado);
        vf.guardarVentas(v);
    }

    void guardarDetalle() {
        String idv = vf.idVentas();
        int idve = Integer.parseInt(idv);
        for (int i = 0; i < tblistado.getRowCount(); i++) {
            int idp = Integer.parseInt(tblistado.getValueAt(i, 1).toString());
            int can = Integer.parseInt(tblistado.getValueAt(i, 3).toString());
            double prec = Double.parseDouble(tblistado.getValueAt(i, 4).toString());
            dv.setIdventa(idve);
            dv.setIdproducto(idp);
            dv.setCantidad(cant);
            dv.setPrecioventa(prec);
            vf.guardarDetalleVentas(dv);
        }

    }

    void actualizarStock() {
        for (int i = 0; i < modelo.getRowCount(); i++) {
            producto p = new producto();
            idp = Integer.parseInt(tblistado.getValueAt(i, 1).toString());
            cant = Integer.parseInt(tblistado.getValueAt(i, 3).toString());
            p = pf.listarId(idp);
            int stockAcutal = p.getStock() - cant;
            pf.actualzarStock(stockAcutal, idp);
        }
    }

    void limpiarTabla() {
        for (int i = 0; i < modelo.getRowCount(); i++) {
            modelo.removeRow(i);
            i = i - 1;
        }
    }

    void limpiarFormulario() {
        txtcodcliente.setText("");
        txtcodproducto.setText("");
        txtcliente.setText("");
        txtproducto.setText("");
        spcantidad.setValue(0);
        txtprecio.setText("");
        txtstock.setText("");
        txttotalpagar.setText("");
        txtcodcliente.requestFocus();
    }

    void limpiarAgregar() {
        txtcodproducto.setText("");
        txtproducto.setText("");
        spcantidad.setValue(0);
        txtprecio.setText("");
        txtstock.setText("");
        txtcodcliente.requestFocus();
    }

    public void pdf() {
        try {
            FileOutputStream archivo;
            File file = new File("src/pdf/venta.pdf");
            archivo = new FileOutputStream(file);
            Document documento = new Document();
            PdfWriter.getInstance(documento, archivo);
            documento.open();
            Image img = Image.getInstance("src/imagenes/logo.jpg");

            Paragraph fecha = new Paragraph();
            Font negrita = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.CYAN);
            fecha.add(Chunk.NEWLINE);
            Date date = new Date();
            fecha.add("NroSerie: " + txtserie.getText() + "\nFecha: " + new SimpleDateFormat("dd-MM-yyyy").format(date) + "\n\n");

            PdfPTable encabezado = new PdfPTable(4);
            encabezado.setWidthPercentage(100);
            encabezado.getDefaultCell().setBorder(0);
            float[] columnaEcabezado = new float[]{20f, 30f, 70f, 40f};
            encabezado.setWidths(columnaEcabezado);
            encabezado.setHorizontalAlignment(Element.ALIGN_LEFT);
            encabezado.addCell(img);

            String ruc = "20604816123";
            String nom = "Habitar Market";
            String tel = "951734991";
            String direc = "Av.Lima Jose Galvez";

            encabezado.addCell("");
            encabezado.addCell("Ruc: " + ruc + "\nNombre: " + nom + "\nTelefono: " + tel + "\nDireccion: " + direc);
            encabezado.addCell(fecha);
            documento.add(encabezado);

            Paragraph cliente = new Paragraph();
            cliente.add(Chunk.NEWLINE);
            cliente.add("DATOS DEL CLIENTE\n\n");
            documento.add(cliente);

            PdfPTable tbcliente = new PdfPTable(2);
            tbcliente.setWidthPercentage(100);
            tbcliente.getDefaultCell().setBorder(0);
            float[] columnaCliente = new float[]{30f, 70f};
            tbcliente.setWidths(columnaCliente);
            tbcliente.setHorizontalAlignment(Element.ALIGN_LEFT);
            PdfPCell cliente1 = new PdfPCell(new Phrase("Dni", negrita));
            PdfPCell cliente2 = new PdfPCell(new Phrase("Nombre", negrita));
            cliente1.setBorder(0);
            cliente2.setBorder(0);
            tbcliente.addCell(cliente1);
            tbcliente.addCell(cliente2);
            tbcliente.addCell(txtcodcliente.getText());
            tbcliente.addCell(txtcliente.getText());

            documento.add(tbcliente);

            PdfPTable tbproducto = new PdfPTable(6);
            tbproducto.setWidthPercentage(100);
            tbproducto.getDefaultCell().setBorder(0);
            float[] columnaProducto = new float[]{10f, 10f, 30f, 10f, 25f, 15f};
            tbproducto.setWidths(columnaProducto);
            tbproducto.setHorizontalAlignment(Element.ALIGN_LEFT);
            PdfPCell producto1 = new PdfPCell(new Phrase("Nro", negrita));
            PdfPCell producto2 = new PdfPCell(new Phrase("Codigo", negrita));
            PdfPCell producto3 = new PdfPCell(new Phrase("Producto", negrita));
            PdfPCell producto4 = new PdfPCell(new Phrase("Cantidad", negrita));
            PdfPCell producto5 = new PdfPCell(new Phrase("Precio Unitario", negrita));
            PdfPCell producto6 = new PdfPCell(new Phrase("Total", negrita));
            producto1.setBorder(0);
            producto2.setBorder(0);
            producto3.setBorder(0);
            producto4.setBorder(0);
            producto5.setBorder(0);
            producto6.setBorder(0);
            producto1.setBackgroundColor(BaseColor.DARK_GRAY);
            producto2.setBackgroundColor(BaseColor.DARK_GRAY);
            producto3.setBackgroundColor(BaseColor.DARK_GRAY);
            producto4.setBackgroundColor(BaseColor.DARK_GRAY);
            producto5.setBackgroundColor(BaseColor.DARK_GRAY);
            producto6.setBackgroundColor(BaseColor.DARK_GRAY);
            tbproducto.addCell(producto1);
            tbproducto.addCell(producto2);
            tbproducto.addCell(producto3);
            tbproducto.addCell(producto4);
            tbproducto.addCell(producto5);
            tbproducto.addCell(producto6);
            for (int i = 0; i < tblistado.getRowCount(); i++) {
                int nro = Integer.parseInt(tblistado.getValueAt(i, 0).toString());
                int idprod = Integer.parseInt(tblistado.getValueAt(i, 1).toString());
                String nombre = tblistado.getValueAt(i, 2).toString();
                int cantidad = Integer.parseInt(tblistado.getValueAt(i, 3).toString());
                double pu = Double.parseDouble(tblistado.getValueAt(i, 4).toString());
                double total = Double.parseDouble(tblistado.getValueAt(i, 5).toString());
                tbproducto.addCell(Integer.toString(nro));
                tbproducto.addCell(Integer.toString(idprod));
                tbproducto.addCell(nombre);
                tbproducto.addCell(Integer.toString(cantidad));
                tbproducto.addCell(Double.toString(pu));
                tbproducto.addCell(Double.toString(total));
            }
            documento.add(tbproducto);

            Paragraph info1 = new Paragraph();
            info1.add(Chunk.NEWLINE);
            info1.add("IGV: " + igv);
            info1.setAlignment(Element.ALIGN_RIGHT);
            documento.add(info1);

            Paragraph info = new Paragraph();
            info.add(Chunk.NEWLINE);
            info.add("Total a pagar: " + txttotalpagar.getText());
            info.setAlignment(Element.ALIGN_RIGHT);
            documento.add(info);

            Paragraph mensaje = new Paragraph();
            mensaje.add(Chunk.NEWLINE);
            mensaje.add("Gracias por su Compra");
            mensaje.setAlignment(Element.ALIGN_CENTER);
            documento.add(mensaje);
            documento.close();
            archivo.close();
            Desktop.getDesktop().open(file);
        } catch (DocumentException | IOException e) {
            JOptionPane.showMessageDialog(null, e);
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

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtserie = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtcodcliente = new javax.swing.JTextField();
        txtcodproducto = new javax.swing.JTextField();
        txtprecio = new javax.swing.JTextField();
        btbuscarcliente = new javax.swing.JButton();
        btbucarproducto = new javax.swing.JButton();
        btagregar = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtcliente = new javax.swing.JTextField();
        txtproducto = new javax.swing.JTextField();
        txtstock = new javax.swing.JTextField();
        spcantidad = new javax.swing.JSpinner();
        txtfecha = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblistado = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        btcancelar = new javax.swing.JButton();
        btgenerar = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        txttotalpagar = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();

        setTitle("Ventas");

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));

        jPanel2.setBackground(new java.awt.Color(0, 204, 153));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/logo.jpg"))); // NOI18N

        jLabel2.setText("NRO DE SERIE:");

        txtserie.setEditable(false);
        txtserie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtserieActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(txtserie, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1))
                .addGap(241, 241, 241))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtserie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20))
        );

        jPanel3.setBackground(new java.awt.Color(0, 204, 153));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel3.setText("DNI.CLIENTE:");

        jLabel4.setText("NOM.PRODUCTO:");

        jLabel5.setText("PRECIO:");

        jLabel6.setText("CANTIDAD:");

        txtcodcliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtcodclienteActionPerformed(evt);
            }
        });

        btbuscarcliente.setText("BUSCAR");
        btbuscarcliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btbuscarclienteActionPerformed(evt);
            }
        });

        btbucarproducto.setText("BUSCAR");
        btbucarproducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btbucarproductoActionPerformed(evt);
            }
        });

        btagregar.setText("AGREGAR");
        btagregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btagregarActionPerformed(evt);
            }
        });

        jLabel7.setText("CLIENTE:");

        jLabel8.setText("IDPRODUCTO:");

        jLabel9.setText("STOCK:");

        txtcliente.setEditable(false);

        txtproducto.setEditable(false);

        txtstock.setEditable(false);

        txtfecha.setEditable(false);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel6)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(txtprecio, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(spcantidad, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtcodcliente, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtcodproducto, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btbucarproducto, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btagregar)
                            .addComponent(txtfecha, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(40, 40, 40)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel8)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(btbuscarcliente, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40)
                        .addComponent(jLabel7)))
                .addGap(24, 24, 24)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtcliente, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
                    .addComponent(txtproducto)
                    .addComponent(txtstock))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtfecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(spcantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtcodcliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btbuscarcliente)
                            .addComponent(jLabel7)
                            .addComponent(txtcliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtcodproducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btbucarproducto)
                            .addComponent(jLabel8)
                            .addComponent(txtproducto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtprecio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btagregar)
                                .addComponent(jLabel5))
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel9)
                                .addComponent(txtstock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(33, 33, 33)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(0, 204, 153));
        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        tblistado.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "NRO", "CODIGO", "PRODUCTO", "CANTIDAD", "PRECIO UNITARIO", "TOTAL"
            }
        ));
        jScrollPane1.setViewportView(tblistado);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(0, 204, 153));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btcancelar.setText("CANCELAR");
        btcancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcancelarActionPerformed(evt);
            }
        });

        btgenerar.setText("GENERAR VENTA");
        btgenerar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btgenerarActionPerformed(evt);
            }
        });

        jLabel11.setText("TOTAL A PAGAR:");

        jLabel12.setText("IGV:");

        jTextField2.setEditable(false);
        jTextField2.setText("0.18");
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(btcancelar)
                .addGap(18, 18, 18)
                .addComponent(btgenerar)
                .addGap(27, 27, 27)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txttotalpagar, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btgenerar)
                    .addComponent(jLabel11)
                    .addComponent(txttotalpagar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btcancelar)
                    .addComponent(jLabel12)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(53, 53, 53))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtserieActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtserieActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtserieActionPerformed

    private void txtcodclienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtcodclienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtcodclienteActionPerformed

    private void btbuscarclienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btbuscarclienteActionPerformed
        buscarCliente();
    }//GEN-LAST:event_btbuscarclienteActionPerformed

    private void btbucarproductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btbucarproductoActionPerformed
        buscarProducto();
        spcantidad.requestFocus();
    }//GEN-LAST:event_btbucarproductoActionPerformed

    private void btagregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btagregarActionPerformed
        agregarProducto();
        actualizarStock();
        limpiarAgregar();
        txtcodproducto.requestFocus();
    }//GEN-LAST:event_btagregarActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void btgenerarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btgenerarActionPerformed
        if (txttotalpagar.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Debe ingresar los datos para generar la venta");
        } else {
            guardarVenta();
            guardarDetalle();
            JOptionPane.showMessageDialog(this, "La venta se realizo con exito");
            pdf();
            limpiarTabla();
            limpiarFormulario();
            generarSerie();
        }
    }//GEN-LAST:event_btgenerarActionPerformed

    private void btcancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcancelarActionPerformed
        limpiarTabla();
    }//GEN-LAST:event_btcancelarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btagregar;
    private javax.swing.JButton btbucarproducto;
    private javax.swing.JButton btbuscarcliente;
    private javax.swing.JButton btcancelar;
    private javax.swing.JButton btgenerar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JSpinner spcantidad;
    public static javax.swing.JTable tblistado;
    public static javax.swing.JTextField txtcliente;
    public static javax.swing.JTextField txtcodcliente;
    private javax.swing.JTextField txtcodproducto;
    private javax.swing.JTextField txtfecha;
    private javax.swing.JTextField txtprecio;
    private javax.swing.JTextField txtproducto;
    private javax.swing.JTextField txtserie;
    private javax.swing.JTextField txtstock;
    private javax.swing.JTextField txttotalpagar;
    // End of variables declaration//GEN-END:variables
}
