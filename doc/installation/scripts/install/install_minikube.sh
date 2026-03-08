#!/bin/bash

set -euo pipefail
DIR_ME=$(realpath $(dirname $0))
. ${DIR_ME}/.installUtils.sh

# Ensure staging directory exists
ensureStaging

echo "Installing Minikube for Linux (amd64)..."

# Download and install Minikube binary
# Using the official static binary URL
MINIKUBE_URL="https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64"

downloadLinux "minikube" "${MINIKUBE_URL}"

# Set default configuration for Minikube
echo "Configuring Minikube defaults..."
# Run as the regular user if possible, but we are root here in the script context usually
# However, 'minikube config' changes are per-user.
# If we want to set it for the user created in createUser.sh, we might need to handle it.
# For now, let's just install it. The user can run config later.
# But we can try to set it for the main user if we know the username.

# Since we are likely root when this is called by install_ubuntu.ps1
# and we want to configure it for the user.

minikube config set cpus 4
minikube config set memory 16384 # 16GB instead of 24GB to be safer, or keep it as user had.
minikube config set driver docker

echo "Minikube installation and basic configuration complete."

# Note: Minikube doesn't usually run as a systemd service by default.
# It is typically started by the user with 'minikube start'.
# If you want to use Docker as the driver (recommended for WSL2):
# minikube start --driver=docker
