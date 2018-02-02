package edu.gsgp;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 2014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class MyException extends Exception{
    private Object originObj;
    
    public MyException(Object originObj, String message) {
        super(message);
        this.originObj = originObj;
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace(); 
        System.out.println("Exception generated on class " + originObj.getClass());
    }
    
    
}
