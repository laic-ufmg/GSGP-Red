/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gsgp.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import edu.gsgp.MersenneTwister;
import edu.gsgp.Utils.DatasetType;
import edu.gsgp.population.Population;
import edu.gsgp.nodes.Node;
import edu.gsgp.nodes.functions.Function;
import edu.gsgp.nodes.terminals.Input;
import edu.gsgp.nodes.terminals.Terminal;
import edu.gsgp.population.treebuilder.FullBuilder;
import edu.gsgp.population.treebuilder.GrowBuilder;
import edu.gsgp.population.treebuilder.HalfBuilder;
import edu.gsgp.population.Individual;
import edu.gsgp.population.operator.Breeder;
import edu.gsgp.population.populator.Populator;
import edu.gsgp.population.treebuilder.TreeBuilder;
import edu.gsgp.population.fitness.Fitness;
import edu.gsgp.population.pipeline.Pipeline;
import edu.gsgp.population.selector.IndividualSelector;
import edu.gsgp.population.selector.TournamentSelector;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * 
 * Copyright (C) 2014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 * 
 * This class load the parameters from the file and instantiate the objects used 
 * in the GSGP
 */
public class PropertiesManager {
    public enum paramType{
        STRING, INT, DOUBLE, CLASS, CLASS_LIST, FILE
    }
    
    protected boolean parameterLoaded;
    
    protected Properties fileParameters;
    protected CommandLine cliParameters;

    protected Options cliOptions;
    
    private DataProducer dataProducer;
    private MersenneTwister mersennePRNG;
    private ExperimentalData data;
    private boolean mutStepFromSD;
    private int numExperiments;
    private int numGenerations;
    private int numThreads;
    private int populationSize;
    private int rtPoolSize;
    
    private int maxInitAttempts;
    
    private double minError;
    private double mutationStep;
    private double spreaderInitProb;
    private double spreaderAlpha;

    private Populator populationInitializer;
    private Pipeline pipeline;
    private Breeder[] breederList;
    private Fitness fitnessFunction;
    private Terminal[] terminalSet;
    private Function[] functionSet;
    private ExperimentalData experimentalData;
    
    private IndividualSelector individualSelector;
    
    private TreeBuilder individualBuilder;
    private TreeBuilder randomTreeBuilder;
    
    private String outputDir;
    private String filePrefix;
    
    // Used do double check the parameters loaded/used by the experiment
    private StringBuilder loadedParametersLog;

    // Saves the trees generated in mutation before we implement the pool of trees
    public Map<Integer, Node> mutationMasks;

    public PropertiesManager(String args[]) throws Exception{
        loadedParametersLog = new StringBuilder();

        mutationMasks = new HashMap<>();

        setOptions();
        parameterLoaded = loadParameterFile(args);
        if(parameterLoaded)
            loadParameters();        
    }
    
    private void loadParameters() throws Exception{
        dataProducer = getDataProducer();
        mersennePRNG = new MersenneTwister(getLongProperty(ParameterList.SEED, System.currentTimeMillis()));
        terminalSet = getTerminalObjects();
        functionSet = getFunctionObjects();
        fitnessFunction = getFitnessObject();
        numExperiments = getIntegerProperty(ParameterList.NUM_REPETITIONS, 1);
        numGenerations = getIntegerProperty(ParameterList.NUM_GENERATION, 200);
        
        numThreads = getIntegerProperty(ParameterList.NUMBER_THREADS, -1);
        if(numThreads == -1) numThreads = Runtime.getRuntime().availableProcessors();
        
        populationSize = getIntegerProperty(ParameterList.POP_SIZE, 1000);
        rtPoolSize = getIntegerProperty(ParameterList.RT_POOL_SIZE, 200);
        
        maxInitAttempts = getIntegerProperty(ParameterList.POP_INIT_ATTEMPTS, 10);
        
        minError = getDoubleProperty(ParameterList.MIN_ERROR, 0);
        mutationStep = getDoubleProperty(ParameterList.MUT_STEP, 0.1);
        mutStepFromSD = getBooleanProperty(ParameterList.MUT_STEP_SD, true);
        
        spreaderInitProb = getDoubleProperty(ParameterList.SPREADER_PROB, 0.5);
        spreaderAlpha = getDoubleProperty(ParameterList.SPREADER_ALPHA, 2);
        
        outputDir = getStringProperty(ParameterList.PATH_OUTPUT_DIR, true);
        filePrefix = getStringProperty(ParameterList.FILE_PREFIX, false);
        
        individualBuilder = getIndividualBuilder(false);
        randomTreeBuilder = getIndividualBuilder(true);
        pipeline = getPipelineObject();
        populationInitializer = getPopInitObject();
        breederList = getBreederObjects();
        
        individualSelector = getIndividualSelector();        
    }

