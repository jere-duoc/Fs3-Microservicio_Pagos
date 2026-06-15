package DuocQuin.Pagos.serviceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import DuocQuin.Pagos.dto.HorarioDTO;
import DuocQuin.Pagos.model.PagosModel;
import DuocQuin.Pagos.repository.PagosRepository;
import DuocQuin.Pagos.service.HorarioClient;
import DuocQuin.Pagos.service.PagosService;

@ExtendWith(MockitoExtension.class)
class PagosServiceTest {

    @Mock
    private PagosRepository repository;

    @Mock
    private HorarioClient horarioClient;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private PagosService service;

    @Test
    void listarPagos() {
        when(repository.findAll()).thenReturn(List.of(new PagosModel(), new PagosModel()));

        List<PagosModel> resultado = service.findAll();

        assertEquals(2, resultado.size());
        verify(repository).findAll();
    }

    @Test
    void obtenerPagoPorId() {
        PagosModel pago = new PagosModel();
        pago.setIdSueldo(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(pago));

        Optional<PagosModel> resultado = service.findById(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getIdSueldo());
    }

    @Test
    void eliminarPago() {
        when(repository.existsById(1L)).thenReturn(true);

        service.deleteById(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    void eliminarPagoNoExistente() {
        when(repository.existsById(1L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> service.deleteById(1L));
    }

    @Test
    void guardarPagoSinBonos() {
        PagosModel pago = new PagosModel();
        pago.setIdUsuario(1L);
        pago.setSueldoBase(500000.0);
        pago.setFechaPago(LocalDate.of(2026, 6, 1));

        HorarioDTO horario = new HorarioDTO();
        horario.setHoraEntrada(LocalTime.of(8, 0));
        horario.setHoraSalida(LocalTime.of(16, 0));

        when(horarioClient.obtenerHorarioPorMes(anyLong(), anyInt(), anyInt())).thenReturn(List.of(horario));

        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        PagosModel resultado = service.save(pago);
        assertEquals(540000.0, resultado.getSueldoTotal());
    }

    @Test
    void guardarPagoSinSueldoBase() {

        PagosModel pago = new PagosModel();
        pago.setIdUsuario(1L);

        assertThrows(IllegalArgumentException.class, () -> service.save(pago));
    }

    @Test
    void actualizarPago() {
        PagosModel existente = new PagosModel();
        existente.setIdSueldo(1L);
        existente.setIdUsuario(1L);
        existente.setSueldoBase(400000.0);
        existente.setBonos(0.0);
        existente.setFechaPago(LocalDate.of(2026, 6, 1));

        PagosModel nuevo = new PagosModel();
        nuevo.setIdUsuario(1L);
        nuevo.setSueldoBase(500000.0);
        nuevo.setBonos(50000.0);
        nuevo.setFechaPago(LocalDate.of(2026, 6, 1));

        HorarioDTO horario = new HorarioDTO();
        horario.setHoraEntrada(LocalTime.of(8, 0));
        horario.setHoraSalida(LocalTime.of(16, 0));

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(horarioClient.obtenerHorarioPorMes(anyLong(), anyInt(), anyInt())).thenReturn(List.of(horario));

        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        PagosModel resultado = service.update(1L, nuevo);
        assertEquals(590000.0, resultado.getSueldoTotal());
        verify(repository).save(any());
    }

    @Test
    void actualizarPagoNoExistente() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.update(1L, new PagosModel()));
    }

    @Test
    void fallbackFindAll() {
        assertTrue(service.findAllFallback(new Exception()).isEmpty());
    }

    @Test
    void fallbackFindById() {
        assertTrue(service.findByIdFallback(1L, new Exception()).isEmpty());
    }

    @Test
    void fallbackSave() {
        assertThrows(RuntimeException.class,
                () -> service.saveFallback(new PagosModel(), new Exception()));
    }

    @Test
    void fallbackUpdate() {
        assertThrows(RuntimeException.class,
                () -> service.updateFallback(1L, new PagosModel(), new Exception()));
    }

    @Test
    void fallbackDelete() {
        assertThrows(RuntimeException.class,
                () -> service.deleteByIdFallback(1L, new Exception()));
    }

    @Test
    void guardarPagoConBonos() {
        PagosModel pago = new PagosModel();
        pago.setIdUsuario(1L);
        pago.setSueldoBase(500000.0);
        pago.setBonos(100000.0);
        pago.setFechaPago(LocalDate.of(2026, 6, 1));

        HorarioDTO horario = new HorarioDTO();
        horario.setHoraEntrada(LocalTime.of(8, 0));
        horario.setHoraSalida(LocalTime.of(16, 0));

        when(horarioClient.obtenerHorarioPorMes(anyLong(), anyInt(), anyInt())).thenReturn(List.of(horario));

        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        PagosModel resultado = service.save(pago);
        assertEquals(640000.0, resultado.getSueldoTotal());
    }

    @Test
    void guardarPagoEnviarEventoRabbit() {
        PagosModel pago = new PagosModel();
        pago.setIdUsuario(1L);
        pago.setSueldoBase(500000.0);
        pago.setBonos(0.0);
        pago.setFechaPago(LocalDate.of(2026, 6, 1));

        when(horarioClient.obtenerHorarioPorMes(anyLong(), anyInt(), anyInt())).thenReturn(List.of());

        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        service.save(pago);

        verify(rabbitTemplate).convertAndSend(
                eq("duocquin.exchange"),
                eq("pago.generado"),
                ArgumentMatchers.<Object>any()
        );
    }

    @Test
void saveCubrirErrorEnRabbitTemplate() {

    PagosModel pago = new PagosModel();
    pago.setIdUsuario(1L);
    pago.setSueldoBase(500000.0);
    pago.setBonos(0.0);
    pago.setFechaPago(LocalDate.of(2026, 6, 1));

    when(horarioClient.obtenerHorarioPorMes(anyLong(), anyInt(), anyInt())).thenReturn(List.of());

    doThrow(new RuntimeException("error rabbit"))
            .when(rabbitTemplate)
            .convertAndSend(anyString(), anyString(), ArgumentMatchers.<Object>any());

    when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

    PagosModel resultado = service.save(pago);
    assertNotNull(resultado);
}

    @Test
    void saveUsuarioNullDebeRetornarHorasCero() {

        PagosModel pago = new PagosModel();
        pago.setIdUsuario(null);
        pago.setSueldoBase(500000.0);
        pago.setBonos(0.0);
        pago.setFechaPago(LocalDate.of(2026, 6, 1));

        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        PagosModel resultado = service.save(pago);

        assertEquals(500000.0, resultado.getSueldoTotal());
    }

    @Test
    void guardarPagoConFechaNullDebeCalcularSoloBase() {
        PagosModel pago = new PagosModel();
        pago.setIdUsuario(1L);
        pago.setSueldoBase(500000.0);
        pago.setBonos(0.0);
        pago.setFechaPago(null);

        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        PagosModel resultado = service.save(pago);

        assertEquals(500000.0, resultado.getSueldoTotal());
    }

    @Test
    void guardarPagoConTurnoNocturno() {

        PagosModel pago = new PagosModel();
        pago.setIdUsuario(1L);
        pago.setSueldoBase(500000.0);
        pago.setFechaPago(LocalDate.of(2026, 6, 1));

        HorarioDTO horario = new HorarioDTO();
        horario.setHoraEntrada(LocalTime.of(22, 0));
        horario.setHoraSalida(LocalTime.of(2, 0));

        when(horarioClient.obtenerHorarioPorMes(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(horario));

        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        PagosModel resultado = service.save(pago);

        assertTrue(resultado.getSueldoTotal() > 500000.0);
    }

    @Test
    void guardarPagoDebeManejarErrorEnHorarioClient() {
        PagosModel pago = new PagosModel();
        pago.setIdUsuario(1L);
        pago.setSueldoBase(500000.0);
        pago.setBonos(0.0);
        pago.setFechaPago(LocalDate.of(2026, 6, 1));

        when(horarioClient.obtenerHorarioPorMes(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of());

        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        PagosModel resultado = service.save(pago);

        assertNotNull(resultado);
    }


    
}