/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.nodes.functions;

import edu.gsgp.nodes.Node;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 2014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class Add extends Function{
    public Add() {
        super();
    }

    @Override
    public int getArity() { return 2; }
    
    @Override
    public double eval(double[] inputs) {
        return arguments[0].eval(inputs) + arguments[1].eval(inputs);
    }

    @Override
    public int getNumNodes() {
        return arguments[0].getNumNodes() + arguments[1].getNumNodes() + 1;
    }
    
    @Override
    public Function softClone() {
        return new Add();
    }
    
    @Override
    public String toString() {
        return "+ ( " + arguments[0].toString() + " " + arguments[1].toString() + " )";
    }
    
    @Override
    public Node clone(Node parent) {
        Add newNode = new Add();
        for(int i = 0; i < getArity(); i++) newNode.arguments[i] = arguments[i].clone(newNode);
        newNode.parent = parent;
        newNode.parentArgPosition = parentArgPosition;
        return newNode;
    }
}
