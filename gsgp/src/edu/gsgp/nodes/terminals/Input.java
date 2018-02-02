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
public class Input implements Terminal{
    private final int index;
    private Node parent = null;
    private int parentArgPosition;

    public Input(int index) {
        this.index = index;
    }
    
    @Override
    public int getArity() {
        return 0;
    }

    @Override
    public double eval(double[] inputs) {
        return inputs[index];
    }
    
    public int getIndex() {
        return index;
    }
    
    @Override
    public String toString() {
        return "x" + index + "";
    }

    @Override
    public Node getChild(int index) {
        return null;
    }

    @Override
    public int getNumNodes() {
        return 1;
    }

    @Override
    public Node getParent() {
        return parent;
    }

    @Override
    public void setParent(Node parent, int argPosition) {
        this.parent = parent;
        this.parentArgPosition = argPosition;
    }

    @Override
    public int getParentArgPosition() {
        return parentArgPosition;
    }

    @Override
    public Node clone(Node parent) {
        Input newNode = new Input(index);
        newNode.parent = parent;
        newNode.parentArgPosition = parentArgPosition;
        return newNode;
    }

    @Override
    public Terminal softClone(MersenneTwister rnd) {
        return new Input(index);
    }
}
