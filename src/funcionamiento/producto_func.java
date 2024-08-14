package funcionamiento;

import datos.conexion;
import datos.crud;
import datos.producto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class producto_func implements crud {
    int r;
    PreparedStatement ps;
    ResultSet rs;
    Connection conn;

    conexion con = new conexion();
    
    public int actualzarStock(int cant,int idp){
        String sql = "update producto set stock = ? where idproducto= ?";
        try {
            conn = con.conectar();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, cant);
            ps.setInt(2, idp);
            ps.executeUpdate();
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, e);
        }
        return r;
    }

    public producto listarNombre(String nombre) {
        producto p1 = new producto();
        String sql = "select * from producto where nombre = ?";
        try {
            conn = con.conectar();
            ps = conn.prepareStatement(sql);
            ps.setString(1, nombre);
            rs = ps.executeQuery();
            while (rs.next()) {                

                p1.setIdproducto(rs.getInt(1));
                p1.setNombre(rs.getString(2));
                p1.setPrecio(rs.getDouble(3));
                p1.setStock(rs.getInt(4));
                p1.setEstado(rs.getString(5));
            }
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, e);
        }
        return p1;
    }
    
    public producto listarId(int id) {
        producto p1 = new producto();
        String sql = "select * from producto where idproducto = ?";
        try {
            conn = con.conectar();
            ps = conn.prepareStatement(sql);
            ps.setInt(1,id);
            rs = ps.executeQuery();
            while (rs.next()) {                

                p1.setIdproducto(rs.getInt(1));
                p1.setNombre(rs.getString(2));
                p1.setPrecio(rs.getDouble(3));
                p1.setStock(rs.getInt(4));
                p1.setEstado(rs.getString(5));
            }
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, e);
        }
        return p1;
    }

    @Override
    public List listar() {
        List<producto> lista = new ArrayList<>();
        String sql = "select * from producto";

        try {
            conn = con.conectar();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                producto p = new producto();

                p.setIdproducto(rs.getInt(1));
                p.setNombre(rs.getString(2));
                p.setPrecio(rs.getDouble(3));
                p.setStock(rs.getInt(4));
                p.setEstado(rs.getString(5));
                lista.add(p);
            }
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, e);
        }
        return lista;
    }

    @Override
    public int add(Object[] o) {
        int r = 0;
        String sql = "insert into producto(nombre,precio,stock,estado)values (?,?,?,?)";

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
        String sql = "update producto set nombre=?,precio=?,stock=?,estado=? where idproducto=?";

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
        String sql = "delete from producto where idproducto=?";
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
