#!/usr/bin/env python3

"""Calculate the mean and standard deviation of size results"""

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
    "gsg2p/"
    ]

output_paths = [
    "gsg2p/size/"
]

for output_path in output_paths:
    os.makedirs(output_prefix + output_path, exist_ok=True)

def main(args):
    for dataset in datasets:
        for input_path, output_path in zip(input_paths, output_paths):         
            data = np.genfromtxt(input_prefix + input_path + "output-" + dataset + "/sizeReduction.csv", delimiter=",")
            original_size = data[:, 0]
            reduced_size = data[:, 1]

            with open(output_prefix + output_path + dataset + ".csv", 'w') as f:
                f.write("{:.2e}({:.2e})\n{:.2f}({:.2f})".format(np.mean(original_size, axis=0), np.std(original_size, axis=0), np.mean(reduced_size, axis=0), np.std(reduced_size, axis=0)))


if __name__ == "__main__":
    main(sys.argv)