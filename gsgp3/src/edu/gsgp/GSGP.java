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
import sun.security.util.BigInt;

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

        Individual bestIndividual = population.getBestIndividual();

        String originalSize = bestIndividual.getNumNodes().toString();

        // Uncomment when printing extra data
//        GSGPIndividual originalIndividual = ((GSGPIndividual) bestIndividual).clone();

        // Reconstruct best individual
        ((GSGPIndividual) bestIndividual).reconstructIndividual();
        bestIndividual.evaluateFitness(expData);

        // Save best individual's statistics to file and also stops the clock
        statistics.finishEvolution(bestIndividual, originalSize);

        /******* EXTRA DATA *******

        // Print equivalent trees' size for comparison
        System.out.println("Best Individual Size: " + originalSize);
        System.out.println("Reconstruction Size: " + bestIndividual.getNumNodes() + "\n");;

        // Print sizes in scientific notation
        NumberFormat formatter = new DecimalFormat("0.###E0");

        // Print trees' features
        System.out.println("Best Individual Size: " + formatter.format(originalIndividual.getNumNodes()));
        System.out.println("Best Individual TR Fitness: " + originalIndividual.getTrainingFitnessAsString());
        System.out.println("Best Individual TS Fitness: " + originalIndividual.getTestFitnessAsString());
        System.out.println("---------------------------------------------");
        System.out.println("Reconstruction Size: " + formatter.format(bestIndividual.getNumNodes()));
        System.out.println("Reconstruction TR Fitness: " + bestIndividual.getTrainingFitnessAsString());
        System.out.println("Reconstruction TS Fitness: " + bestIndividual.getTestFitnessAsString() + "\n");

        // Print reconstructed tree representations
        System.out.println(reconstructedTree);
        System.out.println(((GSGPIndividual) population.getBestIndividual()).getCombinationRepr() + "\n");

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


}