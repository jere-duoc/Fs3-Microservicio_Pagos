package DuocQuin.Pagos.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventoPagosDTO {
    private Long idSueldo;
    private Long idUsuario;
    private String mensaje;
    private String tipoEnvio;


}
