package com.ochavoya.neural_net;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import javax.imageio.ImageIO;

import com.ochavoya.neural_net.FeedForwardNetwork;

public class NewImageNetwork implements Serializable {
    /**
     * 
    */
    
    private static final long serialVersionUID = 1L;
    private int    inputWidth;
    private int    inputHeight;
    private BufferedImage inputImage;
    private static final int windowSize = 13;
    private static final int inputSize = windowSize * windowSize;
    private FeedForwardNetwork greenNetwork;
    private FeedForwardNetwork   redNetwork;
    private FeedForwardNetwork  blueNetwork;
    private int[] nNeuron = new int[]{windowSize*windowSize,16,8,8,16,windowSize*windowSize};
    

    
    public NewImageNetwork(String filename){
        File file = new File(filename);
        try {
            inputImage = ImageIO.read(file);
        } catch (IOException e) {
            System.out.println("ImageNetwork() - Could not read image file " + filename);
            System.exit(0);
        }
        inputWidth        = inputImage.getWidth();
        inputHeight       = inputImage.getHeight();
        greenNetwork = new FeedForwardNetwork(nNeuron);
        redNetwork   = new FeedForwardNetwork(nNeuron);
        blueNetwork  = new FeedForwardNetwork(nNeuron);
    }
    
        
    private int getRed(int rgb){
        return (rgb >> 16) & 0xff;
    }
    
    private int getGreen(int rgb){
        return (rgb >> 8) & 0xff;
    }
    
    private int getBlue(int rgb){
        return rgb & 0xff;
    }
    
    
    
    private double commit(double alpha){
        
        double redError;
        double greenError;
        double blueError;
        double error;
        redError   = redNetwork.commit(alpha);
        greenError = greenNetwork.commit(alpha);        
        blueError  = blueNetwork.commit(alpha);
        error = Math.max(redError, greenError);
        error = Math.max(error, blueError);
        return error;
    }

    private int getRandomRow(){
        int i;
        for(i=(int) Math.random() * inputWidth;i>=inputWidth-windowSize;i=(int) (Math.random()*inputWidth));
        return i;
    }
    
    private int getRandomColumn(){
        int i;
        for(i=(int) Math.random() * inputHeight;i>=inputHeight-windowSize;i=(int) (Math.random()*inputHeight));
        return i;
    }
    
    
    private double train(double alpha,double maxError){
        int rgb;
        double[] redSignal = new double[inputSize];
        double[] greenSignal = new double[inputSize];
        double[] blueSignal = new double[inputSize];
        long roundSize = (long)Math.sqrt((long) inputWidth * inputHeight / inputSize);
        long counter = 0 ;
        double error = 0 ;
        do{
            int row = getRandomRow();
            int col = getRandomColumn();
            int address=0;
            for(int i=0;i<windowSize;++i){
                for(int j=0;j<windowSize;++j){
                  rgb = inputImage.getRGB(row + i, col + j);
                  redSignal[address] = getRed(rgb)/255.0;
                  greenSignal[address] = getGreen(rgb)/255.0;
                  blueSignal[address] = getBlue(rgb)/255.0;
                  ++address;
                }
            }
            redNetwork.processInput(redSignal);
            greenNetwork.processInput(greenSignal);
            blueNetwork.processInput(blueSignal);
            redNetwork.updateGradient(redSignal);
            greenNetwork.updateGradient(greenSignal);
            blueNetwork.updateGradient(blueSignal);
            error    += commit(alpha);
            ++counter;
                System.out.println(counter + "->" + error/counter);
        }
         while( counter < roundSize );
        return error/counter;
    }
    
    
    private int correct(int color){
        color = color < 0 ? 0 :color;
        color = color > 255 ? 255 :color;
        return color;
    }
    
    private int getRgb(int red,int green,int blue){
        red   = correct(red)  ;
        green = correct(green);
        blue  = correct(blue) ;
        
        return red << 16 | green << 8 | blue;
    }
    
    private void write(String filename){
        BufferedImage image = new BufferedImage(inputWidth,inputHeight,BufferedImage.TYPE_INT_BGR);
        double[] redSignal = new double[inputSize];
        double[] greenSignal = new double[inputSize];
        double[] blueSignal = new double[inputSize];
        int rgb;
        for(int row=0;row+windowSize<inputWidth;row += windowSize){
            for(int col=0;col+windowSize<inputHeight;col+=windowSize){
                int address=0;
                for(int i=0;i<windowSize;++i){
                    for(int j=0;j<windowSize;++j){
                        rgb = inputImage.getRGB(row + i, col + j);
                        redSignal[address] = getRed(rgb)/255.0;
                        greenSignal[address] = getGreen(rgb)/255.0;
                        blueSignal[address] = getBlue(rgb)/255.0;
                        ++address;
                    }
                }
                redNetwork.processInput(redSignal);
                greenNetwork.processInput(greenSignal);
                blueNetwork.processInput(blueSignal);
                redSignal = redNetwork.getOutput();
                greenSignal = greenNetwork.getOutput();
                blueSignal = blueNetwork.getOutput();
                address=0;
                for(int i=0;i<windowSize;++i){
                    for(int j=0;j<windowSize;++j){
                        rgb = getRgb((int)(redSignal[address]*255),(int)(greenSignal[address]*255),(int)(blueSignal[address]*255));
                        image.setRGB(row +i, col+j, rgb );
                        ++address;
                    }
                }
            }
        }
        try {
            ImageIO.write(image, "jpg",new File(filename));
        } catch (IOException e) {
           System.out.println("Could not write file " + filename);
        }
    }
    
    public static void main(String[] arg){
        double error;
        double maxError = 1.0;
        NewImageNetwork network = new NewImageNetwork("/home/ochavoya/adelita.jpg");
        do{
        error = network.train(0.1,maxError);
        System.out.println(error);
        network.write("/home/ochavoya/adelita_copy.jpg");
        }while(error> maxError);
    }
}
