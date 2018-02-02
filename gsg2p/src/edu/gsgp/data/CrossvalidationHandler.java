/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gsgp.data;

import edu.gsgp.MersenneTwister;
import java.io.File;
import java.util.ArrayList;
import edu.gsgp.Utils;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 2014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class CrossvalidationHandler implements DataProducer{
    /** Curretn fold. Used to return a training/test set.*/
    private int currentFold;
    
    /** Array of folds, used for complete a crossvalidation. */
    private Dataset[] folds;
    
    /** Random number generator. */
    private MersenneTwister rnd;
    
    /** Number of folds. */
    private int numFolds;
    
    /** Input dataset. */
    private Dataset dataset;
    
    /** Boolean to indicates if we should sample or read the folds from files **/
//    private boolean useFiles = false;
    
    private int numInputs;

    public CrossvalidationHandler(int numFolds) {
        this.numFolds = numFolds;
        currentFold = 0;
    }
    
    public CrossvalidationHandler(){
//        useFiles = true;
        currentFold = 0;
    }
    
    /**
     * Sample partitions for cross validation. Not stratified.
     */
    private void resampleFolds() {
        folds = Utils.getFoldSampling(numFolds, dataset, rnd);
    }
    
    /**
     * Get a training/test set based on cross validation. Make one partition as test and remaining as training. 
     * @return Array with training and test data in first and second positions, respectvely
     */
    @Override
    public ExperimentalData getExperimentDataset(){
//        if(currentFold == 0 && !useFiles){
//            resampleFolds();
//        }
        ExperimentalData data = new ExperimentalData();
        for(int i = 0; i < numFolds; i++){
            if(i == currentFold)
                data.setDataset(folds[i].softClone(), Utils.DatasetType.TEST);
            else{
                data.setDataset(folds[i].softClone(), Utils.DatasetType.TRAINING);
            }
        }
        if(currentFold < numFolds - 1) currentFold++;
        else currentFold = 0;
        return data;
    }

    @Override
    public void setDataset(String trainingPath, String testPath) throws Exception{
//        if(!useFiles){
//            Dataset data = DataReader.readInputDataFile(dataPath);
//            this.dataset = data;
//            numInputs = data.getInputNumber();
//        }
//        else{
            getFoldsFromFile(trainingPath);
//        }
    }

    @Override
    public void setRandomGenerator(MersenneTwister rnd) {
        this.rnd = rnd;
    }

    @Override
    public boolean isValid() {
        if(numFolds > 1)
            return true;
        return false;
    }

    /**
     * Get the folds from a list of files named prename#posname, where # is the 
     * index for the fold inside the file. 
     * @param dataPath Full path to the file with the pattern prename#posname.
     * @return The number of inputs of the dataset
     * @throws Exception Error while reading the dataset within a file.
     * @throws SGPException Error in the file path/pattern.
     */
    private void getFoldsFromFile(String dataPath) throws Exception{
        int lastFileSeparator = dataPath.lastIndexOf(File.separator);
        String filePattern = dataPath.substring(lastFileSeparator + 1);
        String folderName = dataPath.substring(0, lastFileSeparator);
        String[] aux = filePattern.split("#");
        if(aux.length != 2)
            throw new Exception("The file pattern must have one and only one # symbol as fold index.");
        ArrayList<File> files = new ArrayList<File>();
        int index = 0;
        File newFold = new File(folderName + File.separator + aux[0] + index + aux[1]);
        while(newFold.isFile()){
            files.add(newFold);
            index++;
            newFold = new File(folderName + File.separator + aux[0] + index + aux[1]);
        }
        if(files.isEmpty()) 
            throw new Exception("No files found for this file pattern/path: \"" + newFold.getAbsolutePath() + "\"\nUsing CROSSVALIDATION.\n");
        numFolds = files.size();
        folds = new Dataset[numFolds];
        for(int i = 0; i < numFolds; i++){
            folds[i] = DataReader.readInputDataFile(files.get(i));
        }
        numInputs = folds[0].getInputNumber();
    }

    @Override
    public int getNumInputs() {
        return numInputs;
    }
}
