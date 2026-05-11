package DuocQuin.Pagos.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import DuocQuin.Pagos.dto.HorarioDTO;

@Service
public class HorarioClient {

    @Autowired
    private RestTemplate restTemplate;

    private final String URL = "http://localhost:8082/api/horarios/usuario/";

    public List<HorarioDTO> obtenerHorario(Long idUsuario) {

        try {
            HorarioDTO[] response = restTemplate.getForObject(
                URL + idUsuario,
                HorarioDTO[].class
            );

            if (response == null) {
                return List.of();
            }

            return Arrays.asList(response);

        } catch (Exception e) { 
            System.out.println("Error al obtener horarios del usuario: " + idUsuario);
            return List.of();
        }
    }
}