    public enum ParameterList {
        PARENT_FILE("parent", "Path to the parent parameter file. The child parameters overwrite the parent", false),
        PATH_DATA_FILE("experiment.data", "Path for the training/test files. See experiment.sampling option for more details", true),
        PATH_TEST_FILE("experiment.data.test", "Path for the test files. See experiment.sampling option for more details", false),
        PATH_OUTPUT_DIR("experiment.output.dir", "Output directory", false),
        SEED("experiment.seed", "Seed (long int) used by the pseudo-random number generator", false),
        FILE_PREFIX("experiment.file.prefix", "Identifier prefix for files", false),
        TERMINAL_LIST("tree.build.terminals", "List of terminals used to build new trees (separeted by commas)", true),
        FUNCTION_LIST("tree.build.functions", "List of functions used to build new trees (separeted by commas)", true),
        FITNESS_FUNCTION("pop.fitness", "Fitness function adopted during the evolution", true),
        INDIVIDUAL_BUILDER_POP("tree.build.builder", "Builder used to generate trees for the initial population", true),
        INDIVIDUAL_BUILDER_RAND_TREE("tree.build.builder.random.tree", "Builder used to generate random trees for the semantic operators", true),
        INDIVIDUAL_SELECTOR("pop.ind.selector", "Type of selector used to select individuals for next generations", true),
        
        EXPERIMENT_DESIGN("experiment.design", "Type of experiment (cross-validation or holdout):"
                                                + "\n# - If crossvalidation, uses splited data from a list of files. Use paths to the"
                                                + "\n# files in the form /pathToFile/repeatedName#repeatedName, where # indicates "
                                                + "\n# where the fold index is placed (a number from 0 to k-1). E.g. /home/iris-#.dat,"
                                                + "\n# with 3 folds in the path will look for iris-0.dat, iris-1.dat and iris-2.dat"
                                                + "\n# - If holdout, we have two cases: "
                                                + "\n#   1) Use paths to the files in the form /pathToFile/repeatedName#repeatedName,"
                                                + "\n#      where # is composed by the pattern (train|test)-i with i=0,1,...,n-1, where n is"
                                                + "\n#      the number of experiment files. E.g. /home/iris-#.dat, with 4 files (2x(train+test))"
                                                + "\n#      in the path will look for iris-train-0.dat, iris-test-0.dat, iris-train-1.dat and iris-test-1.dat"
                                                + "\n#   2) Use the path in 'experiment.data' to read the training files in the format "
                                                + "\n#      /pathToFile/repeatedName#repeatedName and the path in 'experiment.data.test'"
                                                + "\n#      to read the test data, replacing # by i=0,1,...,n-1. This option is used when"
                                                + "\n#      'experiment.data.test' is provided", true),
        
        MAX_TREE_DEPTH("tree.build.max.depth", "Max depth allowed when building trees", false),
        MIN_TREE_DEPTH("tree.min.depth", "Min depth allowed when building trees", false),
        NUM_GENERATION("evol.num.generation", "Number of generations", false),
        NUM_REPETITIONS("experiment.num.repetition", "Number of experiment repetitions (per fold) - default = 1", false),
        POP_SIZE("pop.size", "Population size", false),
        RT_POOL_SIZE("rt.pool.size", "Size of the pool of random trees used by GSX/GSM", false),
        NUMBER_THREADS("evol.num.threads", "Number of threads (for parallel execution)", false),
        TOURNAMENT_SIZE("pop.ind.selector.tourn.size", "Tournament size, when using tournament as selector", false),
        
