package DuocQuin.Pagos.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class HorarioDTO {
    private Long idHorario;
    private Long idUsuario;
    private LocalTime horaEntrada;
    private LocalTime horaSalida;
    private LocalDate fecha;
    private Integer horasExtra;
}
