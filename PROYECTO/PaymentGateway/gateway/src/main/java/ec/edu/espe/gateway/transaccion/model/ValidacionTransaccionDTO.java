package ec.edu.espe.gateway.transaccion.model;

public class ValidacionTransaccionDTO {
    private Banco banco;
    private Double monto;
    private String modalidad;
    private String codigoMoneda;
    private String marca;
    private String fechaExpiracionTarjeta;
    private String nombreTarjeta;
    private String numeroTarjeta;
    private String direccionTarjeta;
    private String cvv;
    private String pais;
    private String numeroCuenta;
    private String gatewayCuenta;
    private String codigoUnicoTransaccion;
    private Double gtwComision;

    public static class Banco {
        private Integer codigo;

        public Integer getCodigo() {
            return codigo;
        }

        public void setCodigo(Integer codigo) {
            this.codigo = codigo;
        }
    }

    // Getters y Setters
    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public String getModalidad() {
        return modalidad;
    }

    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
    }

    public String getCodigoMoneda() {
        return codigoMoneda;
    }

    public void setCodigoMoneda(String codigoMoneda) {
        this.codigoMoneda = codigoMoneda;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getFechaExpiracionTarjeta() {
        return fechaExpiracionTarjeta;
    }

    public void setFechaExpiracionTarjeta(String fechaExpiracionTarjeta) {
        this.fechaExpiracionTarjeta = fechaExpiracionTarjeta;
    }

    public String getNombreTarjeta() {
        return nombreTarjeta;
    }

    public void setNombreTarjeta(String nombreTarjeta) {
        this.nombreTarjeta = nombreTarjeta;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public String getDireccionTarjeta() {
        return direccionTarjeta;
    }

    public void setDireccionTarjeta(String direccionTarjeta) {
        this.direccionTarjeta = direccionTarjeta;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public String getGatewayCuenta() {
        return gatewayCuenta;
    }

    public void setGatewayCuenta(String gatewayCuenta) {
        this.gatewayCuenta = gatewayCuenta;
    }

    public String getCodigoUnicoTransaccion() {
        return codigoUnicoTransaccion;
    }

    public void setCodigoUnicoTransaccion(String codigoUnicoTransaccion) {
        this.codigoUnicoTransaccion = codigoUnicoTransaccion;
    }

    public Double getGtwComision() {
        return gtwComision;
    }

    public void setGtwComision(Double gtwComision) {
        this.gtwComision = gtwComision;
    }
} 