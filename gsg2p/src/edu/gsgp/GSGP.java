/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp;

import edu.gsgp.data.Dataset;
import edu.gsgp.data.ExperimentalData;
import edu.gsgp.data.Instance;
import edu.gsgp.nodes.Node;
import edu.gsgp.nodes.functions.*;
import edu.gsgp.nodes.terminals.ERC;
import edu.gsgp.population.GSGPIndividual;
import edu.gsgp.population.Population;
import edu.gsgp.population.Individual;
import edu.gsgp.data.PropertiesManager;
import edu.gsgp.population.fitness.Fitness;
import edu.gsgp.population.populator.Populator;
import edu.gsgp.population.pipeline.Pipeline;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 2014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */

public class GSGP {
    private final PropertiesManager properties;
    private final Statistics statistics;
    private final ExperimentalData expData;
    private final MersenneTwister rndGenerator;


    public GSGP(PropertiesManager properties, ExperimentalData expData) throws Exception{
        this.properties = properties;
        this.expData = expData;
        statistics = new Statistics(properties.getNumGenerations(), expData);
        rndGenerator = properties.getRandomGenerator();
    }


    public void evolve() throws Exception{
        statistics.startClock();

        // Early stopping flag
        boolean canStop = false;

        // Initialize auxiliary structures
        Populator populator = properties.getPopulationInitializer();
        Pipeline pipe = properties.getPipeline();
        pipe.setup(properties, statistics, expData, rndGenerator);

        // Generate initial population
        Population population = populator.populate(rndGenerator, expData, properties.getPopulationSize());

        statistics.addGenerationStatistic(population);

        // Map Objects to hashCodes used in individual reconstruction
        Map<Integer, Individual> initialInds = new HashMap<>();

        // Maps initial individuals' Object to hashCode
        for(Individual ind : population) {
            Integer indHash = ind.hashCode();
            initialInds.put(indHash, ind);
        }
        
        for(int i = 0; i < properties.getNumGenerations() && !canStop; i++){
            // Evolve a new Population
            Population newPopulation = pipe.evolvePopulation(population, expData, properties.getPopulationSize()-1);

            // The first position is reserved for the best of the generation (elitism)
            newPopulation.add(population.getBestIndividual());

            // Check stopping criterion
            Individual bestIndividual = newPopulation.getBestIndividual();
            if(bestIndividual.isBestSolution(properties.getMinError())) canStop = true;

            // Update the population
            population = newPopulation;

            // Save statistics to file
            statistics.addGenerationStatistic(population);
        }

        // Reconstruct best individual
        GSGPIndividual reconstructedInd = reconstructIndividual(population.get(0), initialInds, properties, expData);

        // Save best individual's statistics to file and also stops the clock
        statistics.finishEvolution(reconstructedInd, ((GSGPIndividual) population.getBestIndividual()).getNumNodes().toString());


        /******* EXTRA DATA *******

        // Print equivalent trees' size for comparison
        System.out.println("Best Individual Size: " + ((GSGPIndividual) population.getBestIndividual()).getNumNodes());
        System.out.println("Reconstruction Size: " + reconstructedInd.getNumNodes() + "\n");;

        // Print sizes in scientific notation
        NumberFormat formatter = new DecimalFormat("0.###E0");

        // Print trees' features
        System.out.println("Best Individual Size: " + formatter.format(((GSGPIndividual) population.getBestIndividual()).getNumNodes()));
        System.out.println("Best Individual TR Fitness: " + population.getBestIndividual().getTrainingFitnessAsString());
        System.out.println("Best Individual TS Fitness: " + population.getBestIndividual().getTestFitnessAsString());
        System.out.println("---------------------------------------------");
        System.out.println("Reconstruction Size: " + formatter.format(reconstructedInd.getTree().getNumNodes()));
        System.out.println("Reconstruction TR Fitness: " + reconstructedInd.getTrainingFitnessAsString());
        System.out.println("Reconstruction TS Fitness: " + reconstructedInd.getTestFitnessAsString() + "\n");

        // Print reconstructed tree representations
        System.out.println(reconstructedTree);
        System.out.println(((GSGPIndividual) population.getBestIndividual()).getReprCoef() + "\n");

        **************************/
    }


    /**
     * Get statistics for current GSGP instance.
     *
     * @return
     */
    public Statistics getStatistics() {
        return statistics;
    }


    /**
     * Reconstruct an equivalent tree based in the individual's coefficient representation.
     *
     * @param individual
     * @param initialPop
     * @param properties
     * @param expData
     * @return
     */
    public GSGPIndividual reconstructIndividual(Individual individual, Map initialPop, PropertiesManager properties, ExperimentalData expData) {
        // Maps of coefficients related to each subtree
        HashMap<Integer, Double> reprCoef = (HashMap<Integer, Double>) ((GSGPIndividual) individual).getReprCoef();

        // Root node
        Add root = new Add();

        Function current = root;

        int i = 0;
        int mapSize = reprCoef.size();  //The case where size is equal 1 is impossible

        // Iterate over subtrees
        for(Map.Entry<Integer, Double> entry : reprCoef.entrySet()) {
            i += 1;

            // Multiplication to apply coefficient to subtree
            Mul applyCoef = new Mul();

            // Include coefficient as a subnode of the multiplication
            applyCoef.addNode(new ERC(entry.getValue()),0);

            // Get and attach subtree root node
            Individual subInd = (Individual) initialPop.get(entry.getKey());
            if(subInd == null) {  // Subtree is a mutation mask
                applyCoef.addNode((Node) properties.mutationMasks.get(entry.getKey()), 1);
            }
            else {  // Subtree is a parent individual
                applyCoef.addNode(subInd.getTree(), 1);
            }

            // Last element has been reached
            if(i == mapSize) {
                current.addNode(applyCoef, 1);
                break;
            }

            // Attach new term to the main tree
            current.addNode(applyCoef, 0);

            // Next element is the last, don't append another Add Function into the tree
            if(i + 1 == mapSize) {
                continue;
            }

            // Addition operation necessary to continue the linear combination of functions
            Add nextAdd = new Add();
            current.addNode(nextAdd, 1);

            current = nextAdd;
        }

        Fitness fitnessFunction = evaluateFitness(root, properties, expData);

        return new GSGPIndividual(root, BigInteger.valueOf(root.getNumNodes()), fitnessFunction);
    }


    /**
     * Method to evaluate the fitness of a tree beginning at this node.
     *
     * @param tree
     * @param properties
     * @param expData
     * @return
     */
    public Fitness evaluateFitness(Node tree, PropertiesManager properties, ExperimentalData expData){
        Fitness fitnessFunction = properties.getFitnessFunction();

        // Compute the (training/test) semantics of generated random tree
        for(Utils.DatasetType dataType : Utils.DatasetType.values()){
            fitnessFunction.resetFitness(dataType, expData);
            Dataset dataset = expData.getDataset(dataType);

            int instanceIndex = 0;
            for (Instance instance : dataset) {
                double estimated = tree.eval(instance.input);
                fitnessFunction.setSemanticsAtIndex(estimated, instance.output, instanceIndex++, dataType);
            }

            fitnessFunction.computeFitness(dataType);
        }
        return fitnessFunction;
    }
}