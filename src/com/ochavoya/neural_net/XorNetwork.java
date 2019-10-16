package com.ochavoya.neural_net;

public class XorNetwork {
    
    static private double[] firstInput  = new double[]{0,0};
    static private double[] secondInput = new double[]{0,1};
    static private double[] thirdInput  = new double[]{1,0};
    static private double[] fourthInput = new double[]{1,1};
    
    static private double[] firstOutput  = new double[]{0};
    static private double[] secondOutput = new double[]{1};
    static private double[] thirdOutput  = new double[]{1};
    static private double[] fourthOutput = new double[]{0};
    
    static int[] nNeuron = new int[]{2,2,1}; 
    static FeedForwardNetwork network;

    static private void train(double alpha,int n,double maxError){
        double error;
        int counter = 0 ;
        do{
            network = new FeedForwardNetwork(nNeuron);
                counter = n;
                do{
                network.processInput(firstInput);
                network.updateGradient(firstOutput);
                network.processInput(secondInput);
                network.updateGradient(secondOutput);
                network.processInput(thirdInput);
                network.updateGradient(thirdOutput);
                network.processInput(fourthInput);
                network.updateGradient(fourthOutput);
                error = network.commit(alpha);
                }while(--counter>0 && error > maxError);
                System.out.println("Error: " + error);
            }while(error > maxError);
        
    }
    
    
    public static void main(String[] args) {
        train(0.03,10000,0.04);
        System.out.println(network.toString());
    }

}