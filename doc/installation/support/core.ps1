
function Say ([string[]]$lines)
{
    if (Get-Command "gum.exe" -ErrorAction SilentlyContinue)
    {
        gum style `
            --foreground 212 `
            --border double --border-foreground 212 `
            --padding "1 2" `
            --margin "1 2" `
            --align center `
            $lines
    }
    else
    {
        foreach ($line in $lines)
        {
            Out-Host -InputObject $line
        }
    }
}

function VerifyChecksum {
    param(
        [Parameter(Mandatory=$true)][string]$File,
        [Parameter(Mandatory=$true)][string]$ExpectedSha256
    )

    if (-not (Test-Path $File)) { throw "File not found: $File" }

    $actual = (Get-FileHash -Path $File -Algorithm SHA256).Hash.ToLowerInvariant()
    $expected = $ExpectedSha256.ToLowerInvariant()

    if ($actual -ne $expected) {
        throw "Checksum FAILED for $File. Expected $expected, got $actual"
    }

    Write-Output "Checksum OK: $File"
}