$ErrorActionPreference = 'Stop'
$ollamaExe = 'D:\CreateAssetGamer\tools\ai\ollama\ollama.exe'
$modelStore = 'D:\CreateAssetGamer\tools\ai\models\ollama'
$url = 'http://127.0.0.1:11434/api/tags'

if (-not (Test-Path -LiteralPath $ollamaExe -PathType Leaf)) {
    throw "Ollama executable not found: $ollamaExe"
}
if (-not (Test-Path -LiteralPath $modelStore -PathType Container)) {
    throw "Ollama model store not found: $modelStore"
}

try {
    $null = Invoke-RestMethod -Uri $url -TimeoutSec 2
    Write-Host 'Ollama is already responding at http://127.0.0.1:11434.'
    return
} catch {
    # Start the local service below.
}

$env:OLLAMA_MODELS = $modelStore
$env:OLLAMA_HOST = '127.0.0.1:11434'
$env:OLLAMA_LLM_LIBRARY = 'cpu'
$env:CUDA_VISIBLE_DEVICES = '-1'
$process = Start-Process -FilePath $ollamaExe -ArgumentList 'serve' -WindowStyle Hidden -PassThru

for ($attempt = 0; $attempt -lt 20; $attempt++) {
    Start-Sleep -Milliseconds 500
    try {
        $null = Invoke-RestMethod -Uri $url -TimeoutSec 2
        Write-Host "Ollama is ready at http://127.0.0.1:11434 (PID $($process.Id), CPU mode)."
        return
    } catch {
        if ($process.HasExited) {
            throw "Ollama exited during startup with code $($process.ExitCode)."
        }
    }
}

throw 'Ollama started but did not respond within 10 seconds. Check the process and port 11434.'
