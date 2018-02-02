/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population.operator;

import edu.gsgp.MersenneTwister;
import edu.gsgp.data.ExperimentalData;
import edu.gsgp.data.PropertiesManager;
import edu.gsgp.population.Individual;
import edu.gsgp.population.Population;

import java.util.Map;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 2014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 * 
 * Breeder methods are reponsible for the genetic operators throughout the evolution.
 */
public abstract class Breeder{
    protected PropertiesManager properties;
    protected double probability;
    protected Population originalPopulation;
    
    protected Breeder(PropertiesManager properties, double probability) {
        this.properties = properties;
        this.probability = probability;
    }

    public double getProbability() {
        return probability;
    }
    
    public abstract Breeder softClone(PropertiesManager properties);

    public abstract Individual generateIndividual(MersenneTwister rndGenerator, ExperimentalData expData);
    
    public void setup(Population originalPopulation, ExperimentalData expData){
        this.originalPopulation = originalPopulation;
    }
}
