#!/bin/bash

set -euo pipefail
DIR_INSTALL_UTILS=$(realpath $( dirname ${BASH_SOURCE[0]:-$0} ) )
USERNAME=""
HOMEDIR=""

modifyBashrc () {
  searchFor=${1}
  writeString=${2}
  if [[ $(cat ${HOMEDIR}/.bashrc | grep ${searchFor} | wc -l) == 0 ]]; then
    echo ${writeString} >> ${HOMEDIR}/.bashrc
    . ${HOMEDIR}/.bashrc
  else
    echo "${searchFor}: ${HOMEDIR}/.bashrc is already properly configured."
  fi
}

copyConfigureScript () {
  scriptToUse=${1}
  if [[ ! -d ${HOMEDIR}/.local/bin/env/ ]]; then
    mkdir -p ${HOMEDIR}/.local/bin/env/
  fi
  cp -f ${DIR_INSTALL_UTILS}/../config/local/${scriptToUse} ${HOMEDIR}/.local/bin/env/
}

setUserName () {
  USERNAME=${1-""}
  verifyUserName
}

ensureStaging() {
  sudo rm -rf staging ||echo "Staging does not exist"
  mkdir -p staging
}

executeInPowershell () {
  "/mnt/c/windows/system32/WindowsPowerShell/v1.0/powershell.exe" -NoProfile -Command "$1"
}

downloadExe() {
  curl -L -o ./staging/${1} ${2}
  sudo chmod a+x ./staging/${1}
  sudo cp ./staging/${1} $(windowsBinDir)
}

downloadLinux() {
  curl -L -o ./staging/${1} ${2}
  sudo chmod a+x ./staging/${1}
  sudo cp ./staging/${1} /usr/local/bin
}


downloadZip() {
  app_name=$1
  url=$2
  dir=$3
  curl -L -o ./staging/${app_name}.zip ${url}
  sudo unzip -d ./staging/${app_name}_exploded ./staging/${app_name}.zip
  sudo cp ./staging/${app_name}_exploded/${dir}/${app_name}.exe "$(windowsBinDir)/${app_name}.exe"
}

downloadTgz() {
  app_name=$1
  url=$2
  dir=$3
  sudo mkdir -p ./staging/${app_name}_exploded
  curl -L -o ./staging/${app_name}.tar.gz ${url}
  sudo tar xf ./staging/${app_name}.tar.gz -C ./staging/${app_name}_exploded
  sudo cp ./staging/${app_name}_exploded/${dir}/${app_name}.exe "$(windowsBinDir)/${app_name}.exe"
}

downloadZipLinux() {
  app_name=$1
  url=$2
  dir=$3
  curl -L -o ./staging/${app_name}.zip ${url}
  sudo unzip -d ./staging/${app_name}_exploded ./staging/${app_name}.zip
  sudo cp ./staging/${app_name}_exploded/${dir}/${app_name} "/usr/local/bin/${app_name}"
}

downloadTgzLinux() {
  app_name=$1
  url=$2
  dir=$3
  sudo mkdir -p ./staging/${app_name}_exploded
  curl -L -o ./staging/${app_name}.tar.gz ${url}
  sudo tar xf ./staging/${app_name}.tar.gz -C ./staging/${app_name}_exploded
  sudo cp ./staging/${app_name}_exploded/${dir}/${app_name} "/usr/local/bin/${app_name}"
}

addDirectoryToWindowsPath(){
  WIN_DIR=$(wslpath -w $1)
  executeInPowershell "if (!\$env:path.contains(\"${WIN_DIR}\")) { echo \"Adding dir to path's add it. Please restart your terminal before reexecuting\"; [Environment]::SetEnvironmentVariable(\"Path\", [Environment]::GetEnvironmentVariable(\"Path\", \"User\") + \";${WIN_DIR}\", \"User\")}"
}

setWindowsUserEnvVar(){
  executeInPowershell "[Environment]::SetEnvironmentVariable(\"$1\", \"$2\", \"User\")"
}


windowsBinDir() {
  BIN_DIR="/mnt/c/javadev/apps/bin"
  mkdir -p $BIN_DIR
  echo $BIN_DIR
}

verifyUserName () {
  if [[ ${USERNAME} == "" ]]; then
    echo "Please pass a user name"
    exit 1
  elif [[ ${USERNAME} == "root" ]]; then
    HOMEDIR="/root"
  else
    HOMEDIR="/home/${USERNAME}"
  fi
}

addSudoers () {
  suodersString=${1-""}
  suodersFilename=${2-""}
  if [[ ${suodersFilename} == "" ]]; then
    echo "Please provide a filename for sudoers file"
    exit 1
  fi

  temp_sudoers=$(mktemp)
  echo ${suodersString} > ${temp_sudoers}

  # only add sudoers.d additions after checking with visudo
  VISUDO_RES=$(sudo visudo -c -f ${temp_sudoers})
  # check with no error messages (s) and only mathcing (o)
  VISODU_PARSE_OK=$(echo ${VISUDO_RES} | grep -so "parsed OK" | wc -l)

  #only add if vidudo said OK
  if [[ VISODU_PARSE_OK -eq 1  ]]; then
      sudo cp -f ${temp_sudoers} /etc/sudoers.d/${suodersFilename}
  fi
}

modifyWslConf () {
  verifyUserName

  cat << EOF >> /etc/wsl.conf
[user]
default=${USERNAME}
EOF
}