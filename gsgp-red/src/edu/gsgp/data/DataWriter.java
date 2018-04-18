/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.data;

import edu.gsgp.Statistics;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import edu.gsgp.Statistics.StatsType;
import java.io.IOException;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 2014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */

public class DataWriter {
    public static void writeResults(String outputPath,
                                    String outputPrefix, 
                                    Statistics statistic,
                                    int experimentId) throws Exception{

        StatsType writeableStats[] = {StatsType.BEST_OF_GEN_SIZE,
                                      StatsType.SEMANTICS,
                                      StatsType.BEST_OF_GEN_TR_FIT,
                                      StatsType.BEST_OF_GEN_TS_FIT,
                                      StatsType.ELAPSED_TIME,
                                      StatsType.SIZE_REDUCTION};

        for(StatsType type : writeableStats){
            writeOnFile(outputPath, outputPrefix, 
                    experimentId + "," + statistic.asWritableString(type) + "\n", type);
        }
    }


    public static void writeLoadedParameters(PropertiesManager parameters) throws Exception{
        writeOnFile(parameters.getOutputDir(),
                   parameters.getFilePrefix(),
                   parameters.getLoadedParametersString(), 
                   StatsType.LOADED_PARAMETERS);
    }


    public static void writeOutputs(String outputPath,
                                    String outputPrefix, 
                                    Statistics[] statsArray,
                                    ExperimentalData data) throws Exception{
        File outputDir = getOutputDir(outputPath);
        outputDir = new File(outputDir.getAbsolutePath()+ File.separator + outputPrefix);
        outputDir.mkdirs();
        BufferedWriter bw;
        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ File.separator + "outputs.csv"));
//        bw.write(getDesiredOutputs(data));
        bw.write(getStatisticsFromArray(statsArray, StatsType.SEMANTICS));
        bw.close();
    }


    /**
     * Write some information to the output file
     * @param outputPath Path to the directory where the output will be written
     * @param outputPrefix Name of the directory where the files will be written
     * @param info Information to be written
     * @param statsType Type of information
     * @throws NullPointerException The pathname was not found
     * @throws SecurityException If a required system property value cannot be accessed
     * @throws IOException If the file exists but is a directory rather than a regular 
     * file, does not exist but cannot be created, or cannot be opened for any other reason
     */
    private static void writeOnFile(String outputPath,
                                   String outputPrefix, 
                                   String info,
                                   StatsType statsType) throws NullPointerException, SecurityException, IOException{
        File outputDir = getOutputDir(outputPath);
        outputDir = new File(outputDir.getAbsolutePath()+ File.separator + outputPrefix);
        outputDir.mkdirs();
        // Object to write results on file
        BufferedWriter bw;
        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ File.separator + statsType.getPath(), true));
        bw.write(info);
        bw.close();
    }    


    /**
     * Selects the path to save output data.
     * @param outputPath Path to a directory to write the output data
     * @return File object pointing to the output directory
     */
    protected static File getOutputDir(String outputPath){
        File outputDir;
        if(!outputPath.equals("")){
            outputDir = new File(outputPath);
        }
        else{
            outputDir = new File(System.getProperty("user.dir"));
        }  
        return outputDir;
    }


    protected static String getStatisticsFromArray(Statistics[] statsArray, Statistics.StatsType type) {
        StringBuilder str = new StringBuilder();
        for(Statistics stats : statsArray){
            str.append(stats.asWritableString(type) + "\n");
        }
        return str.toString();
    }
}
