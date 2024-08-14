
package funcionamiento;

import datos.conexion;
import datos.detalle_ventas;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class detalle_venta_func {
    PreparedStatement ps;
    ResultSet rs;
    Connection conn;

    conexion con = new conexion();
    
    public List listar() {
        List<detalle_ventas> lista = new ArrayList<>();
        String sql = "select * from detalle_ventas";

        try {
            conn = con.conectar();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                detalle_ventas dv = new detalle_ventas();

                dv.setIddetalleventa(rs.getInt(1));
                dv.setIdventa(rs.getInt(2));
                dv.setIdproducto(rs.getInt(3));
                dv.setCantidad(rs.getInt(4));
                dv.setPrecioventa(rs.getDouble(5));
                lista.add(dv);
            }
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, e);
        }
        return lista;
    }
}
