# MiniShop REST API

Laboratorios 12 y 13 de Construcción y Pruebas de Software. Java 17,
Spring Boot 3.2.12, Spring Web, Spring Data JPA, H2, Lombok y Maven.

[![CI](https://github.com/yuriphacsi/minishop/actions/workflows/ci.yml/badge.svg)](https://github.com/yuriphacsi/minishop/actions/workflows/ci.yml)

Repositorio: https://github.com/yuriphacsi/minishop
El estado del badge será válido al publicar el workflow y completar su primera ejecución.

## Ejecutar y verificar

En este equipo Maven utiliza Java 26 de manera predeterminada. Seleccionar JDK 17:

```powershell
cd C:\minishop
$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-17.0.21.9-hotspot'
mvn clean verify
mvn spring-boot:run
```

En otro equipo, ajustar `JAVA_HOME` a la instalación de JDK 17.
La primera compilación descarga dependencias de Maven Central.

| Comando | Alcance |
| --- | --- |
| `mvn compile` | Compila producción |
| `mvn test` | 3 pruebas unitarias, Surefire, sin contexto Spring |
| `mvn clean verify` | 3 unitarias + empaquetado + 13 de integración, Failsafe |
| `mvn verify -DskipUTs=true` | Empaquetado + 13 de integración; omite las unitarias |
| `mvn verify -DskipUTs=true -Dit.test=ProductRepositoryIT` | Solo la clase de integración indicada |

Las clases `*Test` son unitarias y las `*IT` son las del laboratorio 12 renombradas.
`docs/LAB12.md` conserva los nombres y comandos históricos de aquella práctica.

## API

| Método | Ruta | Resultado |
| --- | --- | --- |
| POST | `/api/products` | 201 y producto con ID |
| GET | `/api/products` | 200 y lista de productos |
| GET | `/api/products/{id}` | 200 y producto; 500 si no existe, según el lab 12 |

```powershell
Invoke-RestMethod -Method Post -Uri 'http://localhost:8080/api/products' `
  -ContentType 'application/json' `
  -Body '{"name":"Laptop Lenovo","price":2500.0,"stock":10}'
Invoke-RestMethod -Uri 'http://localhost:8080/api/products'
```

H2 principal: `jdbc:h2:mem:minishopdb`; H2 de pruebas: `jdbc:h2:mem:testdb`.
Los datos son temporales. Consola: `http://localhost:8080/h2-console`,
usuario `sa`, contraseña vacía. El perfil `test` deshabilita la consola.

## Pipeline

`.github/workflows/ci.yml` se activa con push a `main`, PR hacia `main` y etiquetas
`v*`. **Build y pruebas** tiene siete pasos: checkout, Java 17, compilación,
unitarias, integración y empaquetado, reportes y JAR. Los reportes se conservan
incluso si falla una prueba. El JAR solo se sube cuando el build pasa.

**Publicar versión** depende del build y solo corre con etiquetas `v*`.
Descarga el mismo JAR validado y lo publica con notas automáticas, sin recompilar.
`contents: write` se limita a este job. Usa `github.token`, sin tokens personales.

Se usaron las versiones documentadas de las acciones oficiales: checkout v7,
setup-java v6, upload-artifact v7 y download-artifact v8. Surefire y Failsafe quedan
fijados en 3.1.2. `skipUTs` afecta solo a Surefire.

## Entrega

`docs/LAB13.md` contiene el proceso, reflexiones y los pasos que requieren GitHub.
Los logs están en `evidencias/lab13/` (fuera de Git); el informe en `output/pdf/`.
El código local no demuestra la protección de ramas, el bloqueo de un PR ni la
publicación de una Release: se requieren enlaces y capturas reales del destino.

## Referencias

- [Checkout](https://github.com/actions/checkout)
- [Java](https://github.com/actions/setup-java)
- [Subir artefactos](https://github.com/actions/upload-artifact)
- [Descargar artefactos](https://github.com/actions/download-artifact)
- [Failsafe](https://maven.apache.org/surefire/maven-failsafe-plugin/)
