package edu.gsgp.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 2014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class DataReader {   
    /**
     * Reads a dataset file.
     * @param inputFile Input file
     * @return A dataset object
     * @throws SGPException Error while reading input file
     * @throws Exception Error while reading the file
     */
    public static Dataset readInputDataFile(File inputFile) throws Exception{
        BufferedReader br = null;
        String currentLine;
        Dataset data = new Dataset();
        try {
            br = new BufferedReader(new FileReader(inputFile));
            // Reads each line from the file
            while ((currentLine = br.readLine()) != null) {
                // Checks for comment or empty line
                if(!currentLine.matches("^\\s*#.*") && currentLine.matches(".*\\S+.*")){
                    currentLine = currentLine.replaceAll("\\s*", "");
                    if(!currentLine.equals("")){
                        String[] s_inputOutput = currentLine.split(",");
                        double[] in = new double[s_inputOutput.length-1];
                        for(int i = 0; i < s_inputOutput.length-1; i++){
                            in[i] = Double.parseDouble(s_inputOutput[i]);
                        }
                        double out =  Double.parseDouble(s_inputOutput[s_inputOutput.length-1]);
                        data.add(in, out);
                    }
                }
                
            }
        } 
        catch (NumberFormatException e){
            throw new Exception("Some tokens in the input data file were not numbers.");
        }
        catch(ArrayIndexOutOfBoundsException e){
            throw new Exception("Not enough data points in file.");
        }
        catch(FileNotFoundException e){
            throw new Exception("Input file not found: " + e.getLocalizedMessage());
        }
        catch(IOException e){
            throw new Exception("IO Error reading input file.");
        }
        finally {
            try {
                if (br != null)br.close();
            }
            catch (IOException ex) {}
        }
        return data;
    }
    
    /**
     * Reads a dataset file.
     * @param filePath Path to the input file
     * @return A dataset object
     * @throws Exception Error while reading the file
     */
    public static Dataset readInputDataFile(String filePath) throws Exception{
        File inputFile = new File(filePath);
        return readInputDataFile(inputFile);
    }
}
