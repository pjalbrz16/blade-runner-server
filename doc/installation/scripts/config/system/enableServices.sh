#!/bin/bash

set -euo pipefail
DIR_ME=$(realpath $(dirname $0))

sudo systemctl enable docker
sudo systemctl start docker

#sudo systemctl enable minikube
#sudo systemctl start minikube

#sudo systemctl enable ssh
#sudo systemctl start ssh

sudo systemctl enable cron
sudo systemctl start cron