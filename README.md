# Microservicio de Pagos - Hospital DuocQuin 💰

Encargado del procesamiento financiero de las remuneraciones del personal hospitalario.

## 🛠️ Tecnologías
- **Java 17**
- **Spring Boot 3.x**
- **MySQL**
- **Maven**
- **RestClient/WebClient** (para comunicación inter-microservicios)

## 📋 Funcionalidades
- **Emisión de Pagos**: Creación de liquidaciones de sueldo para funcionarios.
- **Cálculo de Sueldo Total**: Integración con el microservicio de Horarios para calcular horas extra automáticamente.
- **Historial Financiero**: Registro de bonos, sueldos base y descuentos por periodos.

## ⚙️ Configuración y Ejecución
1. Configurar la base de datos MySQL.
2. Actualizar `src/main/resources/application.properties`.
3. Ejecutar el servicio:
```bash
./mvnw spring-boot:run
```
El servicio estará disponible en `http://localhost:8084`.

## 📡 API Endpoints Principales
- `GET /api/sueldos`: Historial general de pagos.
- `POST /api/sueldos`: Generar un nuevo pago para un funcionario.
- `GET /api/sueldos/usuario/{id}`: Consultar liquidaciones personales.
