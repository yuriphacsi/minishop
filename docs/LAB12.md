# MiniShop - Laboratorio 12

API REST del laboratorio de Pruebas de Integración, implementada con Java 17,
Spring Boot 3.2.12, Maven, Spring Web, Spring Data JPA, H2 y Lombok.

## Ejecutar

Configurar Maven y el IDE con **JDK 17**. En este equipo Maven usa Java 26 por
defecto; para esta práctica se verificó con el JDK 17 instalado:

```powershell
cd C:\minishop
$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-17.0.21.9-hotspot'
mvn compile
mvn test
mvn spring-boot:run
```

En otro equipo, ajustar `JAVA_HOME` a su instalación de JDK 17. La primera ejecución
requiere conexión a Maven Central. Detener la aplicación con Ctrl+C.

Para una sola clase:

```powershell
mvn test '-Dtest=ProductRepositoryIntegrationTest'
mvn test '-Dsurefire.useFile=false'
```

## API

| Método | Ruta | Resultado |
| --- | --- | --- |
| POST | `/api/products` | Registra un producto y devuelve 201 con ID |
| GET | `/api/products` | Devuelve 200 y la lista de productos |
| GET | `/api/products/{id}` | Devuelve 200 y el producto solicitado |

Ejemplo de registro en PowerShell, con la aplicación iniciada:

```powershell
Invoke-RestMethod -Method Post -Uri 'http://localhost:8080/api/products' `
  -ContentType 'application/json' `
  -Body '{"name":"Laptop Lenovo","price":2500.0,"stock":10}'
Invoke-RestMethod -Uri 'http://localhost:8080/api/products'
```

La base principal es `jdbc:h2:mem:minishopdb`. Los datos son temporales y se pierden
al detener el proceso. La consola H2 está en `http://localhost:8080/h2-console`,
con usuario `sa`, contraseña vacía y la URL anterior.

## Pruebas y resultados

| Clase | Configuración | Casos | Resultado verificado |
| --- | --- | ---: | --- |
| ProductRepositoryIntegrationTest | @DataJpaTest + H2 | 4 | 4 correctos |
| ProductServiceIntegrationTest | @SpringBootTest + @MockBean | 5 | 5 correctos |
| ProductControllerIntegrationTest | @SpringBootTest + @AutoConfigureMockMvc + H2 | 4 | 4 correctos |
| Total | | 13 | 0 fallos, 0 errores, 0 omitidos |

`mvn compile` y `mvn test` finalizaron con `BUILD SUCCESS`.

Evidencias conservadas en `evidencias/`:

- `mvn-compile.log`: salida real de compilación.
- `mvn-test.log`: salida real de ejecución de las 13 pruebas.
- `surefire/`: reportes XML y TXT originales de Maven.
- `reporte.html`: vista de los resultados basada en los reportes originales.
- `reporte-pruebas.png`: captura de esa vista HTML, con el resumen real de Maven.

Los reportes se generan también en `target/surefire-reports/` al repetir `mvn test`.
Las evidencias conservadas corresponden a la ejecución registrada; no se actualizan
automáticamente al volver a ejecutar los tests.

## Ajustes a la guía

- Se agregó `MinishopApplication`, necesaria para iniciar y descubrir el contexto.
- Se conservó HTTP **500 para ID inexistente**, como exige la guía. Se agregó
  `ProductNotFoundException` y `ApiExceptionHandler` para resolver explícitamente la
  excepción y permitir la aserción de MockMvc. Una API de producción debería usar
  HTTP 404 para este caso. Referencia técnica:
  https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-exceptionhandler.html
- El Repository activa el perfil `test` y conserva su datasource H2 configurado;
  @DataJpaTest revierte cada transacción. Las pruebas del Controller limpian los datos
  antes de cada caso. Las clases se ejecutan secuencialmente; compartir una URL H2
  no garantiza aislamiento si se habilita ejecución paralela sin otros ajustes.
- Las pruebas de persistencia fuerzan `flush` y limpian el contexto JPA antes de leer,
  para verificar los datos en H2. El POST también se comprueba mediante un GET posterior.
- La lista se verifica sin exigir orden, porque `findAll()` no garantiza uno.
- El caso de precio inválido verifica tanto cero como negativo dentro de un solo test,
  manteniendo los 13 casos solicitados.

## Conclusiones

Una prueba unitaria aísla una pieza y permite revisar su lógica rápidamente. Una
prueba de integración verifica la colaboración entre componentes reales y detecta
problemas de persistencia, configuración o contratos entre capas.

@DataJpaTest permite comprobar el repositorio y las consultas contra H2 sin cargar
toda la aplicación. @SpringBootTest carga el contexto completo. En el Service,
@MockBean sustituye el repositorio: estos casos comprueban la lógica y la inyección
de Spring, pero no validan la integración real Service-Repositorio-BD.

Las pruebas del Controller sí recorren MockMvc → Controller → Service → Repository
→ H2 y verifican estados HTTP y JSON. MockMvc no inicia un servidor ni prueba una
conexión de red real. H2 evita depender de infraestructura externa, aunque sus
resultados no sustituyen la validación contra una eventual base de producción.

El código, las tres clases de prueba y las evidencias quedan listos para añadir a un
repositorio y para que un pipeline posterior ejecute `mvn test`.
