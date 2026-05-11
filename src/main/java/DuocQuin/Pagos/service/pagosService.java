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
    private static final Logger logger =LoggerFactory.getLogger(PagosService.class);
    private static final String CIRCUIT_BREAKER_NAME ="PagosService" ;

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
    public Optional<PagosModel> findByIdFallback(Long id,Exception e){
        logger.error("Circuit Breaker activado en findById: {}", e.getMessage());
        return Optional.empty();
    }


    //generar sueldo
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "saveFallback")
    public PagosModel save(PagosModel pago){
        logger.info("guardando nuevo sueldo");

        if (pago.getSueldoBase() == null) {
            throw new IllegalArgumentException("El sueldo base es obligatorio");
        }

        if (pago.getBonos() == null) {
            pago.setBonos(0.0);
        }

        List<HorarioDTO> horarios = horarioClient.obtenerHorario(pago.getIdUsuario());

        double totalHoras = 0;

        for (HorarioDTO h : horarios) {

            if (h.getHoraEntrada() != null && h.getHoraSalida() != null) {

                double horas = java.time.Duration.between(
                    h.getHoraEntrada(),
                    h.getHoraSalida()).toHours();

                if (horas < 0) {
                    horas = horas + 24;
                }
                totalHoras += horas;
            }
        }

        double valorHora = 5000;
        double sueldoTotal = (totalHoras * valorHora) + pago.getSueldoBase() + pago.getBonos();

        pago.setSueldoTotal(sueldoTotal);
        return pagosRepository.save(pago);
    }

    //fallback
    public PagosModel saveFallback(PagosModel sueldo,  Exception e){
        logger.error("Circuit Breaker activado en save: {}", e.getMessage());
        throw new RuntimeException("Servicio de sueldos no disponible", e);
    }


    //actualizar pago
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "updateFallback")
    public PagosModel update(
        Long id,
        PagosModel sueldoDetails){
        logger.info("Actualizando sueldo con ID: {}", id);

        PagosModel sueldo = pagosRepository.findById(id).orElseThrow(() -> new RuntimeException("Sueldo no encontrado"));

        sueldo.setSueldoBase(sueldoDetails.getSueldoBase());
        sueldo.setBonos(sueldoDetails.getBonos());
        sueldo.setFechaPago(sueldoDetails.getFechaPago());
        sueldo.setIdUsuario(sueldoDetails.getIdUsuario());

        List<HorarioDTO> horarios = horarioClient.obtenerHorario(sueldo.getIdUsuario());

        double totalHoras = 0;

        for (HorarioDTO h : horarios) {

            if (h.getHoraEntrada() != null && h.getHoraSalida() != null) {

                double horas = java.time.Duration.between(
                    h.getHoraEntrada(),
                    h.getHoraSalida()).toHours();

                if (horas < 0) {
                    horas = horas + 24;
                }

                totalHoras += horas;
            }
        }

        double valorHora = 5000;
        double sueldoTotal = (totalHoras * valorHora) + sueldo.getSueldoBase() + sueldo.getBonos();

        sueldo.setSueldoTotal(sueldoTotal);
        return pagosRepository.save(sueldo);
    }

    //fallback
    public PagosModel updateFallback(
            Long id,
            PagosModel pagosDetails,
            Exception e){
        logger.error("Circuit Breaker activado en update: {}", e.getMessage());
        throw new RuntimeException("Servicio de sueldos no disponible", e);
    }


    //eliminar sueldo
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "deleteByIdFallback")
    public void deleteById(Long id){
        logger.info("Eliminando sueldo con ID: {}", id);

        if (!pagosRepository.existsById(id)) {
            throw new RuntimeException("Sueldo no encontrado");}

        pagosRepository.deleteById(id);
    }


    //fallback
    public void deleteByIdFallback(
            Long id,
            Exception e){
        logger.error("Circuit Breaker activado en deleteById: {}", e.getMessage());
        throw new RuntimeException("Servicio de sueldos no disponible", e);
    }
}