        MIN_ERROR("evol.min.error", "Minimum error to consider a hit", false),
        MUT_STEP("breed.mut.step", "Mutation step", false),
        MUT_STEP_SD("breed.mut.step.sd", "Indicate if the mutation step is absolute (false) or relative to the stardard deviation"
                                        + "\n# of the outputs of the training set.", false),
        BREEDERS_LIST("breed.list", "List of breeders classes used during the evolution. The list items must be comma separated and the propability"
                                  + "\n# must follow the breeder class separated by a *. E.g.: "
                                  + "\n# edu.gsgp.population.builder.individual.BreederA*0.1, edu.gsgp.population.builder.individual.BreederB*0.9", true),
        POP_INITIALIZER("pop.initializer", "Population Initializer class. Default: edu.gsgp.population.builder.individual.SimplePopulator", true),
        POP_PIPELINE("pop.pipeline", "Class responsible for evolve a new population from a previous one", false),
        POP_INIT_ATTEMPTS("pop.initializer.attempts", "Number of attemtps before adding an individual in the population", false),
        
        SPREADER_PROB("breed.spread.prob", "Probability of applying the spreader operator (in standalone mode)", false),
        SPREADER_ALPHA("breed.spread.alpha", "Alpha used to compute the effective probability of applying the spreader", false);
//        MUT_PROB("breed.mut.prob", "Probability of applying the mutation operator", false),
//        XOVER_PROB("breed.xover.prob", "Probability of applying the crossover operator", false),
//        SEMANTIC_SIMILARITY_THRESHOLD("sem.gp.epsilon", "Threshold used to determine if two semantics are similar", false);

        public final String name;
        public final String description;
        public final boolean mandatory;

        private ParameterList(String name, String description, boolean mandatory) {
            this.name = name;
            this.description = description;
            this.mandatory = mandatory;
        }
    }

