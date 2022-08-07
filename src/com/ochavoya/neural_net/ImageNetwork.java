package com.ochavoya.neural_net;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import javax.imageio.ImageIO;

import com.ochavoya.neural_net.FeedForwardNetwork;

public class ImageNetwork implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private int    inputWidth;
    private int    inputHeight;
    private double scale = 1.0;
    private BufferedImage inputImage;
    private FeedForwardNetwork greenNetwork;
    private FeedForwardNetwork   redNetwork;
    private FeedForwardNetwork  blueNetwork;
    private int[] nNeuron = new int[]{2,32,16,4,1};
    
    public void setScale(double scale){
        this.scale = scale;
    }
    
    public ImageNetwork(String filename){
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
    
    private void reset(){
        redNetwork.reset();
        greenNetwork.reset(); 
        blueNetwork.reset();
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
    
    private double train(double alpha,double maxError){
        int rgb;
        double[] signal = new double[1];
        long counter = 0 ;
        double error = 0 ;
        int i,j;
        do{
            i = (int)(inputWidth * Math.random());
            j = (int)(inputHeight * Math.random());
            reset();
            redNetwork.processInput(new double[]{(double)i/inputWidth,(double)j/inputHeight});
            greenNetwork.processInput(new double[]{(double)i/inputWidth,(double)j/inputHeight});
            blueNetwork.processInput(new double[]{(double)i/inputWidth,(double)j/inputHeight});
            rgb       = inputImage.getRGB(i, j);
            signal[0] =   getRed(rgb)/255.0;
            redNetwork.updateGradient(signal);
            signal[0] = getGreen(rgb)/255.0;
            greenNetwork.updateGradient(signal);
            signal[0] =  getBlue(rgb)/255.0;
            blueNetwork.updateGradient(signal);
            error    += commit(alpha);
            ++counter;
            if( counter % inputWidth == 0 )
                System.out.println(counter + "->" + error/counter);
        }
         while( counter < (long) inputWidth * inputHeight );
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
        int outputWidth =  (int)(scale * inputWidth  );
        int outputHeight = (int)(scale * inputHeight );
        int red,green,blue;
        BufferedImage image = new BufferedImage(outputWidth,outputHeight,BufferedImage.TYPE_INT_BGR);
        for(int i=0;i<outputWidth;++i){
            for(int j=0;j<outputHeight;++j){
                reset();
                double[] output;
                redNetwork.processInput(new double[]{(double)i/outputWidth,(double)j/outputHeight});
                output = redNetwork.getOutput();
                red    = (int) (output[0]*255);
                greenNetwork.processInput(new double[]{(double)i/outputWidth,(double)j/outputHeight});
                output = greenNetwork.getOutput();
                green  = (int) (output[0]*255);
                blueNetwork.processInput(new double[]{(double)i/outputWidth,(double)j/outputHeight});
                output = blueNetwork.getOutput();
                blue   = (int) (output[0]*255);
                image.setRGB(i, j,getRgb(red,green,blue));
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
        double maxError = 0.05;
        ImageNetwork network = new ImageNetwork("/home/ochavoya/adelita.jpg");
        do{
        error = network.train(0.05,maxError);
        System.out.println(error);
        if(error<1.5*maxError){
            network.setScale(4.0);
        }
        network.write("/home/ochavoya/adelita_copy.jpg");
        }while(error> maxError);
    }
}
