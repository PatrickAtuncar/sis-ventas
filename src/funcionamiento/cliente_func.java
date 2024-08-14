package funcionamiento;

import datos.cliente;
import datos.conexion;
import datos.crud;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class cliente_func implements crud {

    PreparedStatement ps;
    ResultSet rs;
    Connection conn;

    conexion con = new conexion();
    
    public cliente listarId(String dni){
        cliente c1 = new cliente();
        String sql = "select * from clientes where dni = ?";
        try {
            conn = con.conectar();
            ps = conn.prepareStatement(sql);
            ps.setString(1,dni);
            rs = ps.executeQuery();
            while (rs.next()) {                
                c1.setIdcliente(rs.getInt(1));
                c1.setDni(rs.getString(2));
                c1.setNombres(rs.getString(3));
                c1.setDireccion(rs.getString(4));
                c1.setEstado(rs.getString(5));
            }
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, e);
        }
        return c1;
    }

    @Override
    public List listar() {
        List<cliente> lista = new ArrayList<>();
        String sql = "select * from clientes";

        try {
            conn = con.conectar();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                cliente c = new cliente();
                c.setIdcliente(rs.getInt(1));
                c.setDni(rs.getString(2));
                c.setDni(rs.getString(2));
                c.setNombres(rs.getString(3));
                c.setDireccion(rs.getString(4));
                c.setEstado(rs.getString(5));
                lista.add(c);
            }
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, e);
        }
        return lista;
    }

    @Override
    public int add(Object[] o) {
        int r = 0;
        String sql = "insert into clientes(dni,nombres,direccion,estado)values (?,?,?,?)";

        try {
            conn = con.conectar();
            ps = conn.prepareStatement(sql);
            ps.setObject(1, o[0]);
            ps.setObject(2, o[1]);
            ps.setObject(3, o[2]);
            ps.setObject(4, o[3]);
            r = ps.executeUpdate();
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, e);
        }
        return r;
    }

    @Override
    public int actualizar(Object[] o) {
        int r = 0;
        String sql = "update clientes set dni=?,nombres=?,direccion=?,estado=? where idcliente=?";

        try {
            conn = con.conectar();
            ps = conn.prepareStatement(sql);
            ps.setObject(1, o[0]);
            ps.setObject(2, o[1]);
            ps.setObject(3, o[2]);
            ps.setObject(4, o[3]);
            ps.setObject(5, o[4]);
            r = ps.executeUpdate();
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, e);
        }
        return r;
    }

    @Override
    public void eliminar(int id) {
        String sql = "delete from clientes where idcliente=?";
        try {
            conn = con.conectar();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, e);
        }
    }
}
