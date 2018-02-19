/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.population.selector;

import java.util.ArrayList;
import java.util.Arrays;
import edu.gsgp.MersenneTwister;
import edu.gsgp.population.Individual;
import edu.gsgp.population.Population;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 2014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class TournamentSelector implements IndividualSelector{
    private int tournamentSize;

    public TournamentSelector(int tournamentSize) {
        this.tournamentSize = tournamentSize;
    }
    
    @Override
    public Individual selectIndividual(Population population, MersenneTwister rnd) throws NullPointerException{
        int popSize = population.size();
        ArrayList<Integer> indexes = new ArrayList<>();
        for(int i = 0; i < popSize; i++) indexes.add(i);
        Individual[] tournament = new Individual[tournamentSize];
        for(int i = 0; i < tournamentSize; i++){
            tournament[i] = population.get(indexes.remove(rnd.nextInt(indexes.size())));
        }
        Arrays.sort(tournament);
        return tournament[0];
    }
}