    /**
     * Load the parameters from the CLI and file
     * @param args CLI parameters
     * @return True if and only if parameters are loaded both from CLI and file
     * @throws Exception 
     */
    private boolean loadParameterFile(String[] args) throws Exception{
        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine parametersCLI = parser.parse(cliOptions, args);
            if(parametersCLI.hasOption("H")){
                writeParameterModel();
                return false;
            }
            if(!parametersCLI.hasOption("p"))
                throw new Exception("The parameter file was not specified.");
            String path = parametersCLI.getOptionValue("p");
            fileParameters = loadProperties(path);
            return true;
        } 
        catch (MissingOptionException ex){
            throw new Exception("Required parameter not found.");
        }
        catch (ParseException ex) {
            throw new Exception("Error while parsing the command line.");
        }
    }
    
    private Properties loadProperties(String path) throws Exception{
        path = path.replaceFirst("^~",System.getProperty("user.home"));
        File parameterFile = new File(path);
        if(!parameterFile.canRead()) 
            throw new FileNotFoundException("Parameter file can not be read: " + parameterFile.getCanonicalPath());
        FileInputStream fileInput = new FileInputStream(parameterFile);
        Properties property = new Properties();
        property.load(fileInput);
        if(property.containsKey(ParameterList.PARENT_FILE.name)){
            Properties propertyParent = loadProperties(property.getProperty("parent").trim());
            propertyParent.putAll(property);
            property = propertyParent;
        }
        return property;
    }
        
    private void writeParameterModel(){
        try{
            File file = new File("model.param");

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            StringBuilder textToPrint = new StringBuilder();
            for(ParameterList p : ParameterList.values()){
                textToPrint.append("# " + p.description + "\n");
                textToPrint.append(p.name + " = \n");
            }
            bw.write(textToPrint.toString());
            bw.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    
    private IndividualSelector getIndividualSelector() throws Exception{
        String value = getStringProperty(ParameterList.INDIVIDUAL_SELECTOR, false).toLowerCase();
        IndividualSelector indSelector;
        switch(value){
            case "tournament":
                indSelector = new TournamentSelector(getIntegerProperty(ParameterList.TOURNAMENT_SIZE, 7));
                break;
            default:
                throw new Exception("The inidividual selector must be defined.");
        }
        return indSelector;
    }
    
    private DataProducer getDataProducer() throws Exception{
        String value = getStringProperty(ParameterList.EXPERIMENT_DESIGN, false).toLowerCase();
        DataProducer dataProducer;
        switch(value){
            case "crossvalidation":
                dataProducer = new CrossvalidationHandler();
                break;
            case "holdout":
                dataProducer = new HoldoutHandler();
                break;
            default:
                throw new Exception("Experiment design must be crossvalidation or holdout.");
        }
        dataProducer.setDataset(getStringProperty(ParameterList.PATH_DATA_FILE, true), getStringProperty(ParameterList.PATH_TEST_FILE, true));
        return dataProducer;
    }
    
    /**
     * Load a boolean property from the file.
     * @param key The name of the property
     * @param defaultValue The default value for this property
     * @return The value loaded from the file or the default value, if it is not specified in the file.
     * @throws NumberFormatException The loaded value can not be converted to boolean
     * @throws NullPointerException The parameter file was not initialized
     * @throws MissingOptionException The parameter is mandatory and it was not found in the parameter file.
     */
    private boolean getBooleanProperty(ParameterList key, boolean defaultValue) 
                    throws NumberFormatException, NullPointerException, MissingOptionException{
        try {
            boolean keyPresent = fileParameters.containsKey(key.name);
            String strValue = keyPresent ? fileParameters.getProperty(key.name).replaceAll("\\s", "") : null;
            if (!keyPresent && key.mandatory) {
                throw new MissingOptionException("The input parameter (" + key.name + ") was not found");
            }
            else if(!keyPresent || strValue.equals("")){
                loadedParametersLog.append(key.name).append("=").append(defaultValue).append(" (DEFAULT)\n");
                return defaultValue;
            }
            loadedParametersLog.append(key.name).append("=").append(strValue).append("\n");
            return Boolean.parseBoolean(strValue);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(e.getMessage() + "\nThe input parameter (" + key.name + ") could not be converted to boolean.");
        } catch (NullPointerException e) {
            throw new NullPointerException(e.getMessage() + "\nThe parameter file was not initialized.");
        }
    }
    
    /**
     * Load an int property from the file.
     * @param key The name of the property
     * @param defaultValue The default value for this property
     * @return The value loaded from the file or the default value, if it is not specified in the file.
     * @throws NumberFormatException The loaded value can not be converted to int
     * @throws NullPointerException The parameter file was not initialized
     * @throws MissingOptionException The parameter is mandatory and it was not found in the parameter file.
     */
    private int getIntegerProperty(ParameterList key, int defaultValue) 
                    throws NumberFormatException, NullPointerException, MissingOptionException{
        try {
            boolean keyPresent = fileParameters.containsKey(key.name);
            String strValue = keyPresent ? fileParameters.getProperty(key.name).replaceAll("\\s", "") : null;
            if (!keyPresent && key.mandatory) {
                throw new MissingOptionException("The input parameter (" + key.name + ") was not found");
            }
            else if(!keyPresent || strValue.equals("")){
                loadedParametersLog.append(key.name).append("=").append(defaultValue).append(" (DEFAULT)\n");
                return defaultValue;
            }
            loadedParametersLog.append(key.name).append("=").append(strValue).append("\n");
            return Integer.parseInt(strValue);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(e.getMessage() + "\nThe input parameter (" + key.name + ") could not be converted to int.");
        } catch (NullPointerException e) {
            throw new NullPointerException(e.getMessage() + "\nThe parameter file was not initialized.");
        }
    }
    
    /**
     * Load a long property from the file.
     * @param key The name of the property
     * @param defaultValue The default value for this property
     * @return The value loaded from the file or the default value, if it is not specified in the file.
     * @throws NumberFormatException The loaded value can not be converted to long
     * @throws NullPointerException The parameter file was not initialized
     * @throws MissingOptionException The parameter is mandatory and it was not found in the parameter file.
     */
    private long getLongProperty(ParameterList key, long defaultValue) 
                    throws NumberFormatException, NullPointerException, MissingOptionException{
        try {
            boolean keyPresent = fileParameters.containsKey(key.name);
            String strValue = keyPresent ? fileParameters.getProperty(key.name).replaceAll("\\s", "") : null;
            if (!keyPresent && key.mandatory) {
                throw new MissingOptionException("The input parameter (" + key.name + ") was not found");
            }
            else if(!keyPresent || strValue.equals("")){
                loadedParametersLog.append(key.name).append("=").append(defaultValue).append(" (DEFAULT)\n");
                return defaultValue;
            }
            loadedParametersLog.append(key.name).append("=").append(strValue).append("\n");
            return Integer.parseInt(strValue);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(e.getMessage() + "\nThe input parameter (" + key.name + ") could not be converted to long.");
        } catch (NullPointerException e) {
            throw new NullPointerException(e.getMessage() + "\nThe parameter file was not initialized.");
        }
    }

    /**
     * Load a double property from the file.
     * @param key The name of the property
     * @param defaultValue The default value for this property
     * @return The value loaded from the file or the default value, if it is not specified in the file.
     * @throws NumberFormatException The loaded value can not be converted to double
     * @throws NullPointerException The parameter file was not initialized
     * @throws MissingOptionException The parameter is mandatory and it was not found in the parameter file.
     */
    private double getDoubleProperty(ParameterList key, double defaultValue) 
                    throws NumberFormatException, NullPointerException, MissingOptionException{
        try {
            boolean keyPresent = fileParameters.containsKey(key.name);
            String strValue = keyPresent ? fileParameters.getProperty(key.name).replaceAll("\\s", "") : null;
            if (!keyPresent && key.mandatory) {
                throw new MissingOptionException("The input parameter (" + key.name + ") was not found");
            }
            else if(!keyPresent || strValue.equals("")){
                loadedParametersLog.append(key.name).append("=").append(defaultValue).append(" (DEFAULT)\n");
                return defaultValue;
            }
            loadedParametersLog.append(key.name).append("=").append(strValue).append("\n");
            return Double.parseDouble(strValue);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(e.getMessage() + "\nThe input parameter (" + key.name + ") could not be converted to double.");
        } catch (NullPointerException e) {
            throw new NullPointerException(e.getMessage() + "\nThe parameter file was not initialized.");
        }
    }
    
    /**
     * Load a string property from the file.
     * @param key The name of the property
     * @param defaultValue The default value for this property
     * @return The value loaded from the file or the default value, if it is not specified in the file.
     * @throws NumberFormatException The loaded value can not be converted to string
     * @throws NullPointerException The parameter file was not initialized
     * @throws MissingOptionException The parameter is mandatory and it was not found in the parameter file.
     */
    private String getStringProperty(ParameterList key, boolean isFile) 
                    throws NumberFormatException, NullPointerException, MissingOptionException{
        try {
            boolean keyPresent = fileParameters.containsKey(key.name);
            String strValue = keyPresent ? fileParameters.getProperty(key.name).replaceAll("\\s", "") : null;
            if (!keyPresent && key.mandatory) {
                throw new MissingOptionException("Input parameter not found: " + key.name);
            }
            else if(!keyPresent){
                return null;
            }
            if(isFile){
                strValue = strValue.replaceFirst("^~",System.getProperty("user.home"));
            }
            loadedParametersLog.append(key.name).append("=").append(strValue).append("\n");
            return strValue;
        } catch (NullPointerException e) {
            throw new NullPointerException(e.getMessage() + "\nThe parameter file was not initialized.");
        }
    }

    private TreeBuilder getIndividualBuilder(boolean isForRandomTrees) throws Exception{
        String builderType;
        if(isForRandomTrees)
            builderType = getStringProperty(ParameterList.INDIVIDUAL_BUILDER_RAND_TREE, false).toLowerCase();
        else
            builderType = getStringProperty(ParameterList.INDIVIDUAL_BUILDER_POP, false).toLowerCase();
        int maxDepth = getIntegerProperty(ParameterList.MAX_TREE_DEPTH, 6);
        int minDepth = getIntegerProperty(ParameterList.MIN_TREE_DEPTH, 2);
        switch(builderType){
            case "grow":
                return new GrowBuilder(maxDepth, minDepth, functionSet, terminalSet);
            case "full":
                return new FullBuilder(maxDepth, minDepth, functionSet, terminalSet);
            case "rhh":
                return new HalfBuilder(maxDepth, minDepth, functionSet, terminalSet);
            default:
                throw new Exception("There is no builder called " + builderType + ".");
        }
    }
    
    public boolean isParameterLoaded() {
        return parameterLoaded;
    }
    
    
    private Breeder[] getBreederObjects() throws Exception{
        String[] strBreeders = getStringProperty(ParameterList.BREEDERS_LIST, false).split(",");
        ArrayList<Breeder> breeders = new ArrayList<Breeder>();
        for(String sBreeder : strBreeders){
            try{
                sBreeder = sBreeder.trim();
                String[] strBreedersSplitted = sBreeder.split("\\*");
                Class<?> breederClass = Class.forName(strBreedersSplitted[0].trim());
                Constructor<?> breederConstructor = breederClass.getConstructor(PropertiesManager.class, Double.class);
                Breeder newBreeder = (Breeder)breederConstructor.newInstance(this, Double.parseDouble(strBreedersSplitted[1].trim()));
                breeders.add(newBreeder);
            }
            catch(ClassNotFoundException e){
                throw new ClassNotFoundException("Error loading the terminal set. Class " + sBreeder + " not found", e);
            }
        }
        return breeders.toArray(new Breeder[breeders.size()]);
    }
    
    /**
     * 
     * @return
     * @throws Exception 
     */
    private Terminal[] getTerminalObjects() throws Exception{
        String[] sTerminals = getStringProperty(ParameterList.TERMINAL_LIST, false).split(",");
        boolean useAllInputs = true;
        for(String str : sTerminals){
            str = str.trim();
            if(str.toLowerCase().matches("x\\d+")){
                useAllInputs = false;
                break;
            }
        }
        ArrayList<Terminal> terminals = new ArrayList<Terminal>();
        if(useAllInputs){
            for(int i = 0; i < dataProducer.getNumInputs(); i++) terminals.add(new Input(i));
            for(String str : sTerminals){
                try{
                    str = str.trim();
                    Class<?> terminal = Class.forName(str);
                    Terminal newTerminal = (Terminal)terminal.newInstance();
                    terminals.add(newTerminal);
                }
                catch(ClassNotFoundException e){
                    throw new ClassNotFoundException("Error loading the terminal set. Class " + str + " not found", e);
                }
            }
        }
        else{
            // ************************ TO IMPLEMENT ************************
        }
        return terminals.toArray(new Terminal[terminals.size()]);
    }

    /**
     * Get the list of functions from the String read from the file
     * @return An array of functions
     * @throws Exception Parameter not found, error while parsing the function type 
     */
    private Function[] getFunctionObjects() throws Exception{
        String[] sFunctions = getStringProperty(ParameterList.FUNCTION_LIST, false).split(",");
        ArrayList<Function> functionArray = new ArrayList<Function>();
       
        for(String str : sFunctions){
            try{
                str = str.trim();
                Class<?> function = Class.forName(str);
                functionArray.add((Function)function.newInstance());
            }
            catch(ClassNotFoundException e){
                throw new ClassNotFoundException("Error loading the function set. Class " + str + " not found", e);
            }
        }
        
        return functionArray.toArray(new Function[functionArray.size()]);
    }
    
    /**
     * Get the population initializer from the file
     * @return A new population initializer object
     * @throws Exception Parameter not found, error while parsing the Populator type 
     */
    private Populator getPopInitObject() throws Exception{
        String populatorClassname = "";
        try {            
            populatorClassname = getStringProperty(ParameterList.POP_INITIALIZER, false).replaceAll("\\s", "");
            Class<?> populatorClass = Class.forName(populatorClassname);
            Constructor<?> populatorConstructor = populatorClass.getConstructor(PropertiesManager.class);
            return (Populator)populatorConstructor.newInstance(this);
        } 
        catch (ClassNotFoundException e) {
            throw new ClassNotFoundException("Error loading the population initializer. Class " + populatorClassname + " not found", e);
        } 
    }
    
    /**
     * Get the pipeline object from the file
     * @return A new pipeline object
     * @throws Exception Parameter not found, error while parsing the Pipeline type 
     */
    private Pipeline getPipelineObject() throws Exception{
        String populatorClassname = "";
        try {            
            populatorClassname = getStringProperty(ParameterList.POP_PIPELINE, false).replaceAll("\\s", "");
            Class<?> populatorClass = Class.forName(populatorClassname);
            Constructor<?> populatorConstructor = populatorClass.getConstructor();
            return (Pipeline)populatorConstructor.newInstance();
        } 
        catch (ClassNotFoundException e) {
            throw new ClassNotFoundException("Error loading the pipeline. Class " + populatorClassname + " not found", e);
        } 
    }
    
    /**
     * Get the fitness object from the file
     * @return A new fintess function
     * @throws Exception Parameter not found, error while parsing the Fitness type
     */
    private Fitness getFitnessObject() throws Exception{
        String fitnessClassname = "";
        try {
            fitnessClassname = getStringProperty(ParameterList.FITNESS_FUNCTION, false).replaceAll("\\s", "");
            Class<?> fitnessClass = Class.forName(fitnessClassname);
            return (Fitness)fitnessClass.newInstance();
        } 
        catch (ClassNotFoundException e) {
            throw new ClassNotFoundException("Error loading the fitness function. Class " + fitnessClassname + " not found", e);
        } 
    }
    
    /**
     * Update the experimental data from the dataProducer
     */
    public void updateExperimentalData() {
        experimentalData = dataProducer.getExperimentDataset();
    }

    /**
     * There are tree input options from the CLI: 
     * - The path for the parameter file
     * - One or more parameters to overwrite parameters from the file 
     * - The option of creating a parameter file model on the classpath
     */
    private void setOptions() {
        cliOptions = new Options();
        cliOptions.addOption(Option.builder("p")
                .required(false)
                .hasArg()
                .desc("Paramaters file")
                .type(String.class)
                .build());
        cliOptions.addOption(Option.builder("P")
                .required(false)
                .hasArg()
                .desc("Overwrite of one or more parameters provided by file.")
                .type(String.class)
                .hasArgs()
                .build());
        cliOptions.addOption(Option.builder("H")
                .required(false)
                .desc("Create a parameter file model on the classpath.")
                .type(String.class)
                .build());
    }

    //********************************** Public getters **********************************
    
    public MersenneTwister getRandomGenerator() {
        long newSeed = mersennePRNG.nextLong();
        return new MersenneTwister(newSeed);
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public int getNumExperiments() {
        return numExperiments;
    }

    /**
     * Return the number of generations to be performed during the evolution
     * @return The number of generations
     */
    public int getNumGenerations() {
        return numGenerations;
    }

    public int getNumThreads() {
        return numThreads;
    }
    
    public int getRTPoolSize() {
        return rtPoolSize;
    }

    public int getMaxInitAttempts() {
        return maxInitAttempts;
    }

    public double getMinError() {
        return minError;
    }

    public double getSpreaderAlpha() {
        return spreaderAlpha;
    }

    public double getSpreaderInitProb() {
        return spreaderInitProb;
    }

    /**
     * Return the mutation step. If the mutation step is defined w.r.t. the standard 
     * deviation of the training set outputs, we compute the value before returning it.
     * Otherwise, we return the value obtained from the parameter file.
     * @return The mutation step.
     */
    public double getMutationStep() {
        // The variable mutationStep is obtained from the parameter file
        if(!mutStepFromSD){
            return mutationStep;
        }
        else{
            return mutationStep * experimentalData.getDataset(DatasetType.TRAINING).getOutputSD();
        }
    }

//    public double getMutProb() {
//        return mutProb;
//    }
//
//    public double getXoverProb() {
//        return xoverProb;
//    }
//
//    public double getSemSimThreshold() {
//        return semSimThres;
//    }
    
    public String getOutputDir() {
        return outputDir;
    }

    public String getFilePrefix() {
        return filePrefix;
    }
    
    public Fitness getFitnessFunction(){
        return fitnessFunction.softClone();
    }
    
    public Node getRandomTree(MersenneTwister rnd){
        return randomTreeBuilder.newRootedTree(0, rnd);
    }
    
    public Node getNewIndividualTree(MersenneTwister rnd){
        return individualBuilder.newRootedTree(0, rnd);
    }
    
    public Individual selectIndividual(Population population, MersenneTwister rndGenerator){
        return individualSelector.selectIndividual(population, rndGenerator);
    }

    public Function getRandomFunction(MersenneTwister rnd) {
        return functionSet[rnd.nextInt(functionSet.length)].softClone();
    }

    public Terminal getRandomTerminal(MersenneTwister rnd) {
        return terminalSet[rnd.nextInt(terminalSet.length)].softClone(rnd);
    }

    public ExperimentalData getExperimentalData() {
        return experimentalData;
    }

    /**
     * Return a copy of the function set, to avoid modifications in the original set.
     * @return A copy of the terminal set
     */
    public Function[] getFunctionSet() {
        Function[] copyFunctionSet = new Function[functionSet.length];
        for(int i = 0; i < functionSet.length; i++){
            copyFunctionSet[i] = functionSet[i].softClone();
        }
        return copyFunctionSet;
    }
    
    /**
     * Return a copy of the terminal set, to avoid modifications in the original set.
     * @param rnd Random number generator used during terminal cloning
     * @return A copy of the terminal set
     */
    public Terminal[] getTerminalSet(MersenneTwister rnd){
        Terminal[] copyTerminalSet = new Terminal[terminalSet.length];
        for(int i = 0; i < terminalSet.length; i++){
            copyTerminalSet[i] = terminalSet[i].softClone(rnd);
        }
        return copyTerminalSet;
    }

    /**
     * Generate an array of pseudorandom number generators, used in multithreaded
     * environments.
     * @param size Number of pseudorandom number generators to be generated
     * @return The array of PRNG's
     */
    public MersenneTwister[] getMersennePRGNArray(int size){
        MersenneTwister[] generators = new MersenneTwister[size];
        for(int i = 0; i < size; i++){
            long seed = mersennePRNG.nextLong();
            generators[i] = new MersenneTwister(seed);
        }
        return generators;
    }

    public Pipeline getPipeline() {
        return pipeline.softClone();
    }
    
    public Populator getPopulationInitializer() {
        return populationInitializer.softClone();
    }

    /**
     * Return a list of sof clones of the breeders list recovered from the 
     * parameter file
     * @return The list of soft clones of the breeders
     */
    public Breeder[] getBreederList() {
        Breeder[] copyBreeders = new Breeder[breederList.length];
        for(int i = 0; i < breederList.length; i++){
            copyBreeders[i] = breederList[i].softClone(this);
        }
        return copyBreeders;
    }
    
    public String getLoadedParametersString(){
        return loadedParametersLog.toString();
    }
}
