/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.nodes.functions;

import edu.gsgp.nodes.Node;

/**
 * Analytic Quotient
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 2014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class AQ extends Function{
    public AQ() { 
        super();
    }

    @Override
    public int getArity() { return 2; }
    
    @Override
    public double eval(double[] inputs) {
        double tmp = arguments[1].eval(inputs) * arguments[1].eval(inputs);
        return arguments[0].eval(inputs) / Math.sqrt(1+tmp);
    }

    @Override
    public int getNumNodes() {
        return arguments[0].getNumNodes() + arguments[1].getNumNodes() + 1;
    }
    
    @Override
    public Function softClone() {
        return new AQ();
    }
    
    @Override
    public String toString() {
        return "AQ ( " + arguments[0].toString() + " " + arguments[1].toString() + " )";
    }
    
    @Override
    public Node clone(Node parent) {
        AQ newNode = new AQ();
        for(int i = 0; i < getArity(); i++) newNode.arguments[i] = arguments[i].clone(newNode);
        newNode.parent = parent;
        newNode.parentArgPosition = parentArgPosition;
        return newNode;
    }
}
