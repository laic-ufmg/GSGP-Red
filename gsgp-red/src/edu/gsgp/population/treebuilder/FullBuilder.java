/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package edu.gsgp.population.treebuilder;
import edu.gsgp.MersenneTwister;
import edu.gsgp.nodes.Node;
import edu.gsgp.nodes.functions.Function;
import edu.gsgp.nodes.terminals.Terminal;


/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 2014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class FullBuilder extends TreeBuilder {

    public FullBuilder(final int maxDepth, 
                       final int minDepth, 
                       final Function[] functions,
                       final Terminal[] terminals) {
        super(maxDepth, minDepth, functions, terminals);
    }

    @Override
    public Node newRootedTree(final int current, MersenneTwister rnd){
        return fullNode(0, maxDepth, rnd);
    }
}
