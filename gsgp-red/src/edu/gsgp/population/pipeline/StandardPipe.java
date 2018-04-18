/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population.pipeline;

import edu.gsgp.data.ExperimentalData;
import edu.gsgp.population.GSGPIndividual;
import edu.gsgp.population.Individual;
import edu.gsgp.population.Population;
import edu.gsgp.population.operator.Breeder;

import java.util.Map;

/**
 *
 * @author luiz
 */
public class StandardPipe extends Pipeline{
    @Override
    public Population evolvePopulation(Population originalPop, ExperimentalData expData, int size) {
        // Update the breeder with the current population before generating a new one
        for(Breeder breeder : breederArray) ((Breeder)breeder).setup(originalPop, expData);

        // Generate the new population from the original one
        Population newPopulation = new Population();
        for(int i = 0; i < size; i++){
            double floatDice = rndGenerator.nextDouble();
            double probabilitySum = 0;
            Breeder selectedBreeder = breederArray[0];
            for (Breeder breeder : breederArray) {
                if (floatDice < probabilitySum + breeder.getProbability()) {
                    selectedBreeder = breeder;
                    break;
                }
                probabilitySum += breeder.getProbability();
            }
            Individual newInd = selectedBreeder.generateIndividual(rndGenerator, expData);
            newPopulation.add(newInd);
            //System.out.println(((GSGPIndividual) newInd).getCombinationRepr());
            //new java.util.Scanner(System.in).nextLine();
        }
        return newPopulation;
    }


    @Override
    public Pipeline softClone() {
        return new StandardPipe();
    }
}
