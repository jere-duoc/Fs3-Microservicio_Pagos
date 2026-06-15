package DuocQuin.utilsTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import DuocQuin.Pagos.utils.EncryptedDoubleConverter;

@ExtendWith(MockitoExtension.class)
class EncryptedTest {

    @Mock
    private StringEncryptor encryptor;

    @InjectMocks
    private EncryptedDoubleConverter converter;

    @Test
    void convertirNullABaseDatos() {

        String resultado =
                converter.convertToDatabaseColumn(null);

        assertNull(resultado);
    }

    @Test
    void convertirDoubleABaseDatos() {

        when(encryptor.encrypt("500000.0"))
                .thenReturn("ENCRYPTED");

        String resultado =
                converter.convertToDatabaseColumn(500000.0);

        assertEquals("ENCRYPTED", resultado);
    }

    @Test
    void convertirNullAEntidad() {

        Double resultado =
                converter.convertToEntityAttribute(null);

        assertNull(resultado);
    }

    @Test
    void convertirVacioAEntidad() {

        Double resultado =
                converter.convertToEntityAttribute("");

        assertNull(resultado);
    }

    @Test
    void desencriptarCorrectamente() {

        when(encryptor.decrypt("ABC"))
                .thenReturn("500000.0");

        Double resultado =
                converter.convertToEntityAttribute("ABC");

        assertEquals(500000.0, resultado);
    }

    @Test
    void usarDatoPlanoSiFallaDesencriptacion() {

        when(encryptor.decrypt("500000.0"))
                .thenThrow(new RuntimeException());

        Double resultado =
                converter.convertToEntityAttribute("500000.0");

        assertEquals(500000.0, resultado);
    }
}