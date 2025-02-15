package ec.edu.espe.pos.service;

import ec.edu.espe.pos.model.Configuracion;
import ec.edu.espe.pos.model.Transaccion;
import ec.edu.espe.pos.repository.TransaccionRepository;
import ec.edu.espe.pos.client.GatewayTransaccionClient;
import ec.edu.espe.pos.client.GatewayComercioClient;
import ec.edu.espe.pos.dto.GatewayTransaccionDTO;
import ec.edu.espe.pos.dto.ComercioDTO;
import ec.edu.espe.pos.dto.FacturacionComercioDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Service
public class TransaccionService {

    private static final Logger log = LoggerFactory.getLogger(TransaccionService.class);

    // Constantes para tipos de transacción
    public static final String TIPO_PAGO = "PAG";
    public static final String TIPO_REVERSO = "REV";

    // Constantes para modalidades
    public static final String MODALIDAD_SIMPLE = "SIM";
    public static final String MODALIDAD_RECURRENTE = "REC";

    // Constantes para estados
    public static final String ESTADO_ENVIADO = "ENV";
    public static final String ESTADO_AUTORIZADO = "AUT";
    public static final String ESTADO_RECHAZADO = "REC";

    // Constantes para estados de recibo
    public static final String ESTADO_RECIBO_IMPRESO = "IMP";
    public static final String ESTADO_RECIBO_PENDIENTE = "PEN";

    // Set de códigos de moneda válidos (ISO 4217)
    private static final Set<String> MONEDAS_VALIDAS = Set.of("USD", "EUR", "GBP");

    private static final Set<String> MARCAS_VALIDAS = Set.of("MSCD", "VISA", "AMEX", "DINE");

    private final TransaccionRepository transaccionRepository;
    private final GatewayTransaccionClient gatewayClient;
    private final GatewayComercioClient comercioClient;
    private final ConfiguracionService configuracionService;

    public TransaccionService(TransaccionRepository transaccionRepository,
            GatewayTransaccionClient gatewayClient,
            GatewayComercioClient comercioClient,
            ConfiguracionService configuracionService) {
        this.transaccionRepository = transaccionRepository;
        this.gatewayClient = gatewayClient;
        this.comercioClient = comercioClient;
        this.configuracionService = configuracionService;
    }

    @Transactional
    public Transaccion crear(Transaccion transaccion, String datosSensibles) {
        log.info("Iniciando creación de transacción. Datos recibidos: {}", transaccion);

        // Validar y transformar marca si es necesario
        if (transaccion.getMarca() == null || transaccion.getMarca().length() > 4
                || !MARCAS_VALIDAS.contains(transaccion.getMarca())) {
            throw new IllegalArgumentException(
                    "Marca inválida. Debe ser una de: " + String.join(", ", MARCAS_VALIDAS));
        }

        // Establecer valores predeterminados
        transaccion.setTipo(TIPO_PAGO);
        transaccion.setModalidad(MODALIDAD_SIMPLE);
        transaccion.setMoneda("USD");
        transaccion.setFecha(LocalDateTime.now());
        transaccion.setEstado(ESTADO_ENVIADO);
        transaccion.setEstadoRecibo(ESTADO_RECIBO_PENDIENTE);

        // Generar código único y detalle
        String codigoUnico = "TRX" + System.currentTimeMillis();
        transaccion.setCodigoUnicoTransaccion(codigoUnico);
        transaccion.setDetalle("Transacción POS - " + transaccion.getMarca());

        log.info("Valores establecidos para transacción: marca={}, monto={}",
                transaccion.getMarca(), transaccion.getMonto());

        // Validar campos obligatorios
        validarCamposObligatorios(transaccion);
        log.info("Validación de campos completada exitosamente");

        // Guardar localmente primero
        Transaccion transaccionGuardada = transaccionRepository.save(transaccion);
        log.info("Transacción guardada localmente con ID: {}", transaccionGuardada.getCodigo());

        // Intentar sincronizar con el gateway
        try {
            // Preparar y enviar al gateway
            GatewayTransaccionDTO gatewayDTO = convertirAGatewayDTO(transaccionGuardada, datosSensibles);
            log.info("Enviando al gateway DTO con datos de tarjeta incluidos");

            String respuesta = gatewayClient.sincronizarTransaccion(gatewayDTO);
            log.info("Respuesta del gateway: {}", respuesta);
        } catch (Exception e) {
            // Si falla la sincronización, solo logueamos el error pero no lo propagamos
            log.error("Error al sincronizar con el gateway. La transacción se procesará posteriormente: {}", 
                    e.getMessage());
            // Aquí podrías implementar un mecanismo de reintentos o cola de sincronización
        }

        return transaccionGuardada;
    }

