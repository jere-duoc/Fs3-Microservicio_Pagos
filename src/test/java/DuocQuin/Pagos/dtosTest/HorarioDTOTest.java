package DuocQuin.Pagos.dtosTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import DuocQuin.Pagos.dto.HorarioDTO;

class HorarioDTOTest {

    @Test
    void gettersYSetters() {
        HorarioDTO dto = new HorarioDTO();

        dto.setIdHorario(1L);
        dto.setIdUsuario(2L);
        dto.setHoraEntrada(LocalTime.of(8, 0));
        dto.setHoraSalida(LocalTime.of(16, 0));
        dto.setFecha(LocalDate.of(2026, 6, 15));
        dto.setHorasExtra(2);

        assertEquals(1L, dto.getIdHorario());
        assertEquals(2L, dto.getIdUsuario());
        assertEquals(LocalTime.of(8, 0), dto.getHoraEntrada());
        assertEquals(LocalTime.of(16, 0), dto.getHoraSalida());
        assertEquals(LocalDate.of(2026, 6, 15), dto.getFecha());
        assertEquals(2, dto.getHorasExtra());
    }
}