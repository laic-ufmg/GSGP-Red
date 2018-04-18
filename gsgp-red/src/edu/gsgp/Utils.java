/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp;

import edu.gsgp.data.Instance;
import edu.gsgp.data.Dataset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import edu.gsgp.nodes.Node;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 2014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class Utils {    
    public enum DatasetType{ TRAINING, TEST };
    private static final DecimalFormat df = new DecimalFormat("0.00000", new DecimalFormatSymbols(Locale.ENGLISH));
    
    /**
     * Generates an array with a number of folds defined by the user. Each fold
     * contains dataset.size() / numFolds instances.
     * @param numFolds Number of folds
     * @param dataset Input datase
     * @param rnd Random number generator
     * @return An array of folds
     */
    public static Dataset[] getFoldSampling(int numFolds, Dataset dataset, MersenneTwister rnd){
        Dataset[] folds = new Dataset[numFolds];
        ArrayList<Instance> dataCopy = dataset.softClone();
        int foldIndex = 0;
        if(rnd == null){
            Iterator<Instance> it = dataCopy.iterator();
            while(it.hasNext()){
                if(folds[foldIndex] == null)
                    folds[foldIndex] = new Dataset();
                folds[foldIndex].add(it.next());
                it.remove();
                if(foldIndex < numFolds - 1) foldIndex++;
                else foldIndex = 0;
            }
        }
        else{
            while(!dataCopy.isEmpty()){
                if(folds[foldIndex] == null)
                    folds[foldIndex] = new Dataset();
                folds[foldIndex].add(dataCopy.remove(rnd.nextInt(dataCopy.size())));
                if(foldIndex < numFolds - 1) foldIndex++;
                else foldIndex = 0;
            }
        }
        return folds;
    }
    
    public static double sigmoid(double x){
        return 1/(1+Math.exp(-x));
    }
    
    public static double[] getSemantics(Dataset training, Node f){
        double[] newSemantic = new double[training.size()];
        int i = 0;
        for(Instance inst : training){
            newSemantic[i++] = f.eval(inst.input);
        }
        return newSemantic;
    }
    
    public static double getMedian(double[] array) {
        double[] auxArray = Arrays.copyOf(array, array.length);
        Arrays.sort(auxArray);
        // Even number
        if(auxArray.length % 2 == 0){
            int secondElement = auxArray.length / 2;
            return (auxArray[secondElement-1]+auxArray[secondElement])/2;
        }
        else{
            int element = (auxArray.length-1)/2;
            return auxArray[element];
        }
    }
    
    public static double getMean(double[] data){
        double sum = 0;
        for(int i = 0; i < data.length; i++){
            sum += data[i];
        }
        return sum / data.length; 
    }
    
    public static double getSD(double[] data, double mean){
        double sum = 0;
        for(int i = 0; i < data.length; i++){
            sum += (data[i]-mean) * (data[i]-mean);
        }
        return Math.sqrt(sum/(data.length-1));
    }
    
    public static double getMedian(long[] array) {
        long[] auxArray = Arrays.copyOf(array, array.length);
        Arrays.sort(auxArray);
        // Even number
        if(auxArray.length % 2 == 0){
            int secondElement = auxArray.length / 2;
            return (auxArray[secondElement-1]+auxArray[secondElement])/2;
        }
        else{
            int element = (auxArray.length-1)/2;
            return auxArray[element];
        }
    }
    
    public static double getIQR(double[] array){
        DescriptiveStatistics da = new DescriptiveStatistics(array);
        return da.getPercentile(75) - da.getPercentile(25);
    }
    
    public static double getCanberraDistance(double[] a, double[] b){
        try{
            if(a == null || b == null)
                throw new MyException(new Utils(), "CanberraDistance: null imput.");
            if(a.length != b.length)
                throw new MyException(new Utils(), "CanberraDistance: inputs with different lengths.");
            double canbDist = 0;
            for(int i = 0; i < a.length; i++){
                double den = Math.abs(a[i]) + Math.abs(b[i]);
                if(den != 0)
                    canbDist += Math.abs(a[i]-b[i])/den;
            }
            return canbDist/a.length;
        }
        catch(MyException e){
            e.printStackTrace();
        }
        return -1;
    }
    
    /**
     * Generate a String from a double value formated with the default number of decimal places
     * @param value The double value to be formated
     * @return The formated String
     */
    public static String format(double value){
        return df.format(value);
    }
}
