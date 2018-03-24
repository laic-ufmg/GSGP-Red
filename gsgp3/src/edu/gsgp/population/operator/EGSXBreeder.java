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
public class EGSXBreeder extends Breeder{

    // Elitist Geometric Semantic Crossover
    public EGSXBreeder(PropertiesManager properties, Double probability) {
        super(properties, probability);
    }


    /*** EUCLIDEAN SEGMENT CROSSOVER ***/

    private Fitness evaluate(GSGPIndividual ind1,
                             GSGPIndividual ind2,
                             double rtValue,
                             ExperimentalData expData){

        Fitness fitnessFunction = ind1.getFitnessFunction().softClone();

        fitnessFunction.resetFitness(DatasetType.TRAINING, expData);
        Dataset dataset = expData.getDataset(DatasetType.TRAINING);

        // Parents semantics
        double[] semInd1;
        double[] semInd2;

        semInd1 = ind1.getTrainingSemantics();
        semInd2 = ind2.getTrainingSemantics();

        int instanceIndex = 0;
        for (Instance instance : dataset) {
            double estimated = rtValue * semInd1[instanceIndex] + (1 - rtValue) * semInd2[instanceIndex];

            fitnessFunction.setSemanticsAtIndex(estimated, instance.output, instanceIndex++, DatasetType.TRAINING);
        }
        fitnessFunction.computeFitness(DatasetType.TRAINING);

        return fitnessFunction;
    }


    @Override
    public Individual generateIndividual(MersenneTwister rndGenerator, ExperimentalData expData) {
        // Crossover parents
        GSGPIndividual parent1 = (GSGPIndividual)properties.selectIndividual(originalPopulation, rndGenerator);
        GSGPIndividual parent2 = (GSGPIndividual)properties.selectIndividual(originalPopulation, rndGenerator);

        // Prevents parents from being the same individual
        while(parent1.equals(parent2)) parent2 = (GSGPIndividual)properties.selectIndividual(originalPopulation, rndGenerator);

        // Random constant used in Euclidian Crossover
        double rc = rndGenerator.nextDouble(true, true);

        // Compute the number of nodes in the offspring (4 extra nodes used for operations, 2 for constant and 1 for the number 1)
        BigInteger numNodes = parent1.getNumNodes().
                add(parent2.getNumNodes()).
                add(BigInteger.valueOf(7));

        Fitness fitnessFunction = evaluate(parent1, parent2, rc, expData);

        double parent1Fitness = parent1.getTrainingFitness();
        double parent2Fitness = parent2.getTrainingFitness();
        double offspringFitness = fitnessFunction.getTrainingFitness();

        if(offspringFitness < parent1Fitness && offspringFitness < parent2Fitness) {
            GSGPIndividual offspring = new GSGPIndividual(numNodes, fitnessFunction, parent1, parent2, rc);

            return offspring;
        }
        else if (parent1Fitness < parent2Fitness) {
            return parent1.clone();
        }
        else {
            return parent2.clone();
        }
    }


    /*** MANHATTAN SEGMENT CROSSOVER ***/

    /*
    private Fitness evaluate(GSGPIndividual ind1,
                             GSGPIndividual ind2,
                             Node randomTree,
                             ExperimentalData expData){
        Fitness fitnessFunction = ind1.getFitnessFunction().softClone();
        for(DatasetType dataType : DatasetType.values()){
            // Compute the (training/test) semantics of generated random tree
            fitnessFunction.resetFitness(dataType, expData);
            Dataset dataset = expData.getDataset(dataType);
            double[] semInd1;
            double[] semInd2;
            if(dataType == DatasetType.TRAINING){
                semInd1 = ind1.getTrainingSemantics();
                semInd2 = ind2.getTrainingSemantics();
            }
            else{
                semInd1 = ind1.getTestSemantics();
                semInd2 = ind2.getTestSemantics();
            }
            int instanceIndex = 0;
            for (Instance instance : dataset) {
                double rtValue = Utils.sigmoid(randomTree.eval(instance.input));
                double estimated = rtValue*semInd1[instanceIndex] + (1-rtValue)*semInd2[instanceIndex];
                fitnessFunction.setSemanticsAtIndex(estimated, instance.output, instanceIndex++, dataType);
            }
            fitnessFunction.computeFitness(dataType);
        }
        return fitnessFunction;
    }

    @Override
    public Individual generateIndividual(MersenneTwister rndGenerator, ExperimentalData expData) {
        GSGPIndividual p1 = (GSGPIndividual)properties.selectIndividual(originalPopulation, rndGenerator);
        GSGPIndividual p2 = (GSGPIndividual)properties.selectIndividual(originalPopulation, rndGenerator);
        while(p1.equals(p2)) p2 = (GSGPIndividual)properties.selectIndividual(originalPopulation, rndGenerator);
        Node rt = properties.getRandomTree(rndGenerator);
        BigInteger numNodes = p1.getNumNodes().add(p2.getNumNodes()).add(BigInteger.ONE).add(BigInteger.ONE);
        Fitness fitnessFunction = evaluate(p1, p2, rt, expData);
        GSGPIndividual offspring = new GSGPIndividual(numNodes, fitnessFunction, p1, p2, null, null, null);
        return offspring;
    }
    */


    @Override
    public Breeder softClone(PropertiesManager properties) {
        return new EGSXBreeder(properties, probability);
    }
}
