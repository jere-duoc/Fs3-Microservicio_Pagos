package DuocQuin.Pagos.model;

import java.time.LocalDate;

import DuocQuin.Pagos.utils.EncryptedDoubleConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sueldos")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PagosModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sueldo")
    private Long idSueldo;

    @Convert(converter = EncryptedDoubleConverter.class)
    @Column(name = "sueldo_base")
    private Double sueldoBase;

    @Convert(converter = EncryptedDoubleConverter.class)
    @Column(name = "bonos")
    private Double bonos;
    
    @Convert(converter = EncryptedDoubleConverter.class)
    @Column(name = "sueldo_total")
    private Double sueldoTotal;

    @Column(name = "fecha_pago")
    private LocalDate fechaPago;

    @Column(name = "id_usuario")
    private Long idUsuario;
}
