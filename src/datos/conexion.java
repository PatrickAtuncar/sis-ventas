
package datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class conexion {
    static Connection conn = null;
    public static Connection conectar(){
        String url = "jdbc:sqlserver://localhost\\Patrick\\SQLEXPRESS:1433;databaseName=bd_ventas;"+"encrypt=false;trustServerCertificate=true";
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("error de sql " + e.getMessage());
        }
        try {
            conn = DriverManager.getConnection(url, "sa", "1234");
        } catch (SQLException e) {
            System.out.println("error de conexion del driver " + e.getMessage());
        }
        return conn;
    }
}
