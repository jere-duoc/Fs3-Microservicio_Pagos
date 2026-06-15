package DuocQuin.Pagos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import DuocQuin.Pagos.model.PagosModel;

class PagoTest {

    @Test
    void modelSetterGetterTest() {
        PagosModel model = new PagosModel();

        model.setIdUsuario(1L);
        model.setSueldoBase(500000.0);
        model.setBonos(10000.0);
        model.setSueldoTotal(510000.0);
        model.setFechaPago(LocalDate.now());

        assertEquals(1L, model.getIdUsuario());
        assertEquals(500000.0, model.getSueldoBase());
    }

}
