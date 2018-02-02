/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.nodes.terminals;

import edu.gsgp.MersenneTwister;
import edu.gsgp.nodes.Node;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 2014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public interface Terminal extends Node{
//    public void setup(MersenneTwister rnd);
    public Terminal softClone(MersenneTwister rnd);

}
