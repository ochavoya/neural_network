package com.ochavoya.neural_net.neuron;

public class InternalNeuron extends Neuron {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private int      inputSize;
    private Neuron[] input;
    private double[] weight;
    private double[] weightGradient;
    private double   threshold;
    private double   thresholdGradient;
    private double   output;
    private boolean  updated = false;
    
    
    public InternalNeuron(Neuron[] input){
        inputSize           = input.length;
        this.input          = input;
        this.weight         = new double[inputSize];
        this.weightGradient = new double[inputSize];
        for(int i = 0; i<inputSize ; ++i){
            this.weight[i]             = gaussian();
            this.weightGradient[i]     = 0;
        }
        threshold             = gaussian();
        thresholdGradient     = 0;
    }
    

    @Override
    public double getOutput() {
        if(!updated){
            output = 0;
            for(int i=0;i<inputSize;++i){
                output += weight[i] * input[i].getOutput();
            }
            output = sigmoid(output + threshold);
            updated = true;
        }
        return output;
    }

    @Override
    public void reset() {
        updated = false;
    }

    @Override
    public void updateGradient(double signal) {
       double output = getOutput();
       signal = signal * output * (1 - output);
       for(int i=0;i<inputSize;++i){
           weightGradient[i] += signal*input[i].getOutput();
           input[i].updateGradient(signal * weight[i]);
       }
       thresholdGradient += signal;
    }

    @Override
    public void commit(double alpha) {


        for(int i=0;i<inputSize;++i){
            weight[i]            -= alpha * weightGradient[i];
            weightGradient[i]    *= lambda;
        }
        threshold                -= alpha * thresholdGradient;
        thresholdGradient        *= lambda;
        reset();
    }
    
    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder("Neuron:\n");
        for(int i=0;i<input.length;++i){
            builder.append( "weight=" + weight[i] + "\n");
        }
        builder.append( "threshold=" + threshold + "\n" );
        return builder.toString();
    }
}
