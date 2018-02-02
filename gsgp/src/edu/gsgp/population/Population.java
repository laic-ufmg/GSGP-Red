/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 2014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class Population extends ArrayList<Individual>{
    
    public Population() {
        super();
    }
    
    public Population(ArrayList<Individual> individuals) {
        super(individuals);
    }
    
    public void addAll(Individual[] newIndividuals){
        addAll(Arrays.asList(newIndividuals));
    }
        
    public Individual getBestIndividual(){
        Collections.sort(this);
        return get(0);
    }
}
