package com.ochavoya.neural_net.neuron;

public class InputNeuron extends Neuron {

    private static final long serialVersionUID = 1L;
    
    private double  input;
    private double  output;
    private double  weight;
    private double  threshold;
    private double  weightGradient        = 0.0;
    private double  thresholdGradient     = 0.0;
    private boolean updated               = false;
    
    
    public InputNeuron(){
        weight    = gaussian();
        threshold = gaussian();
    }
    
    public void setInput(double input){
        this.input = input;
        updated    = false;
    }
    

    @Override
    public double getOutput() {
        if(!updated){
            output  = sigmoid( weight * input + threshold );
            updated = true;
        }
        return output;
    }

    @Override
    public void updateGradient(double signal) {
        double output = getOutput();
        signal *= output * ( 1 - output );
        weightGradient    +=  signal * input;
        thresholdGradient += signal  ;
    }

    @Override
    public void commit(double alpha) {

        weight               -= alpha * weightGradient;
        threshold            -= alpha * thresholdGradient;
        weightGradient        *= lambda;
        thresholdGradient     *= lambda;
        reset();
    }



    @Override
    public void reset() {
    }
    
    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder("Neuron:\n");
        builder.append("weight="+weight + "\n" + "treshold=" + threshold + "\n");
        return builder.toString();
    }
    
}