    private void validarCamposObligatorios(Transaccion transaccion) {
        if (transaccion.getMonto() == null || transaccion.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero");
        }
        if (transaccion.getMarca() == null || transaccion.getMarca().trim().isEmpty()) {
            throw new IllegalArgumentException("La marca es obligatoria");
        }
        if (transaccion.getDetalle() == null || transaccion.getDetalle().trim().isEmpty()) {
            throw new IllegalArgumentException("El detalle es obligatorio");
        }
        if (!MONEDAS_VALIDAS.contains(transaccion.getMoneda())) {
            throw new IllegalArgumentException("Moneda no válida");
        }
    }

    private GatewayTransaccionDTO convertirAGatewayDTO(Transaccion transaccion, String datosSensibles) {
        GatewayTransaccionDTO dto = new GatewayTransaccionDTO();
        
        try {
            // Obtener configuración local del POS
            log.info("Obteniendo configuración actual del POS");
            Configuracion config = configuracionService.obtenerConfiguracionActual();
            log.info("Configuración obtenida: codigoComercio={}, codigoPOS={}, modelo={}",
                    config.getCodigoComercio(), config.getPk().getCodigo(), config.getPk().getModelo());
            
            // Crear DTO de comercio con el código de la configuración
            ComercioDTO comercio = new ComercioDTO();
            comercio.setCodigo(config.getCodigoComercio());
            
            // Obtener facturación usando el código del comercio
            log.info("Consultando facturación para el comercio: {}", comercio.getCodigo());
            FacturacionComercioDTO facturacion = comercioClient.obtenerFacturacionPorComercio(comercio.getCodigo());
            log.info("Facturación obtenida: {}", facturacion);
            
            dto.setComercio(comercio);
            dto.setFacturacionComercio(facturacion);

            // Datos de la transacción
            dto.setTipo(transaccion.getModalidad());
            dto.setMarca(transaccion.getMarca());
            dto.setDetalle(transaccion.getDetalle());
            dto.setMonto(transaccion.getMonto());
            dto.setCodigoUnicoTransaccion(transaccion.getCodigoUnicoTransaccion());
            dto.setFecha(transaccion.getFecha());
            dto.setEstado(transaccion.getEstado());
            dto.setMoneda(transaccion.getMoneda());
            dto.setPais("EC");

            // Agregar datos del POS
            dto.setCodigoPos(config.getPk().getCodigo());
            dto.setModeloPos(config.getPk().getModelo());

            // Agregar datos sensibles de la tarjeta
            dto.setTarjeta(datosSensibles);
            dto.setFechaEjecucionRecurrencia(null);
            dto.setFechaFinRecurrencia(null);

            log.info("DTO preparado para enviar al gateway. Comercio código: {}, POS código: {}, modelo: {}",
                    comercio.getCodigo(), config.getPk().getCodigo(), config.getPk().getModelo());

        } catch (Exception e) {
            log.error("Error al obtener datos del comercio: {}", e.getMessage());
            throw new RuntimeException("Error al preparar datos para el gateway", e);
        }

        return dto;
    }
}