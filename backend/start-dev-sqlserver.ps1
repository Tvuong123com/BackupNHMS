param(
    [ValidateRange(1, 65535)]
    [int]$Port = 8080
)

$ErrorActionPreference = 'Stop'
$envFile = Join-Path $PSScriptRoot '.env'
if (-not (Test-Path -LiteralPath $envFile -PathType Leaf)) {
    throw "Missing backend/.env. Configure SQL Server before starting the backend."
}

Get-Content -LiteralPath $envFile | ForEach-Object {
    $line = $_.Trim()
    if ($line -match '^([A-Z][A-Z0-9_]*)=(.*)$') {
        $value = $Matches[2].Trim()
        if ($value.Length -ge 2 -and
            (($value.StartsWith('"') -and $value.EndsWith('"')) -or
             ($value.StartsWith("'") -and $value.EndsWith("'")))) {
            $value = $value.Substring(1, $value.Length - 2)
        }
        Set-Item -Path "Env:$($Matches[1])" -Value $value
    }
}

foreach ($name in @('DB_HOST', 'DB_PORT', 'DB_NAME', 'DB_USERNAME', 'DB_PASSWORD')) {
    if (-not (Get-Item -Path "Env:$name" -ErrorAction SilentlyContinue).Value) {
        throw "Missing $name in backend/.env."
    }
}

# Set the effective datasource explicitly so an inherited H2 override cannot
# silently start an empty database and reject existing SQL Server accounts.
$env:SPRING_PROFILES_ACTIVE = 'dev'
$env:SPRING_DATASOURCE_URL = "jdbc:sqlserver://$($env:DB_HOST):$($env:DB_PORT);databaseName=$($env:DB_NAME);encrypt=true;trustServerCertificate=true"
$env:SPRING_DATASOURCE_USERNAME = $env:DB_USERNAME
$env:SPRING_DATASOURCE_PASSWORD = $env:DB_PASSWORD
$env:SERVER_PORT = [string]$Port

Push-Location $PSScriptRoot
try {
    & mvn spring-boot:run '-Dmaven.test.skip=true'
    if ($LASTEXITCODE -ne 0) {
        throw "Backend exited with code $LASTEXITCODE."
    }
} finally {
    Pop-Location
}
