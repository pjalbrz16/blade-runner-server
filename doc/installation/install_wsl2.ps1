if(!([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] "Administrator")){
    Write-Host "Rerun script $PSCommandPath as administrator"
    Start-Process powershell.exe -Wait -Verb RunAs "-NoProfile -ExecutionPolicy Bypass -File $PSCommandPath"
    exit;
}


#Enable WSL2 Feature
Enable-WindowsOptionalFeature -Online -FeatureName Microsoft-Windows-Subsystem-Linux -All -NoRestart
Enable-WindowsOptionalFeature -Online -FeatureName VirtualMachinePlatform -All -NoRestart
Enable-WindowsOptionalFeature -Online -FeatureName Microsoft-Hyper-V-All -All -NoRestart

if(-Not (Test-Path -Path .\staging)){
    New-Item -Path .\staging -ItemType Directory
}

New-Item -ItemType Directory -Force -Path C:\temp | Out-Null
Invoke-WebRequest -OutFile c:\temp\wsl_update_x64.msi -Uri http://aka.ms/wsl2kernelmsix64

Write-Host "Please go to the c:\temp directory, and double click on the wsl_update_x64.msi"
Write-Host "Please restart your computer before continuing"