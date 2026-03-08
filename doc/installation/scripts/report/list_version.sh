#!/bin/bash

echo -e "\n\nListing software versions:"

echo -e "\ndocker:"
docker --version
docker --help | grep compose

echo -e "\nk3s:"
k3s --version

echo -e "\nhelm:"
echo "Windows : $(/mnt/c/javadev/apps/bin/helm.exe version)"
echo "Linux : $(helm version)"

echo -e "\nskaffold:"
echo " Windows : $(/mnt/c/javadev/apps/bin/skaffold.exe version)"
echo " Linux : $(skaffold version)"

echo -e "\nkubectl:"
/mnt/c/javadev/apps/bin/kubectl.exe version --client=true

echo -e "\noc:"
echo " Windows : $(/mnt/c/javadev/apps/bin/oc.exe version)"
echo " Linux : $(oc version)"