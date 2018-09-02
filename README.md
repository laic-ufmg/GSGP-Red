# GSGP-Red
**G**eometric **S**emantic **G**enetic **P**rogramming with **Red**uced trees

## Description
This project is an extension of the GSGP Java implementation first presented at [https://github.com/luizvbo/gsgp-gd](https://github.com/luizvbo/gsgp-gd). Some parts of the code are reworked to make it more clear and intuitive. Also, in order to greatly reduce the size of the solutions, the workflow of GSGP-Red has been included in its respective implementation.

## Repository contents
* _experiments_: parameter files for each implementation.
* _gsgp_ and _gsgp-red_: algorithm implementations in Java.
* _scripts_: miscellaneous scripts, mostly used for extended analysis of results.
* *execute_gsgp.py* and *execute_gsgp-red.py*: customizable execution scripts.

## Datasets

The datasets are not included in this repository to reduce its size. All the datasets used in the publications can be found and downloaded at this [link](https://drive.google.com/drive/folders/1cUU7f23z_lBPQCOX7h1unZWKZH3YO_EB?usp=sharing). Each dataset is prepared for a 5-fold cross-validation procedure.

## Running tests

### Parameters

The **master-example.txt** file inside the *experiments* directory presents instructions for each parameter (as comments started with '**#**'). The **master** files are the only that need to be altered to change the behaviour of the algorithm. The dataset specific files are only to refer to its master and get its input/output paths.

### Execution

In order to execute the experiments, run the following command line:

```
    java -jar path/to/jar -p path/to/parameter/file
```

For each implementation, the **jar** can be found at `/out/artifacts`. The parameter file to be passed here is the one specific to the desired dataset and not a **master** file.


## Published papers
* Joao F. B. S. Martins, Luiz Otavio V. B. Oliveira, Luis F. Miranda, Felipe Casadei, and Gisele L. Pappa. **Solving the exponential growth of symbolic regression trees in geometric semantic genetic programming**. In _Proceedings of the Genetic and Evolutionary Computation Conference_, GECCO ’18, Kyoto, Japan, 2018. ACM. ISBN 978-1-4503-5618-3/18/07. doi: 10.1145/3205455.3205593
