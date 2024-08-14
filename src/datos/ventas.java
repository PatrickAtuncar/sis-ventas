
package datos;

public class ventas {
    private int idventa;
    private int idcliente;
    private int idvendedor;
    private String numserie;
    private String fecha;
    private Double monto;
    private String estado;

    public ventas() {
    }

    public ventas(int idventa, int idcliente, int idvendedor, String numserie, String fecha, Double monto, String estado) {
        this.idventa = idventa;
        this.idcliente = idcliente;
        this.idvendedor = idvendedor;
        this.numserie = numserie;
        this.fecha = fecha;
        this.monto = monto;
        this.estado = estado;
    }

    public int getIdventa() {
        return idventa;
    }

    public void setIdventa(int idventa) {
        this.idventa = idventa;
    }

    public int getIdcliente() {
        return idcliente;
    }

    public void setIdcliente(int idcliente) {
        this.idcliente = idcliente;
    }

    public int getIdvendedor() {
        return idvendedor;
    }

    public void setIdvendedor(int idvendedor) {
        this.idvendedor = idvendedor;
    }

    public String getNumserie() {
        return numserie;
    }

    public void setNumserie(String numserie) {
        this.numserie = numserie;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    
    
    
}
