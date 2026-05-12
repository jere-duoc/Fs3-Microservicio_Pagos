package DuocQuin.Pagos.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import DuocQuin.Pagos.model.PagosModel;
import DuocQuin.Pagos.repository.PagosRepository;
import DuocQuin.Pagos.dto.HorarioDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;


@Service
@Transactional
public class PagosService {
    private static final Logger logger = LoggerFactory.getLogger(PagosService.class);
    private static final String CIRCUIT_BREAKER_NAME = "PagosService";
    private static final double VALOR_HORA = 5000;

    @Autowired
    private PagosRepository pagosRepository;

    @Autowired
    private HorarioClient horarioClient;


    //Listar los sueldos
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "findAllFallback")
    public List<PagosModel> findAll(){
        logger.info("Obteniendo todos los sueldos");
        return pagosRepository.findAll();
    }

    //fallback
    public List<PagosModel> findAllFallback(Exception e){
        logger.error("Circuit Breaker activado en findAll: {}",e.getMessage());
        return List.of();
    }

    //Buscar pago por id
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "findByIdFallback")
    public Optional<PagosModel> findById(Long id) {
        logger.info("Buscando sueldo con ID: {}", id);
        return pagosRepository.findById(id);
    }

    //fallback
    public Optional<PagosModel> findByIdFallback(Long id, Exception e){
        logger.error("Circuit Breaker activado en findById: {}", e.getMessage());
        return Optional.empty();
    }


    //generar sueldo
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "saveFallback")
    public PagosModel save(PagosModel pago){
        logger.info("Guardando nuevo sueldo para usuario: {}", pago.getIdUsuario());

        if (pago.getSueldoBase() == null) {
            throw new IllegalArgumentException("El sueldo base es obligatorio");
        }

        if (pago.getBonos() == null) {
            pago.setBonos(0.0);
        }

        double totalHoras = calcularHorasDelMes(pago.getIdUsuario(), pago.getFechaPago());
        double sueldoTotal = pago.getSueldoBase() + pago.getBonos() + (totalHoras * VALOR_HORA);

        logger.info("Cálculo: base={} + bonos={} + ({}h x ${}={}) = TOTAL: {}",
            pago.getSueldoBase(), pago.getBonos(), totalHoras, VALOR_HORA, 
            totalHoras * VALOR_HORA, sueldoTotal);

        pago.setSueldoTotal(sueldoTotal);
        return pagosRepository.save(pago);
    }

    //fallback
    public PagosModel saveFallback(PagosModel sueldo, Exception e){
        logger.error("Circuit Breaker activado en save: {}", e.getMessage());
        throw new RuntimeException("Servicio de sueldos no disponible", e);
    }


    //actualizar pago
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "updateFallback")
    public PagosModel update(Long id, PagosModel sueldoDetails){
        logger.info("Actualizando sueldo con ID: {}", id);

        PagosModel sueldo = pagosRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Sueldo no encontrado"));

        sueldo.setSueldoBase(sueldoDetails.getSueldoBase());
        sueldo.setBonos(sueldoDetails.getBonos());
        sueldo.setFechaPago(sueldoDetails.getFechaPago());
        sueldo.setIdUsuario(sueldoDetails.getIdUsuario());

        double totalHoras = calcularHorasDelMes(sueldo.getIdUsuario(), sueldo.getFechaPago());
        double sueldoTotal = sueldo.getSueldoBase() + sueldo.getBonos() + (totalHoras * VALOR_HORA);

        logger.info("Actualización - Cálculo: base={} + bonos={} + ({}h x ${}={}) = TOTAL: {}",
            sueldo.getSueldoBase(), sueldo.getBonos(), totalHoras, VALOR_HORA, 
            totalHoras * VALOR_HORA, sueldoTotal);

        sueldo.setSueldoTotal(sueldoTotal);
        return pagosRepository.save(sueldo);
    }

    //fallback
    public PagosModel updateFallback(Long id, PagosModel pagosDetails, Exception e){
        logger.error("Circuit Breaker activado en update: {}", e.getMessage());
        throw new RuntimeException("Servicio de sueldos no disponible", e);
    }


    //eliminar sueldo
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "deleteByIdFallback")
    public void deleteById(Long id){
        logger.info("Eliminando sueldo con ID: {}", id);

        if (!pagosRepository.existsById(id)) {
            throw new RuntimeException("Sueldo no encontrado");
        }

        pagosRepository.deleteById(id);
    }


    //fallback
    public void deleteByIdFallback(Long id, Exception e){
        logger.error("Circuit Breaker activado en deleteById: {}", e.getMessage());
        throw new RuntimeException("Servicio de sueldos no disponible", e);
    }


    /**
     * Calcula las horas trabajadas en el mes correspondiente a la fecha de pago.
     * Usa el endpoint /usuario/{id}/mes del microservicio de Horarios para obtener
     * solo los turnos del mes relevante, evitando sumar horas de meses anteriores.
     * 
     * Usa toMinutes()/60.0 para mayor precisión (evita truncar 7h59m a 7h).
     */
    private double calcularHorasDelMes(Long idUsuario, java.time.LocalDate fechaPago) {
        if (idUsuario == null || fechaPago == null) {
            logger.warn("No se puede calcular horas: idUsuario={}, fechaPago={}", idUsuario, fechaPago);
            return 0;
        }

        int anio = fechaPago.getYear();
        int mes = fechaPago.getMonthValue();

        List<HorarioDTO> horarios = horarioClient.obtenerHorarioPorMes(idUsuario, anio, mes);
        
        double totalHoras = 0;

        for (HorarioDTO h : horarios) {
            if (h.getHoraEntrada() != null && h.getHoraSalida() != null) {
                // Usar minutos para mayor precisión
                double minutos = java.time.Duration.between(
                    h.getHoraEntrada(),
                    h.getHoraSalida()).toMinutes();

                // Si es negativo (turno nocturno que cruza medianoche)
                if (minutos < 0) {
                    minutos = minutos + (24 * 60);
                }

                totalHoras += minutos / 60.0;
            }
        }

        logger.info("Total horas calculadas para usuario {} en {}/{}: {} horas", 
            idUsuario, mes, anio, totalHoras);
        return totalHoras;
    }
}