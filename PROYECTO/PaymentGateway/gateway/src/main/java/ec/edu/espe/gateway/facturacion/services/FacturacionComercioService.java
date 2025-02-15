package ec.edu.espe.gateway.facturacion.services;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import ec.edu.espe.gateway.facturacion.model.FacturacionComercio;
import ec.edu.espe.gateway.facturacion.repository.FacturacionComercioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

@Service
@Transactional
public class FacturacionComercioService {

    public static final String ESTADO_ACTIVO = "ACT";
    public static final String ESTADO_FACTURADO = "FAC";
    public static final String ESTADO_PAGADO = "PAG";
    private static final int MAX_TRANSACCIONES_DIGITOS = 9;
    private static final int MAX_VALOR_ENTERO = 16;
    private static final int MAX_VALOR_DECIMAL = 4;
    private static final int MAX_CODIGO_FACTURACION = 20;
    private static final String REGEX_CODIGO_FACTURACION = "^[a-zA-Z0-9]{1," + MAX_CODIGO_FACTURACION + "}$";

    private final FacturacionComercioRepository facturacionComercioRepository;

    public FacturacionComercioService(FacturacionComercioRepository facturacionComercioRepository) {
        this.facturacionComercioRepository = facturacionComercioRepository;
    }

    @Transactional(value = TxType.NEVER)
    public FacturacionComercio obtenerPorCodigo(Integer codigo) {
        Optional<FacturacionComercio> optional = facturacionComercioRepository.findById(codigo);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new EntityNotFoundException("No existe ninguna facturación de comercio con el código: " + codigo);
        }
    }

    @Transactional(value = TxType.NEVER)
    public List<FacturacionComercio> obtenerPorEstado(String estado) {
        return facturacionComercioRepository.findByEstado(estado);
    }

    public void crear(FacturacionComercio facturacionComercio) {
        try {
            validarFacturacion(facturacionComercio);
            facturacionComercio.setEstado(ESTADO_ACTIVO);
            facturacionComercioRepository.save(facturacionComercio);
        } catch (Exception ex) {
            throw new RuntimeException("No se pudo crear la facturación de comercio. Motivo: " + ex.getMessage());
        }
    }

    public void actualizar(FacturacionComercio facturacionComercio) {
        try {
            FacturacionComercio existente = obtenerPorCodigo(facturacionComercio.getCodigo());
            validarFacturacion(facturacionComercio);
            existente.setFechaInicio(facturacionComercio.getFechaInicio());
            existente.setFechaFin(facturacionComercio.getFechaFin());
            existente.setTransaccionesProcesadas(facturacionComercio.getTransaccionesProcesadas());
            existente.setTransaccionesAutorizadas(facturacionComercio.getTransaccionesAutorizadas());
            existente.setTransaccionesRechazadas(facturacionComercio.getTransaccionesRechazadas());
            existente.setTransaccionesReversadas(facturacionComercio.getTransaccionesReversadas());
            existente.setValor(facturacionComercio.getValor());
            existente.setEstado(facturacionComercio.getEstado());
            existente.setCodigoFacturacion(facturacionComercio.getCodigoFacturacion());
            existente.setFechaFacturacion(facturacionComercio.getFechaFacturacion());
            existente.setFechaPago(facturacionComercio.getFechaPago());
            facturacionComercioRepository.save(existente);
        } catch (Exception ex) {
            throw new RuntimeException("No se pudo actualizar la facturación de comercio. Motivo: " + ex.getMessage());
        }
    }

    private void validarFacturacion(FacturacionComercio facturacionComercio) {
        if (facturacionComercio.getFechaInicio().isAfter(facturacionComercio.getFechaFin())) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin.");
        }
        if (facturacionComercio.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El valor de la facturación debe ser mayor a cero.");
        }
        if (facturacionComercio.getValor().scale() > MAX_VALOR_DECIMAL) {
            throw new IllegalArgumentException("El valor de la facturación no puede tener más de " + MAX_VALOR_DECIMAL + " decimales.");
        }
        if (facturacionComercio.getValor().precision() - facturacionComercio.getValor().scale() > MAX_VALOR_ENTERO) {
            throw new IllegalArgumentException("El valor de la facturación no puede tener más de " + MAX_VALOR_ENTERO + " dígitos antes del punto decimal.");
        }
        if (!facturacionComercio.getCodigoFacturacion().matches(REGEX_CODIGO_FACTURACION)) {
            throw new IllegalArgumentException("El código de facturación debe ser alfanumérico y no exceder los " + MAX_CODIGO_FACTURACION + " caracteres.");
        }
        if (facturacionComercio.getTransaccionesProcesadas() < 0 || facturacionComercio.getTransaccionesProcesadas() > MAX_TRANSACCIONES_DIGITOS) {
            throw new IllegalArgumentException("El número de transacciones procesadas no puede ser negativo ni exceder los 9 dígitos.");
        }
        if (facturacionComercio.getTransaccionesAutorizadas() < 0 || facturacionComercio.getTransaccionesAutorizadas() > MAX_TRANSACCIONES_DIGITOS) {
            throw new IllegalArgumentException("El número de transacciones autorizadas no puede ser negativo ni exceder los 9 dígitos.");
        }
        if (facturacionComercio.getTransaccionesRechazadas() < 0 || facturacionComercio.getTransaccionesRechazadas() > MAX_TRANSACCIONES_DIGITOS) {
            throw new IllegalArgumentException("El número de transacciones rechazadas no puede ser negativo ni exceder los 9 dígitos.");
        }
        if (facturacionComercio.getTransaccionesReversadas() < 0 || facturacionComercio.getTransaccionesReversadas() > MAX_TRANSACCIONES_DIGITOS) {
            throw new IllegalArgumentException("El número de transacciones reversadas no puede ser negativo ni exceder los 9 dígitos.");
        }
    }

    @Transactional(value = TxType.NEVER)
    public List<FacturacionComercio> obtenerFacturacionesPendientesPago() {
        return facturacionComercioRepository.findByEstado(ESTADO_FACTURADO);
    }

    public void marcarComoPagado(Integer codigo) {
        try {
            FacturacionComercio existente = obtenerPorCodigo(codigo);
            if (!ESTADO_FACTURADO.equals(existente.getEstado())) {
                throw new IllegalStateException(
                        "Solo se pueden marcar como pagadas las facturaciones en estado 'FAC'.");
            }
            existente.setEstado(ESTADO_PAGADO);
            existente.setFechaPago(java.time.LocalDate.now());
            facturacionComercioRepository.save(existente);
        } catch (Exception ex) {
            throw new RuntimeException("No se pudo marcar la facturación como pagada. Motivo: " + ex.getMessage());
        }
    }
}