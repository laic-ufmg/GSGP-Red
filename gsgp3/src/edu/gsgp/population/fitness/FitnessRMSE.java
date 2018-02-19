/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population.fitness;

import edu.gsgp.Utils.DatasetType;
import edu.gsgp.data.ExperimentalData;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 2014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */

public class FitnessRMSE extends Fitness{    
    private double rmseTr;
    private double rmseTs;

    public FitnessRMSE() {
    }

    public FitnessRMSE(double rmseTr, double rmseTs) {
        this.rmseTr = rmseTr;
        this.rmseTs = rmseTs;
    }
    
    public void setRMSE(double rmse, DatasetType dataType) {
        if(dataType == DatasetType.TRAINING)
            rmseTr = rmse;
        else
            rmseTs = rmse;
    }

    public double getRMSE(DatasetType dataType){
        if(dataType == DatasetType.TRAINING)
            return rmseTr;
        return rmseTs;
    }
    
    /** Control variables used during fitness calculation. **/
    // Variable to store the sum of squared errors (to compute the RMSE).
    private double ctrSumSquareError;

    @Override
    public void resetFitness(DatasetType dataType, ExperimentalData datasets){
        ctrSumSquareError = 0;
        setSemantics(datasets.getDataset(dataType).size(), dataType);
    }
    
    @Override
    public void setSemanticsAtIndex(double estimated, double desired, int index, DatasetType dataType){
        getSemantics(dataType)[index] = estimated;
        double error = estimated - desired;
        ctrSumSquareError += error * error;
    }
    
    @Override
    public void computeFitness(DatasetType dataType){
        double rmse = Math.sqrt(ctrSumSquareError /getSemantics(dataType).length);
        setRMSE(rmse, dataType);
    }

    @Override
    public Fitness softClone() {
        return new FitnessRMSE();
    }

    @Override
    public Fitness clone() {
        return new FitnessRMSE(rmseTr, rmseTs);
    }

    @Override
    public double getTrainingFitness() {
        return rmseTr;
    }

    @Override
    public double getTestFitness(){
        return rmseTs;
    }
    
    @Override
    public double getComparableValue() {
        return getRMSE(DatasetType.TRAINING);
    }
}