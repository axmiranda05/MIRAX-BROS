# Banco inicial de matemáticas. Ejecutar desde MIRAX_BROS con PowerShell.
# Cada variante conserva grado, tema, procedimiento y cuatro resultados distintos.
$ErrorActionPreference='Stop'
$items=[Collections.Generic.List[object]]::new()
function Add-Q($level,$grade,$topic,$variant,$text,$answer,$explanation) {
 $difficulty=@('INTERMEDIA','INTERMEDIA_ALTA','DESAFIO_FINAL','DESAFIO_FINAL')[$variant-1]
 $values=@([string]$answer,[string]($answer+1),[string]($answer-1),[string]($answer+3))
 $offset=($grade+$variant+$items.Count)%4
 $options=@(0..3 | ForEach-Object { $values[($_+$offset)%4] })
 $correct=[array]::IndexOf($options,[string]$answer)
 $items.Add([ordered]@{id="MAT-$level-$grade-$topic-$variant";statement=$text;alternatives=$options;correctAnswer=$correct;explanation=$explanation;level=$level;grade=$grade;course='MATEMATICAS';topic=$topic;difficulty=$difficulty})
}
foreach($grade in 1..6){foreach($v in 1..4){
 $a=$v+3;$b=$v+1
 switch($grade){
  1 {
   Add-Q PRIMARIA $grade Suma $v "Tienes $a semillas y recibes $b más. ¿Cuántas semillas tienes?" ($a+$b) "Sumamos las dos cantidades: $a + $b = $($a+$b)."
   Add-Q PRIMARIA $grade Resta $v "De $($a+5) lápices, entregas $v. ¿Cuántos quedan?" ($a+5-$v) "Quitamos los lápices entregados: $($a+5) - $v = $($a+5-$v)."
   Add-Q PRIMARIA $grade Secuencia $v "Completa la secuencia: $a, $($a+1), $($a+2), __." ($a+3) 'La regla es aumentar una unidad en cada paso.'
   Add-Q PRIMARIA $grade Comparacion $v "Luz tiene $($a+2) fichas y Ana tiene $a. ¿Cuántas tiene Luz más que Ana?" 2 "Comparamos restando: $($a+2) - $a = 2."
   Add-Q PRIMARIA $grade Problema $v "Hay $a aves. Llegan 2 y luego se va 1. ¿Cuántas aves quedan?" ($a+1) "Primero sumamos 2 y luego restamos 1: $a + 2 - 1 = $($a+1)."
  }
  2 {
   $n=20+3*$v
   Add-Q PRIMARIA $grade Decenas $v "¿Qué número forman $v decenas y 6 unidades?" (10*$v+6) "Cada decena equivale a 10 unidades: $v × 10 + 6 = $(10*$v+6)."
   Add-Q PRIMARIA $grade Suma $v "Una biblioteca tiene $n cuentos y recibe 18 más. ¿Cuántos cuentos tiene ahora?" ($n+18) "Sumamos los cuentos existentes y los nuevos: $n + 18 = $($n+18)."
   Add-Q PRIMARIA $grade Resta $v "De $($n+20) naranjas se venden 17. ¿Cuántas quedan?" ($n+3) "Restamos las vendidas: $($n+20) - 17 = $($n+3)."
   Add-Q PRIMARIA $grade Grupos $v "Hay $($v+2) bolsas con 2 panes en cada una. ¿Cuántos panes hay?" (2*($v+2)) "Sumar 2 tantas veces como bolsas equivale a $($v+2) × 2 = $(2*($v+2))."
   Add-Q PRIMARIA $grade Dinero $v "Pagas con S/ 50 una compra de S/ $n. ¿Cuántos soles recibes de vuelto?" (50-$n) "El vuelto es lo pagado menos el precio: 50 - $n = $(50-$n)."
  }
  3 {
   $n=125+23*$v
   Add-Q PRIMARIA $grade Suma $v "Una escuela reunió $n libros y otra 248. ¿Cuántos reunieron juntas?" ($n+248) "Sumamos unidades, decenas y centenas, reagrupando: $n + 248 = $($n+248)."
   Add-Q PRIMARIA $grade Multiplicacion $v "Hay $a cajas con 6 cuadernos cada una. ¿Cuántos cuadernos hay?" ($a*6) "Multiplicamos cajas por cuadernos: $a × 6 = $($a*6)."
   Add-Q PRIMARIA $grade Division $v "Reparte $($a*4) fichas por igual entre 4 estudiantes. ¿Cuántas recibe cada uno?" $a "Dividimos la cantidad total entre 4: $($a*4) ÷ 4 = $a."
   Add-Q PRIMARIA $grade Perimetro $v "Un cuadrado tiene lados de $a cm. ¿Cuál es su perímetro en cm?" ($a*4) "Sumamos sus cuatro lados iguales: 4 × $a = $($a*4) cm."
   Add-Q PRIMARIA $grade Fraccion $v "Una cinta de $($a*2) cm se divide en dos mitades iguales. ¿Cuántos cm mide cada mitad?" $a "Una mitad es una de dos partes iguales: $($a*2) ÷ 2 = $a cm."
  }
  4 {
   Add-Q PRIMARIA $grade Multiplicacion $v "Para una feria hay $($a+10) cajas de 12 vasos. ¿Cuántos vasos hay?" (($a+10)*12) "Distribuimos: $($a+10) × (10 + 2) = $(($a+10)*12)."
   Add-Q PRIMARIA $grade Division $v "Se reparten $($a*24) hojas entre 12 equipos. ¿Cuántas recibe cada equipo?" ($a*2) "El reparto equitativo se calcula con $($a*24) ÷ 12 = $($a*2)."
   Add-Q PRIMARIA $grade Area $v "Un rectángulo mide $($a+2) cm de largo y $a cm de ancho. ¿Cuál es su área en cm²?" (($a+2)*$a) "Área del rectángulo = largo × ancho = $($a+2) × $a = $(($a+2)*$a) cm²."
   Add-Q PRIMARIA $grade Fracciones $v "De $($a*4) plantas, un cuarto son rosas. ¿Cuántas son rosas?" $a "Un cuarto corresponde a dividir entre 4: $($a*4) ÷ 4 = $a."
   Add-Q PRIMARIA $grade Medidas $v "Una ruta tiene $v km y 250 m. ¿Cuántos metros tiene en total?" ($v*1000+250) "Cada kilómetro tiene 1000 m: $v × 1000 + 250 = $($v*1000+250) m."
  }
  5 {
   Add-Q PRIMARIA $grade Fracciones $v "Una parcela tiene $($a*12) m². Se cultivan 2/3. ¿Cuántos m² se cultivan?" ($a*8) "Primero hallamos un tercio y luego dos: $($a*12) ÷ 3 × 2 = $($a*8)."
   Add-Q PRIMARIA $grade Decimales $v "Cada cuaderno cuesta S/ 2,50. ¿Cuántos soles cuestan $($v*2) cuadernos?" ($v*5) "Multiplicamos precio por cantidad: 2,50 × $($v*2) = $($v*5) soles."
   Add-Q PRIMARIA $grade Area $v "Un rectángulo tiene área $($a*8) cm² y ancho 8 cm. ¿Cuánto mide el largo en cm?" $a "Como área = largo × ancho, largo = $($a*8) ÷ 8 = $a cm."
   Add-Q PRIMARIA $grade Promedio $v "Las cantidades de libros leídos fueron $a, $($a+2) y $($a+4). ¿Cuál es su promedio?" ($a+2) "Sumamos y dividimos entre tres: ($a + $($a+2) + $($a+4)) ÷ 3 = $($a+2)."
   Add-Q PRIMARIA $grade Equivalencia $v "Completa la igualdad: 3 × __ + $v = $($a*3+$v)." $a "Restamos $v a ambos lados y dividimos entre 3: $($a*3) ÷ 3 = $a."
  }
  6 {
   Add-Q PRIMARIA $grade Porcentaje $v "¿Cuánto es el 25 % de $($a*40)?" ($a*10) "25 % equivale a un cuarto: $($a*40) ÷ 4 = $($a*10)."
   Add-Q PRIMARIA $grade Descuento $v "Una mochila cuesta S/ $($a*20) y tiene 10 % de descuento. ¿Cuántos soles se pagan?" ($a*18) "El descuento es $($a*2) soles. Restamos: $($a*20) - $($a*2) = $($a*18)."
   Add-Q PRIMARIA $grade Proporcion $v "Tres cuadernos cuestan S/ 12. ¿Cuántos soles cuestan $a cuadernos al mismo precio?" ($a*4) "Cada cuaderno cuesta 12 ÷ 3 = 4 soles. Entonces $a × 4 = $($a*4)."
   Add-Q PRIMARIA $grade Volumen $v "Una caja rectangular mide $a cm por 3 cm por 2 cm. ¿Cuál es su volumen en cm³?" ($a*6) "Volumen = largo × ancho × alto = $a × 3 × 2 = $($a*6) cm³."
   Add-Q PRIMARIA $grade Fracciones $v "Hay $($a*12) libros. Se presta 1/3 y después 1/4 del total inicial. ¿Cuántos libros quedan?" ($a*5) "Se presta 1/3 + 1/4 = 7/12 del total. Quedan 5/12: $($a*12) × 5/12 = $($a*5)."
  }
 }
}}
foreach($grade in 1..5){foreach($v in 1..4){
 $x=$v+$grade+2;$a=$grade+2;$b=$v+3
 switch($grade){
  1 {
   Add-Q SECUNDARIA $grade Enteros $v "La temperatura era -$b °C y subió $($b+$x) °C. ¿Cuál es la temperatura final en °C?" $x "Sumamos el cambio a la temperatura inicial: -$b + $($b+$x) = $x."
   Add-Q SECUNDARIA $grade Ecuacion $v "Resuelve: 2x + $b = $(2*$x+$b). ¿Cuánto vale x?" $x "Restamos $b y dividimos entre 2: $(2*$x) ÷ 2 = $x."
   Add-Q SECUNDARIA $grade Proporcion $v "Un vehículo recorre 60 km en una hora a velocidad constante. ¿Cuántos km recorre en $v horas?" (60*$v) "La distancia es proporcional al tiempo: 60 × $v = $(60*$v) km."
   Add-Q SECUNDARIA $grade Angulos $v "Dos ángulos de un triángulo miden $($v*10+20)° y 60°. ¿Cuántos grados mide el tercero?" (100-$v*10) "Los ángulos interiores suman 180°: 180 - $($v*10+20) - 60 = $(100-$v*10)°."
   Add-Q SECUNDARIA $grade Estadistica $v "Halla la mediana de $x, $($x+2), $($x+4), $($x+6), $($x+8)." ($x+4) 'Los datos están ordenados. De cinco datos, la mediana es el tercero.'
  }
  2 {
   Add-Q SECUNDARIA $grade Ecuaciones $v "Resuelve: 3x + $b = $(3*$x+$b). ¿Cuánto vale x?" $x "Restamos $b y dividimos entre 3: $(3*$x) ÷ 3 = $x."
   Add-Q SECUNDARIA $grade Sistemas $v "Si x + y = $($x+3) y x - y = $($x-3), ¿cuánto vale x?" $x "Sumamos las ecuaciones para eliminar y: 2x = $(2*$x). Por ello x = $x."
   Add-Q SECUNDARIA $grade Potencias $v "Calcula 2^$($v+1) × 2²." ([int][math]::Pow(2,$v+3)) "Al multiplicar potencias de igual base sumamos exponentes: 2^$($v+3) = $([int][math]::Pow(2,$v+3))."
   Add-Q SECUNDARIA $grade ProporcionInversa $v "$v máquinas iguales hacen un trabajo en 12 horas. Si se usan $($v*2) máquinas, ¿cuántas horas tardan al mismo ritmo?" 6 'Al duplicar las máquinas se reduce el tiempo a la mitad: 12 ÷ 2 = 6 horas.'
   Add-Q SECUNDARIA $grade Porcentajes $v "Una población de $($x*100) habitantes aumenta 20 %. ¿Cuántos habitantes tiene después?" ($x*120) "El nuevo total es 120 % del inicial: $($x*100) × 1,2 = $($x*120)."
  }
  3 {
   Add-Q SECUNDARIA $grade Factorizacion $v "Factoriza x² - $($x*$x). En (x - a)(x + a), con a positivo, ¿cuánto vale a?" $x "Es una diferencia de cuadrados: x² - ${x}² = (x - $x)(x + $x)."
   Add-Q SECUNDARIA $grade Cuadratica $v "Resuelve x² = $($x*$x). ¿Cuál es la solución positiva?" $x "Las raíces son $x y -$x; la positiva es $x."
   Add-Q SECUNDARIA $grade Pitagoras $v "Un triángulo rectángulo tiene catetos de $(3*$v) cm y $(4*$v) cm. ¿Cuántos cm mide la hipotenusa?" (5*$v) "Aplicamos c² = a² + b²: c² = $(25*$v*$v), de modo que c = $(5*$v) cm."
   Add-Q SECUNDARIA $grade Funcion $v "Si f(x) = 2x² - 3, calcula f($v)." (2*$v*$v-3) "Sustituimos x por ${v}: 2 × ${v}² - 3 = $(2*$v*$v-3)."
   Add-Q SECUNDARIA $grade Probabilidad $v "Una bolsa tiene $v bolas rojas y $($v*3) azules. ¿Cuál es la probabilidad de sacar una roja, expresada como porcentaje?" 25 "Hay $($v*4) bolas y $v son rojas: $v/$($v*4) = 1/4 = 25 %."
  }
  4 {
   Add-Q SECUNDARIA $grade Sistemas $v "Si 2x + y = $(2*$x+4) y x - y = $($x-4), ¿cuánto vale x?" $x "Sumamos las ecuaciones: 3x = $(3*$x). Dividimos entre 3: x = $x."
   Add-Q SECUNDARIA $grade Cuadraticas $v "La ecuación x² - $($x+2)x + $($x*2) = 0 tiene dos raíces. ¿Cuál es la mayor?" $x "Factorizamos: (x - 2)(x - $x) = 0. Las raíces son 2 y $x; la mayor es $x."
   Add-Q SECUNDARIA $grade Trigonometria $v "En un triángulo rectángulo, la hipotenusa mide $($v*10) cm y el seno de un ángulo agudo es 3/5. ¿Cuántos cm mide su cateto opuesto?" ($v*6) "Seno = cateto opuesto / hipotenusa. El cateto mide (3/5) × $($v*10) = $($v*6) cm."
   Add-Q SECUNDARIA $grade Sucesiones $v "Una progresión aritmética comienza en $v y tiene diferencia 3. ¿Cuál es su décimo término?" ($v+27) "Usamos a_n = a_1 + (n - 1)d: $v + 9 × 3 = $($v+27)."
   Add-Q SECUNDARIA $grade Geometria $v "Un cilindro tiene radio $v cm y altura 5 cm. Si su volumen se escribe kπ cm³, ¿cuánto vale k?" (5*$v*$v) "V = πr²h = π × ${v}² × 5 = $(5*$v*$v)π cm³."
  }
  5 {
   Add-Q SECUNDARIA $grade Factorizacion $v "Resuelve por factorización: x² - $($x+3)x + $($x*3) = 0. ¿Cuál es la raíz mayor?" $x "Buscamos dos números cuya suma sea $($x+3) y cuyo producto sea $($x*3): 3 y $x. Entonces (x - 3)(x - $x) = 0, y la raíz mayor es $x."
   Add-Q SECUNDARIA $grade FuncionCuadratica $v "La trayectoria h(t) = -(t - $v)² + $($x*2) tiene un máximo. ¿Cuál es su altura máxima?" ($x*2) "El cuadrado es no negativo, por eso -(t - $v)² ≤ 0. La altura máxima es $($x*2), alcanzada en t = $v."
   Add-Q SECUNDARIA $grade Trigonometria $v "Una escalera de $($v*6) m forma 30° con el suelo. ¿A cuántos metros de altura llega? Usa sen 30° = 1/2." ($v*3) "La altura es el cateto opuesto: h = $($v*6) × sen 30° = $($v*6) × 1/2 = $($v*3) m."
   Add-Q SECUNDARIA $grade ProgresionGeometrica $v "Una progresión geométrica empieza en $v y tiene razón 2. ¿Cuál es su sexto término?" ($v*32) "Usamos a_n = a_1 × r^(n-1): $v × 2^5 = $($v*32)."
   Add-Q SECUNDARIA $grade Combinatoria $v "En un grupo de $($v+3) estudiantes se eligen 2 representantes sin importar el orden. ¿Cuántas parejas distintas pueden formarse?" ([int](($v+3)*($v+2)/2)) "Cada pareja se cuenta dos veces si importa el orden. Combinaciones = n(n-1)/2 = $($v+3) × $($v+2) ÷ 2 = $([int](($v+3)*($v+2)/2))."
  }
 }
}}
$json=ConvertTo-Json -InputObject @($items.ToArray()) -Depth 6
[IO.File]::WriteAllText((Join-Path (Get-Location) 'src/main/resources/education/questions.json'),$json,[Text.UTF8Encoding]::new($false))
Write-Output "Preguntas creadas: $($items.Count)"
