#!/bin/bash

set -euo pipefail
DIR_ME=$(realpath $(dirname $0))

# this script is called by root an must fail if no user is provided
. ${DIR_ME}/../../install/.installUtils.sh
setUserName ${1-""}
OS_TYPE=${2-"ubuntu"}

setupSsh () {
  mkdir -p ~/.ssh

  if test -f "/mnt/c/Users/$USER/.ssh/id_rsa"; then
      cp "/mnt/c/Users/$USER/.ssh/id_rsa" ~/.ssh/id_rsa
  fi

  if test -f "/mnt/c/Users/$USER/.ssh/id_rsa.pub"; then
        cat "/mnt/c/Users/$USER/.ssh/id_rsa.pub" >> ~/.ssh/authorized_keys
  fi

  sudo chmod -R 700 ~/.ssh
}

createMainUser () {
  echo "Main user creation..."
  verifyUserName
  if [[ $(cat /etc/passwd | grep ${USERNAME} | wc -l) == 0 ]]; then
    useradd -m -s /bin/bash ${USERNAME}
  fi

  # add to docker group
  usermod -aG docker ${USERNAME}

  # add to sudo group
  if [[ "${OS_TYPE}" == "ubuntu" ]]; then
    usermod -aG sudo ${USERNAME}
  fi

  if [[ ! -d ${HOMEDIR}/Downloads ]]; then
      mkdir ${HOMEDIR}/Downloads
      chown ${USERNAME}:${USERNAME} ${HOMEDIR}/Downloads
  fi

  # ensure no password is set
  passwd -d ${USERNAME}

  # Get UID from username
  TARGET_UID=$(id -u "$USERNAME" 2>/dev/null)

  if [ -z "$TARGET_UID" ]; then
      echo "Error: User '$USERNAME' does not exist"
  fi

  echo "Configuring XDG_RUNTIME_DIR for user $USERNAME (UID: $TARGET_UID)"

  # Create the linger directory if it doesn't exist
  mkdir -p /var/lib/systemd/linger

  # Enable linger by creating the linger file manually
  touch /var/lib/systemd/linger/${USERNAME}

  # Create tmpfiles configuration for this user's runtime directory
  cat > /etc/tmpfiles.d/user-${TARGET_UID}.conf << EOF
# Runtime directory for user ${USERNAME} (UID ${TARGET_UID})
d /run/user/${TARGET_UID} 0700 ${TARGET_UID} ${TARGET_UID} -
EOF

  # Apply the tmpfiles configuration immediately
  systemd-tmpfiles --create /etc/tmpfiles.d/user-${TARGET_UID}.conf

  echo "✓ Enabled linger and runtime directory for user $USERNAME"
}
createMainUser
setupSsh

modifyWslConf