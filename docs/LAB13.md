# Laboratorio 13 - Integración Continua con GitHub Actions

## Estado

Repositorio: https://github.com/yuriphacsi/minishop. El proyecto tiene
Surefire, Failsafe, 3 pruebas unitarias y el workflow CI/CD. Las 13 pruebas del
laboratorio 12 se conservaron y renombraron a `ProductRepositoryIT`,
`ProductServiceIT` y `ProductControllerIT`.

El primer pipeline pasó en GitHub y se creó la regla `proteger-main` activa.
El [PR #1](https://github.com/yuriphacsi/minishop/pull/1) demostró el fallo de precio
cero, quedó bloqueado y se fusionó tras corregirlo. El informe PDF reúne las
capturas y registros, incluyendo la refactorización y publicación de versión.

## Secuencia en GitHub

1. Confirmar destino y permisos. Si contiene otro proyecto, integrar MiniShop sin
   sobrescribirlo. Publicar en `main`, esperar al primer workflow verde y añadir
   al README el badge con el propietario y repositorio confirmados.
2. Crear `proteger-main` en Settings → Rules → Rulesets, estado Active, sobre la
   rama predeterminada. Exigir PR con 0 aprobaciones y check `Build y pruebas`.
   Comprobar que la cuenta y visibilidad permiten aplicar esa regla.
3. En `feature/precio-minimo`, cambiar únicamente `product.getPrice() <= 0` por
   `product.getPrice() < 0`. Abrir PR y esperar al fallo de
   `ProductServiceTest.save_precioCero_lanzaExcepcion`. Capturar PR bloqueado
   y mensaje del paso 4. No fusionar el cambio defectuoso.
4. Restaurar `<= 0` en la misma rama, esperar el check verde y fusionar el PR.
5. En `refactor/validacion-producto`, extraer las dos validaciones a
   `validarProducto(Product product)`, sin cambiar pruebas. Ejecutar
   `mvn verify`, abrir PR y fusionarlo después del check verde.
6. Sobre `main` actualizada, crear la etiqueta anotada `v1.0.0` si no existe y
   subirla. Esperar ambos jobs verdes y comprobar la Release con JAR.
   Si esa versión ya existe, acordar otra; no reutilizarla.

No habilitar bypass de la protección para fusionar un PR rojo ni ampliar permisos
globales de Actions: el workflow limita la escritura al job release.

## Evidencias, en orden del enunciado

1. Enlace al repositorio y README con badge verde.
2. POM, ProductServiceTest y resultado local de `mvn clean verify`.
3. Workflow completo `.github/workflows/ci.yml`.
4. Primera ejecución de GitHub en verde, siete pasos.
5. Ruleset `proteger-main` activo.
6. PR bloqueado y log de la prueba que falla.
7. El mismo PR corregido y fusionado.
8. PR de refactorización fusionado en verde.
9. Ejecución de etiqueta con ambos jobs verdes y Release con JAR adjunto.
10. Reflexiones siguientes, respaldadas por las evidencias obtenidas.

## Reflexiones

**¿Por qué las unitarias antes que las de integración?**
Las unitarias revisan lógica aislada sin iniciar Spring ni H2 y detectan errores
con menor coste. Surefire las ejecuta en `test`; Failsafe ejecuta las `*IT` en
`integration-test` y comprueba su resultado en `verify`, después del empaquetado.
La separación identifica la capa que falla y permite omitir solo las unitarias
ya ejecutadas mediante `skipUTs`.

**¿Dónde se detecta el error y qué ocurre sin proteger main?**
Al aceptar precio cero con `< 0`, falla
`ProductServiceTest.save_precioCero_lanzaExcepcion`: se esperaba
IllegalArgumentException y no se lanzó. En el workflow corresponde al paso 4.
La ejecución se detuvo antes de integración; los reportes se guardaron por
`if: always()`. Una regla activa que exija ese check impide fusionar el PR rojo.
Sin protección, alguien con permisos podría fusionarlo o enviar el error directo
a main. El PR #1 conserva el historial del check rojo y del check verde posterior.

**¿Por qué es segura la refactorización sin probarla a mano?**
Extraer las validaciones conserva condiciones, mensajes y orden. Las mismas
pruebas revisan precio, stock, persistencia y HTTP antes y después. Un resultado
verde reduce el riesgo en los casos cubiertos, pero no demuestra ausencia de
todos los errores posibles.

**¿Qué diferencia hay entre integración y entrega continuas?**
`build-and-test` valida cambios con compilación, unitarias, integración y JAR.
`release` entrega una versión descargable cuando se crea una etiqueta `v*` y el
build pasa. Publicar el JAR no despliega la API en un servidor de producción.

## Ajustes respecto a la guía

- Acciones oficiales actualizadas según sus README.
- Release reutiliza el artefacto validado, evitando compilar de nuevo sin tests.
- Unitarias verifican mensajes, repositorio sin interacciones ante errores y
  producto devuelto en el caso válido.
- `failIfNoTests` detecta una suite que desaparezca por mala configuración.
- Evidencias, ZIP y archivos de trabajo quedan fuera del historial del código.
