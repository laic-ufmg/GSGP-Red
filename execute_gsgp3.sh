#!/bin/bash
#title          :execute_gsgp3.sh
#description    :Execute GSGP3 for all specified datasets
#author         :Joao Francisco B. S. Martins
#date           :01.02.2018
#usage          :bash execute_gsgp3.sh
#bash_version   :GNU bash, version 4.4.0(1)-release
#==============================================================================

datasets=("airfoil" "ccn" "ccun" "concrete" "energyCooling" "energyHeating" "parkinsons" "ppb" "towerData" "wineRed" "wineWhite" "yacht")
gsgp_path=$(pwd)"/gsgp3/out/artifacts/gsgp3_jar"
experiments_path=$(pwd)"/experiments/gsgp3"
results_path=$(pwd)"/results/gsgp3"
scripts_path=$(pwd)"/scripts"

mkdir -p "$results_path"

for dataset in "${datasets[@]}"
do
    echo "Executing $dataset"
    java -Xms512m -Xmx8g -jar "$gsgp_path"/gsgp3.jar -p "$experiments_path"/"$dataset".txt > "$results_path"/"$dataset".txt
done