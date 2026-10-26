$ErrorActionPreference='Stop'
& "$PSScriptRoot/New-MathQuestionBank.ps1"
$path=Join-Path (Get-Location) 'src/main/resources/education/questions.json'
$questions=[Collections.Generic.List[object]]::new()
foreach($q in (Get-Content $path -Raw -Encoding UTF8 | ConvertFrom-Json)) { $questions.Add($q) }
$counts=@{}
foreach($file in @('intro-questions.psv','social-questions.psv','digital-questions.psv')) {
 foreach($line in Get-Content "$PSScriptRoot/$file" -Encoding UTF8){
  if($line.StartsWith('#') -or [string]::IsNullOrWhiteSpace($line)){continue}
  $fields=$line.Split('|')
  if($fields.Count -ne 8){throw "Campos incorrectos: $line"}
  $course=$fields[0];if(!$counts.ContainsKey($course)){$counts[$course]=0};$n=++$counts[$course]
  $offset=$n%4;$values=$fields[3..6];$options=@(0..3 | ForEach-Object {$values[($_+$offset)%4]})
  $difficulty=if($n -le 5){'INTERMEDIA'}elseif($n -le 10){'INTERMEDIA_ALTA'}else{'DESAFIO_FINAL'}
  $questions.Add([ordered]@{id="INTRO-PRIMARIA-1-$course-$n";statement=$fields[2];alternatives=$options;correctAnswer=[array]::IndexOf($options,$fields[3]);explanation=$fields[7];level='PRIMARIA';grade=1;course=$course;topic=$fields[1];difficulty=$difficulty})
 }
}
$json=ConvertTo-Json -InputObject @($questions.ToArray()) -Depth 6
[IO.File]::WriteAllText($path,$json,[Text.UTF8Encoding]::new($false))
Write-Output "Total: $($questions.Count). Cobertura adicional:"
$counts
