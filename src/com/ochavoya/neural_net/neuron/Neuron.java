package com.ochavoya.neural_net.neuron;

import java.io.Serializable;

public abstract class Neuron implements Serializable {
    
    /**
     * 
     */
    private static final double mu = 0, sigma = 2.0;
    private static final long serialVersionUID = 1L;

    public static final double sigmoid(double x){
        return 1/(1 + Math.exp(-x));
    }
    
    public static double lambda = 0.25;
    
    public static final double gaussian(){
        double theta = 2*Math.PI*Math.random();
        double r     = Math.sqrt(- 2 * Math.log(1-Math.random()));
        return sigma * r * Math.cos(theta) + mu;
    }
    
    
    abstract public double  getOutput();
    abstract public void    reset();
    abstract public void    updateGradient(double delta);
    abstract public void    commit(double rate);
    
    public static void main(String[] arg){
        for(int i=0;i<1000;++i){
            System.out.println(gaussian());
        }
    }

}
