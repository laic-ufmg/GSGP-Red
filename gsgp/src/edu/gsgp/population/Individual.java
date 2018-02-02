/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population;

import edu.gsgp.data.ExperimentalData;
import edu.gsgp.nodes.Node;
import edu.gsgp.population.fitness.Fitness;

import javax.naming.InitialContext;
import java.math.BigInteger;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 2014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */

public abstract class Individual implements Comparable<Individual>{
    protected Node tree;
    protected BigInteger numNodes;
    protected Fitness fitnessFunction;

    public Individual(Node tree, BigInteger numNodes, Fitness fitnessFunction) {
        this.tree = tree;
        this.numNodes = new BigInteger(numNodes + "");
        this.fitnessFunction = fitnessFunction;
    }
    
    public double eval(double[] input){
        return tree.eval(input);
    }

    public Node getTree() {
        return tree;
    }

    public void setTree(Node tree) {
        this.tree = tree;
    }
    
    @Override
    public int compareTo(Individual o) {
        if (getFitness() < o.getFitness()){
            return -1;
        }
        if (getFitness() > o.getFitness()) {
            return 1;
        }
        return 0;
    }

    public boolean isBestSolution(double minError) {
        return getFitness() <= minError;
    }

    @Override
    public String toString() {
        return tree.toString();
    }
        
    public Fitness getFitnessFunction(){
        return fitnessFunction;
    }
    
    @Override
    public abstract Individual clone();


    /**
     * Return the number of nodes as a BigInteger.
     * @return Number of nodes
     */
    public final BigInteger getNumNodes() {
        return numNodes;
    }


    /**
     * Set the number of nodes.
     * @param numNodes Number of nodes (BigInteger)
     */
    public final void setNumNodes(BigInteger numNodes) {
        this.numNodes = numNodes;
    }


    public abstract double getFitness();
    public abstract String getNumNodesAsString();
    public abstract String getTrainingFitnessAsString();
    public abstract String getTestFitnessAsString();
    public abstract double[] getTrainingSemantics();
    public abstract double[] getTestSemantics();
}
