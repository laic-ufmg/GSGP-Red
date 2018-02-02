/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp;

import edu.gsgp.data.ExperimentalData;
import edu.gsgp.population.GSGPIndividual;
import edu.gsgp.population.Population;
import edu.gsgp.population.Individual;
import sun.security.util.BigInt;

import java.math.BigInteger;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 2014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */

public class Statistics {
    
    public enum StatsType{
        BEST_OF_GEN_SIZE("individualSize.csv"), 
        SEMANTICS("outputs.csv"),
        BEST_OF_GEN_TS_FIT("tsFitness.csv"), 
        BEST_OF_GEN_TR_FIT("trFitness.csv"),
        ELAPSED_TIME("elapsedTime.csv"),
        SIZE_REDUCTION("sizeReduction.csv"),
        LOADED_PARAMETERS("loadedParams.txt");
        
        private final String filePath;

        private StatsType(String filePath) {
            this.filePath = filePath;
        }
        
        public String getPath(){
            return filePath;
        }
    }


    protected ExperimentalData expData;

    protected String[] bestOfGenSize;
    protected String[] bestOfGenTsFitness;
    protected String[] bestOfGenTrFitness;

    private double[] bestTrainingSemantics;
    private double[] bestTestSemantics;

    protected float elapsedTime;
    protected String bestOriginalSize;
    protected String bestReducedSize;

    protected int currentGeneration;



    public Statistics(int numGenerations, ExperimentalData expData) {
        bestOfGenSize = new String[numGenerations + 1];
        bestOfGenTrFitness = new String[numGenerations + 1];
        bestOfGenTsFitness = new String[numGenerations + 1];
        currentGeneration = 0;
        this.expData = expData;
    }


    /**
     * Update the statistics with information obtained in the end of the generation
     * @param pop Current population
     */
    public void addGenerationStatistic(Population pop){        
        // In order to not add the time used by this method, we subtract it
        long methodTime = System.nanoTime();
        
        Individual bestOfGen = pop.getBestIndividual();
       
        bestOfGenSize[currentGeneration] = bestOfGen.getNumNodesAsString();
        bestOfGenTrFitness[currentGeneration] = bestOfGen.getTrainingFitnessAsString();
        // In GSG2P the test fitness doesn't need to be calculated for every individual, only for the best
        bestOfGenTsFitness[currentGeneration] = "0.00000";
        
//        System.out.println("Best of Gen (RMSE-TR/RMSE-TS/nodes: " + bestOfGenTrFitness[currentGeneration] +
//                           "/" + bestOfGenTsFitness[currentGeneration] + "/" + bestOfGenSize[currentGeneration]);

        currentGeneration++;
        
        // Ignore the time elapsed to store the statistics
        elapsedTime += System.nanoTime() - methodTime;
    }


    public void finishEvolution(Individual bestIndividual, String originalSize) {
        elapsedTime = System.nanoTime() - elapsedTime;

        // Convert nanosecs to secs
        elapsedTime /= 1000000000;

        bestTrainingSemantics = bestIndividual.getTrainingSemantics();
        bestTestSemantics = ((GSGPIndividual)bestIndividual).getTestSemantics();

        bestOfGenTrFitness[bestOfGenTrFitness.length - 1] = bestIndividual.getTrainingFitnessAsString();
        bestOfGenTsFitness[bestOfGenTsFitness.length - 1] = bestIndividual.getTestFitnessAsString();

        bestOriginalSize = originalSize;
        bestReducedSize = ((GSGPIndividual) bestIndividual).getNumNodesAsString();
    }


    public String asWritableString(StatsType type) {
        switch(type){
            case BEST_OF_GEN_SIZE:
                return concatenateArray(bestOfGenSize);
            case SEMANTICS:
                return getSemanticsAsString();
            case BEST_OF_GEN_TR_FIT:
                return concatenateArray(bestOfGenTrFitness);
            case BEST_OF_GEN_TS_FIT:
                return concatenateArray(bestOfGenTsFitness);
            case ELAPSED_TIME:
                return String.valueOf(elapsedTime);
            case SIZE_REDUCTION:
                return bestOriginalSize + "," + bestReducedSize;
            default:
                return null;
        }
    }


    private String concatenateArray(String[] stringArray){
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < stringArray.length-1; i++){
            str.append(stringArray[i] + ",");
        }
        str.append(stringArray[stringArray.length-1]);        
        return str.toString();
    }


    private String concatenateFloatArray(float[] floatArray) {
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < floatArray.length-1; i++){
            str.append(Utils.format(floatArray[i]) + ",");
        }
        str.append(Utils.format(floatArray[floatArray.length-1]));        
        return str.toString();
    }


    private String getSemanticsAsString() {
        StringBuffer str = new StringBuffer();
        
        for(int i = 0; i < bestTrainingSemantics.length; i++){
            str.append(bestTrainingSemantics[i] + ",");
        }
        
        String sep = "";
        
        return str.toString();
    }


    public void startClock(){
        elapsedTime = System.nanoTime();
    }
    
}
