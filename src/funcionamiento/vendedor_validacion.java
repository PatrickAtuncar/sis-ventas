
package funcionamiento;

import datos.conexion;
import datos.vendedor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;


public class vendedor_validacion {
    PreparedStatement ps;
    ResultSet rs;
    Connection conn;
    
    conexion con = new conexion();
    vendedor v = new vendedor();
    
    public vendedor validarVendedor(String dni,String user){
        String nombre = "";
        String sql = "select * from vendedor where dni = ? and usuario = ?";
        try {
            conn = con.conectar();
            ps = conn.prepareStatement(sql);
            ps.setString(1, dni);
            ps.setString(2, user);
            rs = ps.executeQuery();
            while (rs.next()) {                
                v.setIdvendedor(rs.getInt(1));
                v.setDni(rs.getString(2));
                v.setNombre(rs.getString(3));
                v.setTelefono(rs.getString(4));
                v.setEstado(rs.getString(5));
                v.setUser(rs.getString(6));
            }
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, e);
        }
        return v;
    }
}
