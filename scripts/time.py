#!/usr/bin/env python3

"""Calculate the mean and standard deviation of time results"""

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
    "gsgp/time/,
    "gsg2p/time/"
]

for output_path in output_paths:
    os.makedirs(output_prefix + output_path)

def main(args):
    for dataset in datasets:
        for input_path, output_path in zip(input_paths, output_paths):            
            data = np.genfromtxt(input_prefix + input_path + "output-" + dataset + "/elapsedTime.csv", delimiter=',')
            data = data[:, -1]

            mean = np.mean(data, axis=0)
            stddev = np.std(data)

            with open(output_prefix + output_path + dataset + ".csv", 'w') as f:
                f.write("{:.3f}({:.3f})".format(mean, stddev))
                f.write("\n")


if __name__ == "__main__":
    main(sys.argv)