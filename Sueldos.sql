
USE sueldos;


CREATE TABLE sueldos (
    id_sueldo INT AUTO_INCREMENT PRIMARY KEY,
    sueldo_base DECIMAL(10,2) NOT NULL,
    bonos DECIMAL(10,2) DEFAULT 0,
    sueldo_total DECIMAL(10,2),
    fecha_pago DATE NOT NULL,
    id_usuario INT
);

