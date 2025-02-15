package ec.edu.espe.gateway.transaccion.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ec.edu.espe.gateway.transaccion.model.Transaccion;
import ec.edu.espe.gateway.transaccion.services.TransaccionService;
import ec.edu.espe.gateway.transaccion.services.RecurrenceService;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transacciones")
public class TransaccionController {

    private static final Logger log = LoggerFactory.getLogger(TransaccionController.class);
    private final TransaccionService transaccionService;
    private final RecurrenceService recurrenceService;

    public TransaccionController(TransaccionService transaccionService, RecurrenceService recurrenceService) {
        this.transaccionService = transaccionService;
        this.recurrenceService = recurrenceService;
    }

    @PostMapping("/pos/{codigoPos}")
    public ResponseEntity<?> crearTransaccionPOS(
            @PathVariable String codigoPos,
            @RequestBody Transaccion transaccion) {
        try {
            Transaccion nuevaTransaccion = transaccionService.crearTransaccionPOS(transaccion, codigoPos);
            return ResponseEntity.ok(nuevaTransaccion);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/{codigo}/estado")
    public ResponseEntity<Void> actualizarEstado(
            @PathVariable Integer codigo,
            @RequestParam String nuevoEstado) {
        try {
            transaccionService.actualizarEstado(codigo, nuevoEstado);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Transaccion>> obtenerPorEstado(@PathVariable String estado) {
        List<Transaccion> transacciones = transaccionService.obtenerPorEstado(estado);
        return ResponseEntity.ok(transacciones);
    }

    @GetMapping("/comercio/{codigoComercio}")
    public ResponseEntity<List<Transaccion>> obtenerPorComercioYFecha(
            @PathVariable Integer codigoComercio,
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin) {
        List<Transaccion> transacciones = transaccionService.obtenerPorComercioYFecha(
            codigoComercio, fechaInicio, fechaFin);
        return ResponseEntity.ok(transacciones);
    }

    @PostMapping("/recurrentes/procesar")
    public ResponseEntity<Void> procesarTransaccionesRecurrentes() {
        try {
            recurrenceService.procesarTransaccionesRecurrentes();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PostMapping("/recurrentes/comercio/{codigoComercio}/detener")
    public ResponseEntity<Void> detenerRecurrenciasPorComercio(@PathVariable Integer codigoComercio) {
        try {
            recurrenceService.detenerRecurrenciasPorComercio(codigoComercio);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PostMapping("/sincronizar")
    public ResponseEntity<String> sincronizarTransaccion(@RequestBody Transaccion transaccion) {
        log.info("Recibiendo petición de sincronización desde POS");
        log.info("Datos de transacción recibidos: {}", transaccion);
        log.info("Comercio ID: {}", transaccion.getComercio().getCodigo());
        log.info("Facturación ID: {}", transaccion.getFacturacionComercio().getCodigo());
        
        try {
            transaccionService.procesarTransaccionPOS(transaccion);
            log.info("Sincronización completada exitosamente");
            return ResponseEntity.ok("Transacción sincronizada exitosamente");
            
        } catch (EntityNotFoundException e) {
            log.error("Entidad no encontrada: {}", e.getMessage());
            return ResponseEntity.notFound().build();
            
        } catch (IllegalArgumentException e) {
            log.error("Error de validación: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
            
        } catch (Exception e) {
            log.error("Error inesperado al sincronizar: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body("Error al sincronizar la transacción: " + e.getMessage());
        }
    }
}
