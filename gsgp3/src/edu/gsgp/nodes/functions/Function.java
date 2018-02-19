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
public abstract class Function implements Node{
    protected Node[] arguments;
    protected Node parent = null;
    protected int parentArgPosition;
    
    public Function() {
        arguments = new Node[getArity()];
    }
    
    /** Methods to be implemented in the subclasses. **/
    @Override
    public abstract double eval(double[] inputs);
    @Override
    public abstract int getNumNodes();
    @Override
    public abstract Node clone(Node parent);
    @Override
    public abstract int getArity();
    public abstract Function softClone();
    
    /**
     * Return the argument at the given position
     * @param index Position
     * @return The Node argument
     */
    @Override
    public final Node getChild(int index) {
        return arguments[index];
    }
    
    @Override
    public final Node getParent() {
        return parent;
    }

    @Override
    public final void setParent(Node parent, int argPosition) {
        this.parent = parent;
        this.parentArgPosition = argPosition;
    }
    
    public final void addNode(Node newNode, int argPosition) {
        arguments[argPosition] = newNode;
        newNode.setParent(this, argPosition);
    }

    @Override
    public final int getParentArgPosition() {
        return parentArgPosition;
    }
}