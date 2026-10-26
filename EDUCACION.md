# MIRAX BROS — Aventura escolar

Implementación dentro del proyecto existente, con Java 17, Maven y JavaFX Canvas.
Se conserva el menú, arte, movimiento, combate, enemigos y los 15 sectores de la versión Java.

## Uso

1. NUEVA PARTIDA → escribe tu nombre → ENTER.
2. Elige primaria o secundaria con las flechas izquierda/derecha o el ratón.
3. Elige el grado y después el curso. ENTER confirma; ESC vuelve.
4. Responde con las teclas 1–4, teclado numérico o ratón. ENTER continúa después de leer la explicación.
5. CONTINUAR recupera el último perfil. Volver a elegir el mismo nombre, grado y curso abre su progreso independiente.

Los controles originales permanecen. E también interactúa con el cofre y la puerta de energía opcionales.
El cofre concede 50 monedas adicionales. La puerta abierta concede un escudo de 10 segundos.
C alterna el aspecto MIRAX ESMERALDA cuando se desbloquea; conserva las animaciones del personaje original.

## Desafíos implementados

| Evento | Cantidad | Dificultad | Condición |
| --- | ---: | --- | --- |
| Cofre | 1 | Intermedia | Acertar |
| Puerta de energía | 1 | Intermedia | Acertar |
| Perder todas las vidas | 1 por intento | Intermedia | Acertar para recuperar una vida |
| Activar portal | 2 | Intermedia-alta | Acertar ambas |
| Terminar sector | 3 | Intermedia-alta | Dos aciertos para avanzar |
| Jefe final existente | 3 | Desafío final | Dos aciertos |
| Pasar al siguiente mundo | 5 | Desafío final | Cinco aciertos y evaluación del sector |

El portal requiere derrotar a los enemigos jefe del sector antes de evaluar el desafío educativo.
Después de cada desafío especial del portal, vuelve a pulsar E para continuar la secuencia.
La simulación se detiene durante preguntas, explicaciones y resultados.
Cada pregunta tiene 90 segundos. Agotar el tiempo cuenta como una respuesta incorrecta.

Al perder todas las vidas hay como máximo tres oportunidades por recorrido del sector, contando también las recuperaciones exitosas.
Una respuesta correcta restaura una vida y devuelve al último punto de control de ese recorrido.
Cada respuesta correcta da 50 puntos y 10 monedas. Una incorrecta muestra respuesta y explicación antes del siguiente intento.
Las preguntas no se repiten durante el mismo recorrido. Si se agotan las de la dificultad exacta, aparece un aviso y se vuelve al menú; nunca se sustituye el grado.
Reintentar físicamente el sector inicia otro recorrido y reinicia su lista de preguntas e intentos; las estadísticas acumuladas permanecen.

En la evaluación del sector se guardan de cero a tres estrellas; se conserva el mejor resultado.
Con cero o un acierto se puede repetir el sector. No se desbloquea el siguiente.
El último sector termina la campaña, sin crear sectores adicionales.

## Progreso y recompensas

`saves/education/` contiene un JSON por combinación de nombre, nivel educativo, grado y curso.
Los nombres de archivo usan identificadores derivados del perfil, sin usar el nombre del jugador como ruta.
`current.json` permite continuar el último perfil. Los archivos se reemplazan mediante escritura temporal y movimiento atómico cuando el sistema lo permite.
El guardado original `saves/mirax-java.json` se conserva separado; no puede desbloquear sectores del perfil educativo.
CONTINUAR educativo reanuda desde el inicio del sector guardado; no guarda posiciones y enemigos a mitad de una partida.

Se guardan nombre, grado, curso, sector actual, sectores desbloqueados y completados, respuestas totales, correctas e incorrectas, monedas, puntos, estrellas por sector, insignias, poderes, mundos y personaje activo.
La precisión se calcula a partir de los contadores y se muestra junto con las recompensas al terminar cada desafío.
Las monedas y puntos del juego también se acumulan en el perfil.

Diez aciertos acumulados desbloquean ENERGÍA ESCOLAR: escudo inicial de ocho segundos al cargar un sector.
También conceden GENIO MATEMÁTICO, MAESTRO DE HISTORIA, EXPERTO EN CIENCIAS, CAMPEÓN DE COMUNICACIÓN o EXPLORADOR DEL CONOCIMIENTO según el curso.
Completar tres sectores distintos concede EXPLORADOR DEL CONOCIMIENTO y el aspecto MIRAX ESMERALDA.

## Cobertura real del banco: fase 5 incompleta

Hay **440 preguntas**, repartidas en **22 de las 132 combinaciones** de grado y curso.
Cada combinación disponible tiene 20 preguntas: cinco intermedias, cinco intermedias-altas y diez de desafío final.

| Curso | Primaria | Secundaria |
| --- | --- | --- |
| Matemáticas | 1.º a 6.º: 20 por grado | 1.º a 5.º: 20 por grado |
| Los otros once cursos | 1.º: 20 por curso; 2.º a 6.º pendientes | Todos los grados pendientes |

