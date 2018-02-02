/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population;

import edu.gsgp.Utils;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import edu.gsgp.nodes.Node;
import edu.gsgp.population.fitness.Fitness;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 2014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */

public class GSGPIndividual extends Individual{

    private Map<Integer, Double> reprCoef;


    public GSGPIndividual(Node tree, BigInteger numNodes, Fitness fitnessFunction) {
        super(tree, numNodes, fitnessFunction);
        reprCoef = new HashMap<>();

        // Individual doesn't have an initial representation (probably from the initial population)
        reprCoef.put(this.hashCode(), 1.0);
    }


    public GSGPIndividual(BigInteger numNodes, Fitness fitnessFunction, GSGPIndividual p1, GSGPIndividual p2, Double crossoverConst) {
        super(null, numNodes, fitnessFunction);
        reprCoef = new HashMap<>();

        // Individual is a crossover offspring
        this.propagateCrossover(p1, p2, crossoverConst);
    }


    public GSGPIndividual(BigInteger numNodes, Fitness fitnessFunction, GSGPIndividual p1, Integer mutationT1, Integer mutationT2, double mutationStep) {
        super(null, numNodes, fitnessFunction);
        reprCoef = new HashMap<>();

        // Individual is a mutation offspring
        this.propagateMutation(p1, mutationT1, mutationT2, mutationStep);
    }


    @Override
    public GSGPIndividual clone(){
        if(tree != null)
            return new GSGPIndividual(tree.clone(null), numNodes, fitnessFunction);
        return new GSGPIndividual(null, BigInteger.valueOf(0), fitnessFunction);
    }


    @Override
    public String toString() {
        return tree.toString();
    }


    public double eval(double[] input){
        return tree.eval(input);
    }


    @Override
    public String getNumNodesAsString() {
        return getNumNodes().toString();
    }


    @Override
    public String getTrainingFitnessAsString() {
        return Utils.format(fitnessFunction.getTrainingFitness());
    }


    @Override
    public String getTestFitnessAsString() {
        return Utils.format(fitnessFunction.getTestFitness());
    }


    @Override
    public double getFitness() {
        double value = fitnessFunction.getComparableValue();
        if(Double.isInfinite(value) || Double.isNaN(value)) return Double.MAX_VALUE;
        return value;
    }


    @Override
    public double[] getTrainingSemantics() {
        return fitnessFunction.getSemantics(Utils.DatasetType.TRAINING);
    }


    @Override
    public double[] getTestSemantics() {
        return fitnessFunction.getSemantics(Utils.DatasetType.TEST);
    }


    public Map getReprCoef(){
        return this.reprCoef;
    }


    /**
     * Propagate coefficients for crossover offspring.
     *
     * @param p1
     * @param p2
     * @param crossoverConst
     */
    public void propagateCrossover(GSGPIndividual p1, GSGPIndividual p2, Double crossoverConst) {
        this.addCoefficients(p1.getReprCoef(), crossoverConst);

        this.addCoefficients(p2.getReprCoef(), (1.0 - crossoverConst));
    }


    /**
     * Propagate coefficients for mutation offspring.
     *
     * @param p1
     * @param mutationT1
     * @param mutationT2
     * @param mutationStep
     */
    public void propagateMutation(GSGPIndividual p1, Integer mutationT1, Integer mutationT2, double mutationStep) {
        this.addCoefficients(p1.getReprCoef(), 1.0);

        Double storedCoef = this.reprCoef.get(mutationT1);
        this.reprCoef.put(mutationT1, (storedCoef == null) ? mutationStep : storedCoef + mutationStep);

        storedCoef = this.reprCoef.get(mutationT2);
        this.reprCoef.put(mutationT2, (storedCoef == null) ? (mutationStep * -1) : storedCoef + (mutationStep * -1));
    }


    /**
     * Add coefficients from a parent's representation times a constant to this individual representation.
     *
     * @param parentRepr
     * @param multiplyBy
     */
    public void addCoefficients(Map parentRepr, double multiplyBy) {
        for(Map.Entry<Integer, Double> entry : ((HashMap<Integer, Double>) parentRepr).entrySet()) {
            Integer indHash = (Integer) entry.getKey();
            Double coef = (Double) entry.getValue();
            Double storedCoef = this.reprCoef.get(indHash);

            coef = coef * multiplyBy;

            this.reprCoef.put(indHash, (storedCoef == null) ? coef : storedCoef + coef);
        }
    }
}
