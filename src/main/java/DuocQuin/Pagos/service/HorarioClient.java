package DuocQuin.Pagos.service;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import DuocQuin.Pagos.dto.HorarioDTO;

@Service
public class HorarioClient {

    private static final Logger logger = LoggerFactory.getLogger(HorarioClient.class);

    @Autowired
    private RestTemplate restTemplate;

    private final String BASE_URL = "http://localhost:8082/api/horarios";

    // Obtener TODOS los horarios de un usuario (legacy)
    public List<HorarioDTO> obtenerHorario(Long idUsuario) {
        try {
            HorarioDTO[] response = restTemplate.getForObject(
                BASE_URL + "/usuario/" + idUsuario,
                HorarioDTO[].class
            );

            if (response == null) {
                return List.of();
            }

            logger.info("Obtenidos {} horarios totales para usuario {}", response.length, idUsuario);
            return Arrays.asList(response);

        } catch (Exception e) { 
            logger.error("Error al obtener horarios del usuario {}: {}", idUsuario, e.getMessage());
            return List.of();
        }
    }

    // Obtener solo los horarios de un mes específico (para cálculo de sueldo)
    public List<HorarioDTO> obtenerHorarioPorMes(Long idUsuario, int anio, int mes) {
        try {
            String url = BASE_URL + "/usuario/" + idUsuario + "/mes?anio=" + anio + "&mes=" + mes;
            logger.info("Consultando horarios por mes: {}", url);
            
            HorarioDTO[] response = restTemplate.getForObject(url, HorarioDTO[].class);

            if (response == null) {
                logger.warn("Respuesta nula del servicio de horarios para usuario {} mes {}/{}", idUsuario, mes, anio);
                return List.of();
            }

            logger.info("Obtenidos {} horarios para usuario {} en {}/{}", response.length, idUsuario, mes, anio);
            return Arrays.asList(response);

        } catch (Exception e) {
            logger.error("Error al obtener horarios por mes del usuario {}: {}", idUsuario, e.getMessage());
            return List.of();
        }
    }
}