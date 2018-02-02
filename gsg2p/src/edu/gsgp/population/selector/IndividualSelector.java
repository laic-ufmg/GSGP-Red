/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population.selector;

import edu.gsgp.MersenneTwister;
import edu.gsgp.population.Individual;
import edu.gsgp.population.Population;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 2014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public interface IndividualSelector {
    public Individual selectIndividual(Population population, MersenneTwister rnd);
}
