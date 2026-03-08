# 🐧 WSL2 + Ubuntu Automated Installation

This repository provides PowerShell scripts to install **WSL2** and **Ubuntu** on Windows in a simple and reproducible way.

The installation is divided into two steps because **WSL installation requires administrator privileges**, while the **Ubuntu distribution installation should run as a standard user**.

---

## 📋 Requirements

- Windows 10 (version 2004+) or Windows 11
- PowerShell 5.1 or PowerShell 7+
- Internet connection
- Administrator rights (for the first step)

---

## 📦 Installation Steps

### 1️⃣ Install WSL2 (Administrator PowerShell)

1. Open **PowerShell as Administrator**
2. Navigate to this repository folder
3. Run:

```powershell
.\install_wsl2.ps1