# Verificación de la entrega Java

Fecha: 25 de octubre de 2026. Carpeta comprobada: D:\UNAJ EPISS\CRUD\crud-persona-spring-BACKUP\MIRAX_BROS.

Se conservaron main.py, config.py, game/, ui/, tests/, assets/, README.md y la configuración IntelliJ existente. Se añadieron fuentes Java en src/, pom.xml, Maven Wrapper, documentación y una configuración Maven en .run/.

Java utilizado: 17.0.12. JavaFX: 21.0.4. Maven Wrapper: 3.9.16. No se instaló Maven globalmente; se descargaron, con autorización, dependencias de pruebas en la caché Maven.

## Resultados ejecutados
- Compilación limpia y pruebas: BUILD SUCCESS.
- JUnit: 9 pruebas, cero fallos, cero errores, cero omitidas.
- Arranque JavaFX mediante javafx-maven-plugin y Launcher: BUILD SUCCESS, sin error de componentes runtime.
- Verificación adicional mediante --smoke-test: doble salto, aterrizaje, pausa, posición congelada en pausa, reanudación, desplazamiento, ataques, proyectiles, monedas y activación del portal por la ruta real de entrada/simulación.
- Resultado de recorrido: MIRAX_SMOKE_OK frames=250 victoria=true coins=24 score=2000 lives=3.
- Captura del nivel revisada visualmente: target/smoke-level.png.

El contador de frames corresponde al renderizado de la ventana durante aproximadamente cuatro segundos; no es un benchmark estable ni una garantía de 60 FPS. El recorrido adicional usa pasos de simulación acelerados.

## Reproducir
En PowerShell, desde esta carpeta:

    .\mvnw.cmd -B clean test
    .\mvnw.cmd -B javafx:run "-Djavafx.args=--smoke-test"

La prueba abre ventana y se cierra sola. Su guardado se dirige a target/smoke-save.json y no al guardado normal.

Para jugar:

    .\mvnw.cmd javafx:run

En IntelliJ, importar pom.xml, seleccionar JDK 17 y ejecutar la configuración Maven MIRAX BROS en .run/. Si no aparece, usar el panel Maven y ejecutar clean javafx:run. La configuración se escribió, pero no se operó la interfaz de IntelliJ durante esta verificación.

## Límites de la verificación
No se realizó una sesión manual exhaustiva con teclado/ratón ni se recorrieron los quince sectores con combate. El test de terreno no incluye enemigos; la verificación integrada sí incluye enemigos del primer nivel. No se ejecutaron las pruebas Python porque pertenecen a la implementación anterior.

Esta entrega termina el primer nivel y añade la base de campaña; no completa todavía todas las fases del diseño original. Pendientes: audio, diseño individual de sectores, plataformas móviles/temporales, experiencia/mejoras y pulido de mecánicas avanzadas. Ver README-JAVA.md.