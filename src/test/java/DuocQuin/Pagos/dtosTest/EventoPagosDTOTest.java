package DuocQuin.Pagos.dtosTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import DuocQuin.Pagos.dto.EventoPagosDTO;

class EventoPagosDTOTest {

    @Test
    void gettersYSetters() {
        EventoPagosDTO dto = new EventoPagosDTO();

        dto.setIdSueldo(1L);
        dto.setIdUsuario(2L);
        dto.setMensaje("Pago generado");
        dto.setTipoEnvio("PLATAFORMA");

        assertEquals(1L, dto.getIdSueldo());
        assertEquals(2L, dto.getIdUsuario());
        assertEquals("Pago generado", dto.getMensaje());
        assertEquals("PLATAFORMA", dto.getTipoEnvio());
    }
}