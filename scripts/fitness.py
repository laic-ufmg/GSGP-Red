#!/usr/bin/env python3

"""Calculate the median of fitness results"""

__author__ = "João Francisco Barreto da Silva Martins"
__email__ = "joaofbsm@dcc.ufmg.br"
__license__ = "MIT"

import os
import sys
import numpy as np

datasets = ["airfoil", "ccn", "ccun", "concrete", "energyCooling", "energyHeating", "parkinsons", "ppb", "towerData", "wineRed", "wineWhite", "yacht"]

input_prefix = "/Users/joaofbsm/Documents/UFMG/2018-1/POC2/implementation/results/"
output_prefix = "/Users/joaofbsm/Documents/UFMG/2018-1/POC2/implementation/processed/"

input_paths = [
    "gsgp/",
    "gsg2p/"
    ]

output_paths = [
    "gsgp/fitness/",
    "gsg2p/fitness/"
]

file_names = ["trFitness.csv", "tsFitness.csv"]

for output_path in output_paths:
    os.makedirs(output_prefix + output_path, exist_ok=True)

def main(args):
    for dataset in datasets:
        for input_path, output_path in zip(input_paths, output_paths):
            data = np.genfromtxt(input_prefix + input_path + "output-" + dataset + "/trFitness.csv", delimiter=',')
            data = data[:, -1]

            median = np.median(data, axis=0)

            with open(output_prefix + output_path + dataset + ".csv", 'w') as f:
                f.write("{:.3f}".format(median))
                f.write("\n")

            data = np.genfromtxt(input_prefix + input_path + "output-" + dataset + "/tsFitness.csv", delimiter=',')
            data = data[:, -1]

            median = np.median(data, axis=0)

            with open(output_prefix + output_path + dataset + ".csv", 'a') as f:
                f.write("{:.3f}".format(median))
                f.write("\n")


if __name__ == "__main__":
    main(sys.argv)