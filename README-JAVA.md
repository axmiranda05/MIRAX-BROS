# MIRAX BROS — Ultimate Adventure (Java)

Proyecto Java 17 + Maven + JavaFX Canvas. La versión Python y su README se conservan sin cambios.

## Ejecutar desde IntelliJ IDEA
1. Abrir esta carpeta MIRAX_BROS e importar pom.xml como proyecto Maven.
2. Seleccionar JDK 17 en Project SDK y Maven Runner.
3. Ejecutar la configuración Maven `MIRAX BROS` incluida en `.run/`, o el objetivo `clean javafx:run` desde el panel Maven.
4. Alternativa terminal: `mvn clean javafx:run` o `.\mvnw.cmd javafx:run`.
5. Para una configuración Application, clase principal `com.mirax.Launcher` y classpath del módulo Maven. Se recomienda el objetivo Maven.

Launcher no hereda Application; el plugin configura el runtime JavaFX. Un JAR simple sin dependencias no es ejecutable mediante java -jar.

## Controles
A/D o flechas: moverse; Shift: correr; Espacio: salto y doble salto (soltar entre pulsaciones); J: ataque; K: proyectil; L: deslizar; F: escudo; Q: impulso; T: teleportación con colisiones; E: activar portal; Esc: pausa; R: reiniciar. En pausa: M guarda y vuelve al menú. El checkpoint restaura salud. Se puede saltar sobre enemigos. El portal final requiere derrotar al jefe.

## Alcance de esta entrega
Menú con ratón/teclado, arte Canvas original, parallax, armadura animada, física con subpasos, cámara, partículas, combate, monedas, salud, energía, tres vidas, checkpoint, derrota/victoria y guardado JSON atómico en saves/mirax-java.json.

Campaña inicial de 15 sectores en 5 paletas temáticas. Comparten diseño base: todavía no son 15 niveles diseñados individualmente. Siete tipos de enemigos con estados, jefe final y selección progresiva por ratón. Continuar recupera nivel y puntuación, no posición exacta ni enemigos derrotados. Nueva partida conserva sectores desbloqueados. R reinicia puntuación, monedas y vidas del nivel. Deslizar acelera sin reducir el collider.

Pendientes: audio, plataformas móviles/temporales, meteoritos, rutas secretas, experiencia/mejoras y pulido de mundos. Los poderes están disponibles desde el inicio. El objetivo de 60 FPS requiere medición en el equipo de destino.

## Verificación
`mvn test`: pruebas de física, doble salto, recorrido del terreno, datos de campaña, daño, IA y guardado/corrupción. Las pruebas Python pertenecen a la versión anterior y no validan la implementación Java.

`mvn javafx:run -Djavafx.args=--smoke-test`: abre una ventana, inicia el nivel y cierra tras cuatro segundos, mostrando MIRAX_SMOKE_OK y frames renderizados. Además verifica por simulación el doble salto, pausa, combate, monedas y victoria del primer nivel; guarda una captura en target/smoke-level.png. No sustituye una prueba manual completa. Ver VERIFICACION-JAVA.md para los resultados ejecutados.
## Aventura educativa
Selección de primaria/secundaria, grados y cursos, preguntas, recuperación, estrellas, recompensas y progreso individual integrados. Consulta EDUCACION.md para controles, cobertura real del banco y pruebas ejecutadas. La ampliación completa de preguntas sigue pendiente.

