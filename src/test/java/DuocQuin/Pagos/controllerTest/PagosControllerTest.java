package DuocQuin.Pagos.controllerTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import DuocQuin.Pagos.controller.PagosController;
import DuocQuin.Pagos.model.PagosModel;
import DuocQuin.Pagos.service.PagosService;

@WebMvcTest(PagosController.class)
@AutoConfigureMockMvc(addFilters = false)
class PagosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PagosService pagosService;

    @Test
    void listar() throws Exception {
        PagosModel pago = new PagosModel();
        pago.setIdSueldo(1L);

        when(pagosService.findAll()).thenReturn(List.of(pago));

        mockMvc.perform(get("/api/sueldos")).andExpect(status().isOk());
    }

    @Test
    void obtenerPorId() throws Exception {
        PagosModel pago = new PagosModel();
        pago.setIdSueldo(1L);

        when(pagosService.findById(1L)).thenReturn(Optional.of(pago));

        mockMvc.perform(get("/api/sueldos/1")).andExpect(status().isOk());
    }

    @Test
    void obtenerPorIdNoExiste() throws Exception {
        when(pagosService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/sueldos/1")).andExpect(status().isNotFound());
    }

    @Test
    void crear() throws Exception {

        PagosModel pago = new PagosModel();
        pago.setIdSueldo(1L);
        pago.setIdUsuario(1L);
        pago.setSueldoBase(500000.0);
        pago.setBonos(50000.0);
        pago.setFechaPago(LocalDate.of(2026, 6, 1));

        when(pagosService.save(any(PagosModel.class))).thenReturn(pago);

        mockMvc.perform(post("/api/sueldos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pago)))
                .andExpect(status().isOk());
    }

    @Test
    void actualizar() throws Exception {
        PagosModel pago = new PagosModel();
        pago.setIdSueldo(1L);
        pago.setIdUsuario(1L);

        when(pagosService.update(eq(1L), any(PagosModel.class))).thenReturn(pago);

        mockMvc.perform(put("/api/sueldos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pago)))
                .andExpect(status().isOk());
    }

    @Test
    void eliminar() throws Exception {
        mockMvc.perform(delete("/api/sueldos/1")).andExpect(status().isOk());
    }
}