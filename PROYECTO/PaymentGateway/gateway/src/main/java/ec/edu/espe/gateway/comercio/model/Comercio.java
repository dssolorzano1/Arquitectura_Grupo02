package ec.edu.espe.gateway.comercio.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotNull;

import ec.edu.espe.gateway.comision.model.Comision;

@Entity
@Table(name = "GTW_COMERCIO")
public class Comercio implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COD_COMERCIO", nullable = false)
    private Integer codigo;
    @NotNull
    @Column(name = "CODIGO_INTERNO", length = 10, nullable = false)
    private String codigoInterno;
    @NotNull
    @Column(name = "RUC", length = 13, nullable = false)
    private String ruc;
    @NotNull
    @Column(name = "RAZON_SOCIAL", length = 100, nullable = false)
    private String razonSocial;
    @NotNull
    @Column(name = "NOMBRE_COMERCIAL", length = 100, nullable = false)
    private String nombreComercial;
    @NotNull
    @Column(name = "FECHA_CREACION", nullable = false)
    private LocalDateTime fechaCreacion;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "COD_COMISION", nullable = false)
    private Comision comision;
    @NotNull
    @Column(name = "PAGOS_ACEPTADOS", length = 3, nullable = false)
    private String pagosAceptados;
    @NotNull
    @Column(name = "ESTADO", length = 3, nullable = false)
    private String estado;
    @Column(name = "FECHA_ACTIVACION")
    private LocalDateTime fechaActivacion;
    @Column(name = "FECHA_SUSPENSION")
    private LocalDateTime fechaSuspension;

    public Comercio() {
    }

    public Comercio(Integer codigo) {
        this.codigo = codigo;
    }

    // Getters y Setters

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getCodigoInterno() {
        return codigoInterno;
    }

    public void setCodigoInterno(String codigoInterno) {
        this.codigoInterno = codigoInterno;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Comision getComision() {
        return comision;
    }

    public void setComision(Comision comision) {
        this.comision = comision;
    }

    public String getPagosAceptados() {
        return pagosAceptados;
    }

    public void setPagosAceptados(String pagosAceptados) {
        if (!"SIM".equals(pagosAceptados) && !"REC".equals(pagosAceptados) && !"DOS".equals(pagosAceptados)) {
            throw new IllegalArgumentException("PAGOS_ACEPTADOS debe ser SIM, REC o DOS.");
        }
        this.pagosAceptados = pagosAceptados;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        if (!"PEN".equals(estado) && !"ACT".equals(estado) && !"INA".equals(estado) && !"SUS".equals(estado)) {
            throw new IllegalArgumentException("ESTADO debe ser PEN, ACT, INA o SUS.");
        }
        this.estado = estado;
    }

    public LocalDateTime getFechaActivacion() {
        return fechaActivacion;
    }

    public void setFechaActivacion(LocalDateTime fechaActivacion) {
        this.fechaActivacion = fechaActivacion;
    }

    public LocalDateTime getFechaSuspension() {
        return fechaSuspension;
    }

    public void setFechaSuspension(LocalDateTime fechaSuspension) {
        this.fechaSuspension = fechaSuspension;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Comercio other = (Comercio) obj;
        if (codigo == null) {
            if (other.codigo != null)
                return false;
        } else if (!codigo.equals(other.codigo))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "GtwComercio [codigo=" + codigo + ", codigoInterno=" + codigoInterno + ", ruc=" + ruc + ", razonSocial="
                + razonSocial + ", nombreComercial=" + nombreComercial + ", fechaCreacion=" + fechaCreacion
                + ", comision=" + comision + ", pagosAceptados=" + pagosAceptados + ", estado=" + estado
                + ", fechaActivacion=" + fechaActivacion + ", fechaSuspension=" + fechaSuspension + "]";
    }

}
