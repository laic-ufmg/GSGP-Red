#!/bin/bash
#title          :execute_gsg2p.sh
#description    :Execute GSG2P for all specified datasets
#author         :Joao Francisco B. S. Martins
#date           :01.02.2018
#usage          :bash execute_gsg2p.sh
#bash_version   :GNU bash, version 4.4.0(1)-release
#==============================================================================

datasets=("airfoil" "ccn" "ccun" "concrete" "energyCooling" "energyHeating" "parkinsons" "ppb" "towerData" "wineRed" "wineWhite" "yacht")
gsgp_path=$(pwd)"/gsg2p/out/artifacts/gsg2p_jar"
experiments_path=$(pwd)"/experiments/gsg2p"
results_path=$(pwd)"/results/gsg2p"
scripts_path=$(pwd)"/scripts"

mkdir -p "$results_path"

for dataset in "${datasets[@]}"
do
    echo "Executing $dataset"
    java -Xms512m -Xmx8g -jar "$gsgp_path"/gsg2p.jar -p "$experiments_path"/"$dataset".txt > "$results_path"/"$dataset".txt
done