**Faltan 2200 preguntas** para alcanzar el mínimo solicitado de 2640.
La pantalla de cursos indica `BANCO INCOMPLETO` y bloquea las combinaciones sin preguntas suficientes.
Las pruebas verifican esta cobertura; no se afirma que los 132 bancos estén terminados.
El banco se organiza mediante los campos de nivel, grado, curso, tema y dificultad en `src/main/resources/education/questions.json`.
Cada pregunta tiene identificador, cuatro alternativas diferentes, índice de respuesta correcto desde cero y explicación.

Para reconstruir el JSON desde las fuentes de autoría, ejecutar desde la raíz:

```powershell
./tools/Build-QuestionBank.ps1
```

Los enunciados de Matemáticas son ejercicios originales con procedimientos calculados en el generador, organizados en cinco temas por grado y cuatro variantes por tema.
Los otros cursos contienen enunciados originales de iniciación en archivos PSV de autoría. El juego solo carga el JSON empaquetado.
No se han copiado preguntas de libros del MINEDU. No se presenta este banco como una certificación curricular oficial.

## Referencia curricular y límites pedagógicos

Se verificó el marco del CNEB y la existencia de programas con desempeños por grado en las publicaciones oficiales del MINEDU:

- [Programas curriculares de Educación Básica Regular](https://www.gob.pe/90158).
- [Currículo Nacional de la Educación Básica](https://repositorio.minedu.gob.pe/handle/20.500.12799/4551).
- [Programa de primaria: desempeños por grado](https://www.minedu.gob.pe/curriculo/pdf/03062016-programa-nivel-primaria-ebr.pdf).
- [Programa de secundaria](https://repositorio.minedu.gob.pe/handle/20.500.12799/4550).

Los doce cursos son categorías del videojuego, no una afirmación de que el CNEB tenga doce áreas independientes con esos nombres.

| Categorías del juego | Referencia de organización |
| --- | --- |
| Matemáticas | Cantidad; regularidad, equivalencia y cambio; forma y localización; gestión de datos |
| Comunicación | Lectura, escritura y comunicación oral |
| Química, Física y Biología | Ciencia y Tecnología; en primaria, materiales, observación, seres vivos y entorno, sin química universitaria |
| Historia, Geografía y Cívica | Personal Social en primaria; Ciencias Sociales y Desarrollo Personal, Ciudadanía y Cívica en secundaria |
| Finanzas | Uso responsable de recursos, necesidades y decisiones económicas |
| Computación | Competencia transversal vinculada a entornos virtuales y actividades introductorias |
| Arte | Arte y Cultura: apreciación y creación |
| Inglés | Vocabulario y comunicación inicial; en primero de primaria se ofrece como enriquecimiento |

La distribución exacta de cada pregunta, su dificultad y su correspondencia con desempeños individuales todavía requieren revisión docente.
La consulta del marco curricular no equivale a haber verificado pedagógicamente las 440 preguntas contra todos los desempeños oficiales.
La ampliación pendiente debe incorporar esa revisión y diversificar temas antes de afirmar cobertura curricular completa.

## Compilación y verificación

```powershell
./mvnw.cmd test
./mvnw.cmd javafx:run
./mvnw.cmd javafx:run '-Djavafx.args=--smoke-test'
```

El wrapper de Windows se corrigió para directorios Maven sin enlace simbólico, cuyo atributo Target puede ser nulo.
En este entorno se utilizó JDK 17.0.12 y la caché local con `-Dmaven.repo.local=C:/Users/MIRANDA/.m2/repository`.
Jackson es la dependencia Maven para leer y guardar JSON; JavaFX se mantiene en la versión existente del proyecto.

Resultado ejecutado: **19 pruebas JUnit aprobadas**, diez educativas y nueve originales del juego.
La prueba gráfica abre JavaFX brevemente, recorre el terreno con combate y monedas, selecciona primaria/secundaria y cursos, simula muertes, responde incorrectamente tres veces, verifica recuperación desde checkpoint y su límite incluso con aciertos, prueba uno y dos aciertos al finalizar, comprueba guardado, cofre, puerta, jefe, mundos y recompensas.
Los saltos directos a sectores avanzados solo se usan dentro del método de verificación, protegido por la propiedad de prueba.
El modo de humo escribe exclusivamente en `target/smoke-save.json` y `target/smoke-education/`.
Genera capturas `target/smoke-*.png`. No es una evaluación pedagógica con estudiantes ni una medición de rendimiento.

## Estado por fase

- Fase 1: selección y perfiles implementados, compilados y probados.
- Fase 2: banco JSON, validación y pantalla educativa implementados; contenido parcial.
- Fase 3: muerte, recuperación, evaluación de victoria y bloqueo de avance implementados y probados en JavaFX.
- Fase 4: recompensas, estadísticas y guardado implementados y probados.
- Fase 5: pruebas técnicas ejecutadas; ampliación a 2640 preguntas y revisión docente pendientes.

La música no existía en la implementación Java recibida; permanece pendiente junto con los pendientes previos descritos en README-JAVA.md.
