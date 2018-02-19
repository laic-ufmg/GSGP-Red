/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gsgp.data;

import edu.gsgp.MersenneTwister;


/**
 * DataProducer.java
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 2014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public interface DataProducer {
    /**
     * Returns an array with trainin and test set, in this order
     * @return Two positions array
     */
    public ExperimentalData getExperimentDataset();
    
    /**
     * Sets a dataset path
     * @param trainingPath Path for the (training) dataset used
     * @param testPath Path for the (test) dataset used
     * @throws java.lang.Exception Exception caused when reading dataset file(s)
     */
    public void setDataset(String trainingPath, String testPath) throws Exception;
    
    /**
     * Sets the random number generator
     * @param rnd A MersenneTwisterFast objetct, used by ECJ
     */
    public void setRandomGenerator(MersenneTwister rnd);
    
    /**
     * Check if parameter ranges are correct.
     * @return True if everything is ok.
     */
    public boolean isValid();
    
    public int getNumInputs();
}
