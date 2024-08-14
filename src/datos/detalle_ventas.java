
package datos;

public class detalle_ventas {
    private int iddetalleventa;
    private int idventa;
    private int idproducto;
    private int cantidad;
    private Double precioventa;

    public detalle_ventas() {
    }

    public detalle_ventas(int iddetalleventa, int idventa, int idproducto, int cantidad, Double precioventa) {
        this.iddetalleventa = iddetalleventa;
        this.idventa = idventa;
        this.idproducto = idproducto;
        this.cantidad = cantidad;
        this.precioventa = precioventa;
    }

    public int getIddetalleventa() {
        return iddetalleventa;
    }

    public void setIddetalleventa(int iddetalleventa) {
        this.iddetalleventa = iddetalleventa;
    }

    public int getIdventa() {
        return idventa;
    }

    public void setIdventa(int idventa) {
        this.idventa = idventa;
    }

    public int getIdproducto() {
        return idproducto;
    }

    public void setIdproducto(int idproducto) {
        this.idproducto = idproducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecioventa() {
        return precioventa;
    }

    public void setPrecioventa(Double precioventa) {
        this.precioventa = precioventa;
    }
    
    
}
