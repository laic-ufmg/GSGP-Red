/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population.operator;

import edu.gsgp.MersenneTwister;
import edu.gsgp.Utils;
import edu.gsgp.Utils.DatasetType;
import edu.gsgp.data.Dataset;
import edu.gsgp.data.ExperimentalData;
import edu.gsgp.data.Instance;
import edu.gsgp.data.PropertiesManager;
import edu.gsgp.nodes.Node;
import edu.gsgp.population.GSGPIndividual;
import edu.gsgp.population.Individual;
import edu.gsgp.population.fitness.Fitness;
import java.math.BigInteger;
import java.util.Map;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 2014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class GSMBreeder extends Breeder{

    public GSMBreeder(PropertiesManager properties, Double probability) {
        super(properties, probability);
    }


    private Fitness evaluate(GSGPIndividual ind,
                             Node randomTree1,
                             Node randomTree2,
                             ExperimentalData expData) {

        Fitness fitnessFunction = ind.getFitnessFunction().softClone();

        // Compute the training semantics of generated random tree
        fitnessFunction.resetFitness(DatasetType.TRAINING, expData);
        Dataset dataset = expData.getDataset(DatasetType.TRAINING);

        // Parent semantics
        double[] semInd;

        semInd = ind.getTrainingSemantics();

        int instanceIndex = 0;
        for (Instance instance : dataset) {

            double rtValue = randomTree1.eval(instance.input);
            rtValue -= randomTree2.eval(instance.input);

            double estimated = semInd[instanceIndex] + properties.getMutationStep() * rtValue;

            fitnessFunction.setSemanticsAtIndex(estimated, instance.output, instanceIndex++, DatasetType.TRAINING);
        }
        fitnessFunction.computeFitness(DatasetType.TRAINING);

        return fitnessFunction;
    }


    @Override
    public Individual generateIndividual(MersenneTwister rndGenerator, ExperimentalData expData) {
        // Mutation parent
        GSGPIndividual parent = (GSGPIndividual)properties.selectIndividual(originalPopulation, rndGenerator);

        // Generate random mask trees
        Node randomTree1 = properties.getRandomTree(rndGenerator);
        Node randomTree2 = properties.getRandomTree(rndGenerator);

        // Compute the number of nodes in the offspring (3 extra nodes used for operations and 1 for mutation step)
        BigInteger numNodes = parent.getNumNodes().
                add(BigInteger.valueOf(randomTree1.getNumNodes())).
                add(BigInteger.valueOf(randomTree2.getNumNodes())).
                add(BigInteger.valueOf(4));

        Fitness fitnessFunction = evaluate(parent, randomTree1, randomTree2, expData);

        GSGPIndividual offspring = new GSGPIndividual(numNodes, fitnessFunction, parent, randomTree1, randomTree2, properties.getMutationStep());

        return offspring;
    }


    @Override
    public Breeder softClone(PropertiesManager properties) {
        return new GSMBreeder(properties, probability);
    }
}