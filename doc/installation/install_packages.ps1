Param (
    [ValidateNotNull()][string]$wslName='Ubuntu-24.04'
)

. "$PSScriptRoot\support\core.ps1"

# Configure git to make it able to install Scoop
#git config --global http.version HTTP/1.1

if (Get-Command -Name scoop -ErrorAction SilentlyContinue) {
    Write-Output "Scoop is installed"
} else {
    Write-Output "Installing Scoop"
    Invoke-RestMethod get.scoop.sh | Invoke-Expression
}

echo "Updating scoop"

scoop update *
if (scoop bucket list | Select-String "charm")
{
    scoop uninstall charm-gum
    scoop bucket rm charm
}

echo "Import scoop-starter.json"

# A first scoop installation must be performed with minimal required
scoop import .\scoop-starter.json
#Say -lines "Attention, before all you have to setup a ssh key with Azure DevOps"

#while ($true) {
#    gum confirm "Did you correctly created your ssh key for Azure DevOps?"
#    if ($LASTEXITCODE -eq 0) { break }
#    Say -lines "Please follow the instructions of the link above to create your SSH key"
#}

echo "Import scoop-baseling.json"

scoop import .\scoop-baseline.json

#scoop uninstall skaffold
#scoop install skaffold

# Say -lines "Defining Minikube base configuration"
# minikube config set cpus 4
# minikube config set memory 24g


Say -lines "Installing Docker BuildKit"

$targetName = "buildx-v0.32.0.windows-amd64.exe"

$file = Join-Path $HOME ".docker\cli-plugins\docker-buildx.exe"

New-Item -ItemType Directory -Force -Path (Split-Path $file) | Out-Null

$releases = Invoke-RestMethod -Uri "https://api.github.com/repos/docker/buildx/releases"
$asset = $null

foreach ($release in $releases) {
    if ($release.prerelease) { continue }
    $asset = $release.assets | Where-Object { $_.name -eq $targetName } | Select-Object -First 1
    if ($asset) { break }
}

if (-not $asset) {
    throw "Could not find asset $targetName in docker/buildx releases."
}

$digest = $asset.digest.Replace("sha256:","")

curl.exe -L -o $file $asset.browser_download_url
VerifyChecksum -File $file -ExpectedSha256 $digest

if (Get-Command docker -ErrorAction SilentlyContinue) {
    docker buildx install
} else {
    Write-Warning "docker not found in PATH, skipping 'docker buildx install'"
}

helm repo add --force-update codecentric https://codecentric.github.io/helm-charts
helm repo add --force-update bitnami https://charts.bitnami.com/bitnami

Say -lines "Configuring WSL"
Copy-Item scripts\files\.wslconfig ${HOME}/.wslconfig
Copy-Item scripts\files\.bashrc ${HOME}/.wslconfig

Say -lines "Configuring kubernetes"
[Environment]::SetEnvironmentVariable('SKAFFOLD_DEFAULT_REPO', "", 'User')
[Environment]::SetEnvironmentVariable('SKAFFOLD_KUBE_CONTEXT', "minikube", 'User')
[Environment]::SetEnvironmentVariable('SKAFFOLD_PUSH', "false", 'User')

Say -lines "Configuring docker"
[Environment]::SetEnvironmentVariable('DOCKER_HOST', "tcp://localhost:2375", 'User')
