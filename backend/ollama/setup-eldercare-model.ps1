param(
    [switch]$PullBase,
    [string]$OllamaExe = 'D:\CreateAssetGamer\tools\ai\ollama\ollama.exe',
    [string]$ModelsPath = 'D:\CreateAssetGamer\tools\ai\models\ollama'
)

$ErrorActionPreference = 'Stop'
$baseModel = 'qwen3.5:2b-q4_K_M'

if (-not (Test-Path -LiteralPath $OllamaExe)) {
    $command = Get-Command ollama -ErrorAction SilentlyContinue
    if (-not $command) { throw 'Ollama CLI was not found. Set -OllamaExe to its absolute path.' }
    $OllamaExe = $command.Source
}

$env:OLLAMA_HOST = '127.0.0.1:11434'
$env:OLLAMA_MODELS = $ModelsPath
try {
    Invoke-RestMethod -Uri 'http://127.0.0.1:11434/api/version' -TimeoutSec 3 | Out-Null
} catch {
    throw 'Ollama local service is not running. Start Ollama before creating the models.'
}

if ($PullBase) {
    & $OllamaExe pull $baseModel
    if ($LASTEXITCODE -ne 0) { throw "Failed to pull $baseModel" }
}

$models = @(
    @{ Name = 'eldercare-ai'; File = 'Modelfile.eldercare' },
    @{ Name = 'eldercare-website'; File = 'Modelfile.website' }
)

foreach ($model in $models) {
    $path = Join-Path $PSScriptRoot $model.File
    if (-not (Test-Path -LiteralPath $path)) { throw "Missing Modelfile: $path" }
    & $OllamaExe create $model.Name -f $path
    if ($LASTEXITCODE -ne 0) { throw "Failed to create $($model.Name)" }
    Write-Host "Created $($model.Name)"
}

Write-Host 'Use eldercare-ai as the admin Ollama model. Public requests default to eldercare-website.'
