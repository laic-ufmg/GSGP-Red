/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population.operator;

import edu.gsgp.population.operator.Breeder;
import edu.gsgp.MersenneTwister;
import edu.gsgp.data.ExperimentalData;
import edu.gsgp.data.PropertiesManager;
import edu.gsgp.population.Individual;

import java.util.Map;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 2014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class ReproductionBreeder extends Breeder {

//    public ReproductionBreeder(PropertiesManager properties, ExperimentalData expData,  Double probability) {
//        super(properties, expData, probability);
//    }
    
    public ReproductionBreeder(PropertiesManager properties, Double probability) {
        super(properties, probability);
    }

    @Override
    public Individual generateIndividual(MersenneTwister rndGenerator, ExperimentalData expData) {
        return properties.selectIndividual(originalPopulation, rndGenerator).clone();
    }
    
    @Override
    public Breeder softClone(PropertiesManager properties) {
        return new ReproductionBreeder(properties, this.probability);
    }
}
