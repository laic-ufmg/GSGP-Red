/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population.fitness;

import edu.gsgp.Utils.DatasetType;
import edu.gsgp.data.ExperimentalData;
import java.math.BigInteger;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 2014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */

public abstract class Fitness{
    protected double[] semanticsTr;
    protected double[] semanticsTs;
    

    public Fitness() {
    }

    /**
     * Return the semantics relative to the training or test data
     * @param dataType What semantics to return (relative to training or test)
     * @return The semantics
     */
    public final double[] getSemantics(DatasetType dataType){
        if(dataType == DatasetType.TRAINING)
            return semanticsTr;
        else
            return semanticsTs;
    }
    
    /**
     * Set the the semantics relative to the training or test data
     * @param semantics The new semantics
     * @param dataType What semantics to set (relative to training or test)
     */
    public final void setSemantics(double[] semantics, DatasetType dataType) {
        if(dataType == DatasetType.TRAINING)
            semanticsTr = semantics;
        else
            semanticsTs = semantics;
    }
    
    /**
     * Set the the semantics with an empty array with the size given. 
     * @param size The size of the new array
     * @param dataType What semantics to set (training or test)
     */
    public final void setSemantics(int size, DatasetType dataType) {
        if(dataType == DatasetType.TRAINING)
            semanticsTr = new double[size];
        else
            semanticsTs = new double[size];
    }

    /**
     * Before starting to compute the fitness function, its variables must be
     * (re)initialized.
     * @param dataType Inidicate if the fitness refer to training or test set
     * @param datasets Training and test data in an ExperimentalData object
     */
    public abstract void resetFitness(DatasetType dataType, ExperimentalData datasets);
    public abstract void setSemanticsAtIndex(double estimated, double desired, int index, DatasetType dataType);
    public abstract void computeFitness(DatasetType dataType);
    
    public abstract Fitness softClone();
    public abstract Fitness clone();
    public abstract double getTrainingFitness();
    public abstract double getTestFitness();
    public abstract double getComparableValue();
}
