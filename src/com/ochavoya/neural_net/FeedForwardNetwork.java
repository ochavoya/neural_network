package com.ochavoya.neural_net;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import com.ochavoya.neural_net.neuron.*;

public class FeedForwardNetwork implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private InputNeuron[]    inputLayer;
    private InternalNeuron[] outputLayer;
    private int nLayers;
    private ArrayList<Neuron> list = new ArrayList<Neuron>();
    private double error;
    private int sampleCounter = 0;
    private int[] nNeurons;

    public FeedForwardNetwork(int[] nNeurons){
       boolean invalidArgument = false;
       if( nNeurons == null || nNeurons.length == 0){
           invalidArgument = true;
       }
       if(!invalidArgument){
           for(int i =0; i< nNeurons.length && !invalidArgument ; ++i){
               if(nNeurons[i]<1){
                   invalidArgument = true;
               }
           }
       }
       if(invalidArgument){
           System.out.println("FATAL ERROR:");
           System.out.println("FeedForwardNetwork(int[] nNeurons) - Invalid argument");
           System.out.println("-- The array nNeurons holds the number of neurons per layer.");
           System.out.println("-- It must have at least one element, and all its elements must");
           System.out.println("   be positive.");
           System.exit(0);
       }
       nLayers = nNeurons.length;
       this.nNeurons = nNeurons;
       inputLayer = new InputNeuron[nNeurons[0]];
       for(int i=0;i<inputLayer.length;++i)inputLayer[i] = new InputNeuron();
       list.addAll(Arrays.asList(inputLayer));
       Neuron[] last = null;
       InternalNeuron[] next = null;
       last = inputLayer;
       for(int i=1;i<nLayers;++i){
           next = new InternalNeuron[nNeurons[i]];
           for(int j=0;j<nNeurons[i];++j){
               next[j] = new InternalNeuron(last);
           }
           list.addAll(Arrays.asList(next));
           last = next;
       }
       outputLayer = next;
    }
    
    public void processInput(double[] input) throws IllegalArgumentException {
        if(input.length != inputLayer.length){
            throw new IllegalArgumentException("setInput()");
        }
        reset();
        for(int i=0;i<input.length;++i){
            inputLayer[i].setInput(input[i]);
        }
        for(int i=0;i<outputLayer.length;++i){
            outputLayer[i].getOutput();
        }
    }
    
    public void updateGradient(double[] signal) throws IllegalArgumentException{
        if(signal.length != outputLayer.length){
            throw new IllegalArgumentException("updateGradient()");
        }
        for(int i=0;i<outputLayer.length;++i){
            double delta = outputLayer[i].getOutput() - signal[i];
            error += Math.abs(delta);
            outputLayer[i].updateGradient(delta);
        }
        ++sampleCounter;
    }
    

    
    public void reset(){
        for(Neuron n:list){
            n.reset();
        }
    }
    
    public double commit(double alpha){
        for(Neuron n:list){
            n.commit(alpha);
        }
        double averageError = error/sampleCounter;
        error = 0;
        sampleCounter = 0;
        return averageError;
    }
    
    public double[] getOutput(){
        double[] output = new double[outputLayer.length];
        for(int i=0;i<outputLayer.length; ++i) output[i] = outputLayer[i].getOutput();
        return output;
    }
    
    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder("Number of layers = " + nLayers +"\n");
        for(int i=0;i<nLayers;++i){
            builder.append("Neurons in layer " + i + " = " + nNeurons[i] + "\n");
        }
        for(Neuron n:list){
            builder.append(n.toString());
        }
        return builder.toString();
    }
}
