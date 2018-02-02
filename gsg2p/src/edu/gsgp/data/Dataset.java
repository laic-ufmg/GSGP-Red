/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gsgp.data;

import java.util.ArrayList;
import edu.gsgp.Utils;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 2014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class Dataset extends ArrayList<Instance>{   
    private double[] outputVector;
    // Outputs standard deviation
    private Double outputSD;
    
    /**
     * Constructor declaration
     */
    public Dataset() {
        super();
    }

    /**
     * Constructor declaration
     * @param dataset Dataset copied to the new one.
     */
    public Dataset(Dataset dataset) {
        super(dataset);
    }
    
    /**
     * Creates a new instance and add it to the dataset.
     * @param input Instance input
     * @param output Instance output
     */
    public void add(double[] input, Double output){
        Instance newInstance = new Instance(input, output);
        this.add(newInstance);
    }
    
    /**
     * Soft clones this object, copying the references to data instances to a 
     * new ArrayList. 
     * @return A Dataset pointing to the instances of this one.
     */
    public Dataset softClone() {
        Dataset newDataset = new Dataset();
        newDataset.addAll(this);
        return newDataset;
    }
    
    /**
     * Return the number of inputs of the dataset.
     * @return The number of inputs.
     */
    public int getInputNumber(){
        return get(0).input.length;
    }

    @Override
    public String toString() {
        return size() + ""; 
    }

    public double getOutputSD() {
        if(outputSD == null){
            double mean = 0;
            double[] outputs = new double[size()];
            for(int i = 0; i < size(); i ++){
                double tmp = get(i).output;
                outputs[i] = tmp;
                mean += tmp;
            }
            outputSD = Utils.getSD(outputs, mean/size());
        }
        return outputSD;
    }

    public double[] getOutputs() {
        if(outputVector == null){
            setOutputs();
        }
        return outputVector;
    }
    
    private void setOutputs(){
        outputVector = new double[size()];
        for(int i = 0; i < size(); i++){
            outputVector[i] = get(i).output;
        }
    }
    
    /** Used to calculate store the total sum of squares. **/
    private Double ssTotal = null;
    
    /**
     * Compute SStot for the coefficient of determination: 1-(SSres/SStot),
     * where SSres is the residual sum of squares and SStot is total sum of 
     * squares.
     * @return The total sum of squares
     */
    public double getSStotal(){
        // SStot is computed only one time and stored in ssTotal
        if(ssTotal == null){
            double meanOutput = 0;
            ssTotal = new Double(0);
            if(outputVector == null) setOutputs();
            for(int i = 0; i < outputVector.length; i++) meanOutput += outputVector[i];
            meanOutput /= outputVector.length;            
            for(int i = 0; i < outputVector.length; i++){
                double tmp = outputVector[i] - meanOutput;
                ssTotal += tmp * tmp;
            }
        }
        return ssTotal;
    }
}

