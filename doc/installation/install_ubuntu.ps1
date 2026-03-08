Param (
    [ValidateNotNull()][string]$wslName='Ubuntu-24.04',
    [ValidateNotNull()][string]$username=$Env:UserName,
    [ValidateNotNull()][string]$installAllSoftware=$true,
    [ValidateNotNull()][string]$version='stable',
    [ValidateNotNull()][string]$force=$false,
    [ValidateNotNull()][string]$cleanStaging=$true
)

. "$PSScriptRoot\support\core.ps1"

$wslInstallationBasePath="G:\javadev\wsl2"
$wslInstallationDistroPath="$wslInstallationBasePath\\$wslName"

Say -lines "Welcome to the workstation installation!", "If your Windows Subsystem for Linux (WSL) requires an update,", "please re-launch this script after the update is done."

Start-Process wsl -ArgumentList "--update --web-download" -Verb RunAs -Wait
Say -lines "Wsl $wslName downloaded !"

wsl --set-default-version 2
Say -lines "Wsl version set to 2"

if ($installAllSoftware -eq $true) {
    Invoke-Expression ".\install_packages.ps1 -wslName $wslName"
}

if ($force -eq $true) {
    wsl --unregister $wslName
}

Say -lines "Installing wsl $wslName"

wsl --install -d $wslName

Say -lines "Create user and add to sudoers"

# create your user and add it to sudoers
wsl -d $wslName -u root bash --login -ic "./scripts/config/system/createUser.sh $username ubuntu; sync"

Start-Sleep -Seconds 3

Say -lines "Wsl shutdown"

wsl --shutdown

Start-Sleep -Seconds 2

Say -lines "Update $wslName - apt update and apt upgrade -y"

# Update the system
wsl -d $wslName -u root bash --login -ic "apt update; apt upgrade -y"

Say -lines "Installing Minikube.","This can take up to 5 min, be patient."
wsl -d $wslName -u root bash --login -ic "./scripts/install/install_minikube.sh; sync"

Say -lines "\o/ Installation complete but don't close yet your terminal!"
Write-Output "Tip: Copy this window's content and paste it somewhere to help with troubleshooting if needed"