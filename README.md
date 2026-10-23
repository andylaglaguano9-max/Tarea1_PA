# 🎮 PixelVault API - The Retro Gaming Engine

Backend de alta fidelidad desarrollado con **Spring Boot 3.3.4**, **Java 17/25**, **Spring Data JPA**, **H2 Database**, **AspectJ (AOP)** y **OpenAPI 3 / Swagger**.

---

## 🕹️ Características de la Arquitectura

1. **Inversión de Control (IoC) e Inyección de Dependencias (DI):**
   - Interfaz `CalculoComisionService` con 2 implementaciones concretas: `ComisionCartuchosService` (5%) y `ComisionConsolasGrialService` (15%).
   - Resolución de dependencias mediante `@Qualifier` en `OrdenService`.
   - Beans de infraestructura definidos con `@Bean` en `VaultAppConfig` (`retroCurrencyFormatter`, `vaultAuditPrefix`, `transactionPrefix`).

2. **Interceptores Web (`HandlerInterceptor`):**
   - `VaultPassSecurityInterceptor`: Valida el encabezado `X-Vault-Pass: RETRO-MASTER-KEY-1989`. Lanza `VaultSecurityException` (HTTP 401) si falta o es inválido.
   - `RetroLatencyAuditInterceptor`: Mide el tiempo de respuesta HTTP en milisegundos.
   - Configuración en `WebMvcConfig` con exclusiones seguras: catálogo público, Swagger UI, OpenAPI y consola H2.

3. **Programación Orientada a Aspectos (Spring AOP):**
   - `ServicePerformanceAspect` (`@Around`): Monitorea la latencia de todos los métodos de servicio.
   - `VaultAuditAspect` (`@AfterReturning`): Registra auditorías de adquisiciones completadas exitosamente.
   - `AntiScalperGrailRuleAspect` (`@Before`): **Regla de Negocio Anti-Scalper**: Bloquea con `AntiScalperViolationException` (HTTP 409) si un coleccionista intenta adquirir más de 2 artículos clasificados como `SANTO_GRIAL` o `MINT_IN_BOX` en una ventana de 24 horas.

4. **Validaciones Declarativas y Validación Personalizada:**
   - 6+ anotaciones estándar de Bean Validation (`@NotBlank`, `@Size`, `@Min`, `@Max`, `@Positive`, `@Email`).
   - Validación personalizada `@ValidRetroSerial` con su validador `RetroSerialValidator` que verifica el formato oficial de cartuchos (`^(NES|SNES|N64|GEN|PS1)-\d{4}-(USA|JPN|EUR)$`).

5. **Manejo Global de Excepciones con `@RestControllerAdvice`:**
   - `GlobalExceptionHandler` estandariza todas las respuestas bajo `ApiErrorResponse` con mapa de errores de campo.

---

## 🚀 Ejecución del Proyecto

### 1. Iniciar el servidor
```powershell
.\mvnw.cmd spring-boot:run
```
El servidor arrancará en `http://localhost:8080`.

### 2. Documentación Swagger / OpenAPI
- Interfaz interactiva: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- JSON de especificación: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

> **Nota:** Para probar endpoints protegidos en Swagger, pulsa el botón **Authorize** e introduce: `RETRO-MASTER-KEY-1989`.

### 3. Consola de Base de Datos H2
- URL de acceso: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
- JDBC URL: `jdbc:h2:mem:pixelvaultdb`
- Usuario: `sa`
- Contraseña: `pixelpassword`

### 4. Ejecución de Pruebas Unitarias
```powershell
.\mvnw.cmd test
```
Ejecuta 21 tests automatizados cubriendo Mockito, validaciones de seriales y la regla de negocio anti-acaparamiento con su aspecto de telemetría.

---

## 📡 Endpoints Principales

| Método | Endpoint | Cabecera `X-Vault-Pass` | Descripción |
| :--- | :--- | :---: | :--- |
| `GET` | `/api/v1/juegos/catalogo-publico` | No | Lista los juegos cargados en el inventario. |
| `GET` | `/api/v1/juegos/{id}` | Sí | Detalle y rareza de un cartucho. |
| `POST` | `/api/v1/juegos` | Sí | Da de alta un nuevo cartucho en la bóveda con validación. |
| `GET` | `/api/v1/coleccionistas` | Sí | Lista de coleccionistas y balances. |
| `GET` | `/api/v1/coleccionistas/{id}/balance` | Sí | Balance disponible en monedas retro (`PX$`). |
| `POST` | `/api/v1/ordenes/checkout` | Sí | Checkout transaccional con activación del Aspecto Anti-Scalper. |
| `GET` | `/api/v1/ordenes/historial/{id}` | Sí | Historial de compras de un coleccionista. |

---

## 🧪 Pruebas con Postman / Insomnia
Se incluye el archivo [PixelVault_Postman_Collection.json](file:///c:/Users/PC-MASTER/7MO%20SEMESTRE/programacion%20avanzada/Ejemplo_exepciones/PixelVault_Postman_Collection.json) en la raíz del proyecto para importar directamente en tu cliente REST.
