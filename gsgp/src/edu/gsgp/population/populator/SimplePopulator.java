/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population.populator;

import edu.gsgp.MersenneTwister;
import edu.gsgp.Utils.DatasetType;
import edu.gsgp.data.Dataset;
import edu.gsgp.data.ExperimentalData;
import edu.gsgp.data.Instance;
import edu.gsgp.data.PropertiesManager;
import edu.gsgp.nodes.Node;
import edu.gsgp.population.GSGPIndividual;
import edu.gsgp.population.Population;
import edu.gsgp.population.fitness.Fitness;

import java.math.BigInteger;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 2014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 * 
 * Generate a population using the tree initialization method given in the parameter file
 */
public class SimplePopulator extends Populator{

    public SimplePopulator(PropertiesManager properties) {
        super(properties);
    }
    
    /**
     * Evaluate the new generated trees
     * @param newTree The tree to be evaluated
     * @return The fitness of the input tree
     */
    private Fitness evaluate(Node newTree, ExperimentalData expData){
        Fitness fitnessFunction = properties.getFitnessFunction();

        for(DatasetType dataType : DatasetType.values()){
            // Compute the (training/test) semantics of generated random tree
            fitnessFunction.resetFitness(dataType, expData);
            Dataset dataset = expData.getDataset(dataType);
            int instanceIndex = 0;
            for (Instance instance : dataset) {
                double estimated = newTree.eval(instance.input);
                fitnessFunction.setSemanticsAtIndex(estimated, instance.output, instanceIndex++, dataType);
            }
            fitnessFunction.computeFitness(dataType);
        }

        return fitnessFunction;
    }

    /**
     * Generate a population of inidividuals created from the initialization tree method
     * given in the parameter file
     * @param rndGenerator Pseudorandom number generator
     * @param expData Experimental data used to evaluate the inidividual
     * @param size Size of the population to be generated
     * @return The generated popualtion
     */
    @Override
    public Population populate(MersenneTwister rndGenerator, ExperimentalData expData, int size) {
        Population population = new Population();
        for(int i = 0; i < size; i++){
            Node newTree = properties.getNewIndividualTree(rndGenerator);
            Fitness fitnessFunction = evaluate(newTree, expData);
            population.add(new GSGPIndividual(newTree, BigInteger.valueOf(newTree.getNumNodes()), fitnessFunction));
        }
        return population;
    }

    @Override
    public Populator softClone() {
        return new SimplePopulator(properties);
    }
}
