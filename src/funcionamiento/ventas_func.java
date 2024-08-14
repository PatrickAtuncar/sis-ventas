
package funcionamiento;

import datos.conexion;
import datos.detalle_ventas;
import datos.vendedor;
import datos.ventas;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class ventas_func {
    PreparedStatement ps;
    ResultSet rs;
    Connection conn;
    int r = 0;

    conexion con = new conexion();
    
    public List listar() {
        List<ventas> lista = new ArrayList<>();
        String sql = "select * from ventas";

        try {
            conn = con.conectar();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                ventas v = new ventas();

                v.setIdventa(rs.getInt(1));
                v.setIdcliente(rs.getInt(2));
                v.setIdvendedor(rs.getInt(3));
                v.setNumserie(rs.getString(4));
                v.setFecha(rs.getString(5));
                v.setMonto(rs.getDouble(6));
                v.setEstado(rs.getString(7));
                lista.add(v);
            }
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, e);
        }
        return lista;
    }
    
    public String nroSerie(){
        String serie = "";
        String sql = "select max(numeroserie) from ventas ";
        try {
            conn = con.conectar();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {                
                serie = rs.getString(1);
            }
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, e);
        }
        return serie;
    }
    
    public String idVentas(){
        String idv = "";
        String sql = "select max (idventa) from ventas";
        try {
            conn = con.conectar();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next()) {                
                idv = rs.getString(1);
            }
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, e);
        }
        return idv;
    }
    
    public int  guardarVentas(ventas v){
        ventas venta = new ventas();
        String sql = "insert into ventas(idcliente,idvendedor,numeroserie,fechaventa,monto,estado) values (?,?,?,?,?,?)";
        try {
            conn = con.conectar();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, v.getIdcliente());
            ps.setInt(2, v.getIdvendedor());
            ps.setString(3, v.getNumserie());
            ps.setString(4, v.getFecha());
            ps.setDouble(5, v.getMonto());
            ps.setString(6, v.getEstado());
            r = ps.executeUpdate();
            
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, e);
        }
        
        return r;
    }
    public int guardarDetalleVentas(detalle_ventas dv){
        String sql = "insert into detalle_ventas(idventa,idproducto,cantidad,precioventa) values(?,?,?,?)";
        try {
            conn = con.conectar();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, dv.getIdventa());
            ps.setInt(2, dv.getIdproducto());
            ps.setInt(3, dv.getCantidad());
            ps.setDouble(4, dv.getPrecioventa());
            ps.executeUpdate();
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, e);
        }
        
        return r;
    }
}
