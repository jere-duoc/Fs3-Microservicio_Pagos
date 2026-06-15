package DuocQuin.Pagos.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.stereotype.Component;

/**
 * Converter JPA que cifra/descifra valores Double en la base de datos.
 * Serializa el Double a String antes de cifrar, y parsea de vuelta al leer.
 * Soluicona el error 500 causado por aplicar EncryptedStringConverter
 * (que es AttributeConverter<String,String>) directamente sobre campos Double.
 */
@Component
@Converter
public class EncryptedDoubleConverter implements AttributeConverter<Double, String> {

    private final StringEncryptor encryptor;

    public EncryptedDoubleConverter(StringEncryptor encryptor) {
        this.encryptor = encryptor;
    }

    @Override
    public String convertToDatabaseColumn(Double attribute) {
        if (attribute == null) {
            return null;
        }
        return encryptor.encrypt(attribute.toString());
    }

    @Override
    public Double convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        try {
            String decrypted = encryptor.decrypt(dbData);
            return Double.parseDouble(decrypted);
        } catch (Exception e) {
            // Dato legado no cifrado: intentar parsear directamente
            try {
                return Double.parseDouble(dbData);
            } catch (NumberFormatException nfe) {
                return null;
            }
        }
    }
}
