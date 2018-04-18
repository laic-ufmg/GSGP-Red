/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population.operator;

import edu.gsgp.MersenneTwister;
import edu.gsgp.Utils;
import edu.gsgp.Utils.DatasetType;
import edu.gsgp.data.Dataset;
import edu.gsgp.data.ExperimentalData;
import edu.gsgp.data.Instance;
import edu.gsgp.data.PropertiesManager;
import edu.gsgp.nodes.Node;
import edu.gsgp.nodes.functions.Add;
import edu.gsgp.nodes.functions.Mul;
import edu.gsgp.nodes.functions.ProtectedDiv;
import edu.gsgp.nodes.terminals.ERC;
import edu.gsgp.nodes.functions.Pow;
import edu.gsgp.population.GSGPIndividual;
import edu.gsgp.population.Individual;
import edu.gsgp.population.fitness.Fitness;
import java.math.BigInteger;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 2014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class EGSMSigBreeder extends Breeder{

    // Elitist Geometric Semantic Mutation with Sigmoid
    public EGSMSigBreeder(PropertiesManager properties, Double probability) {
        super(properties, probability);
    }

    private Fitness evaluate(GSGPIndividual parent,
                             Node randomTree1,
                             Node randomTree2,
                             ExperimentalData expData) {

        Fitness fitnessFunction = parent.getFitnessFunction().softClone();

        // Compute the training semantics of generated random tree
        fitnessFunction.resetFitness(DatasetType.TRAINING, expData);
        Dataset dataset = expData.getDataset(DatasetType.TRAINING);

        // Parent semantics
        double[] semInd;

        semInd = parent.getTrainingSemantics();

        int instanceIndex = 0;
        for (Instance instance : dataset) {

            double rtValue = Utils.sigmoid(randomTree1.eval(instance.input));
            rtValue -= Utils.sigmoid(randomTree2.eval(instance.input));

            double estimated = semInd[instanceIndex] + properties.getMutationStep() * rtValue;

            fitnessFunction.setSemanticsAtIndex(estimated, instance.output, instanceIndex++, DatasetType.TRAINING);
        }
        fitnessFunction.computeFitness(DatasetType.TRAINING);

        return fitnessFunction;
    }


    @Override
    public Individual generateIndividual(MersenneTwister rndGenerator, ExperimentalData expData) {


        // Mutation parent
        GSGPIndividual parent = (GSGPIndividual)properties.selectIndividual(originalPopulation, rndGenerator);

        // Generate random mask trees
        Node randomTree1 = properties.getRandomTree(rndGenerator);
        Node randomTree2 = properties.getRandomTree(rndGenerator);

        // Compute the number of nodes in the offspring (3 extra nodes used for operations, 1 for mutation step and 2 * 8 for sigmoids)
        BigInteger numNodes = parent.getNumNodes().
                add(BigInteger.valueOf(randomTree1.getNumNodes())).
                add(BigInteger.valueOf(randomTree2.getNumNodes())).
                add(BigInteger.valueOf(4)).
                add(BigInteger.valueOf(2 * 8));

        Fitness fitnessFunction = evaluate(parent, randomTree1, randomTree2, expData);

        randomTree1 = appendSigmoid(randomTree1);
        randomTree2 = appendSigmoid(randomTree2);

        double parentFitness = parent.getTrainingFitness();
        double offspringFitness = fitnessFunction.getTrainingFitness();

        if(offspringFitness < parentFitness) {
            GSGPIndividual offspring = new GSGPIndividual(numNodes, fitnessFunction, parent, randomTree1, randomTree2, properties.getMutationStep());

            return offspring;
        }
        else {
            return parent.clone();
        }
    }


    public Node appendSigmoid(Node maskTree) {
        ProtectedDiv root = new ProtectedDiv();
        Add sigAdd = new Add();
        Pow sigPow = new Pow();
        Mul sigMul = new Mul();

        root.addNode(new ERC(1), 0);
        root.addNode(sigAdd, 1);

        sigAdd.addNode(new ERC(1), 0);
        sigAdd.addNode(sigPow, 1);

        sigPow.addNode(new ERC(java.lang.Math.E), 0);
        sigPow.addNode(sigMul, 1);

        sigMul.addNode(new ERC(-1), 0);
        sigMul.addNode(maskTree, 1);

        return root;
    }


    @Override
    public Breeder softClone(PropertiesManager properties) {
        return new EGSMSigBreeder(properties, probability);
    }
}