package com.ochavoya.powerball;

import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

public class PowerBallDataCollector {

    private ArrayList<int[]> data;
    private ArrayList<byte[]> trainingData;

    private static int regularNumber = 69;
    private static int powerBallNumber = 35;
    private double[] regularDistribution   = new double[regularNumber + 1];
    private double[] powerBallDistribution = new double[powerBallNumber + 1];

    private void setUpRegularDistribution() {
        int i;
        for (i = 0; i < regularDistribution.length; ++i)
            regularDistribution[i] = 0.0;
        for (int[] x : data) {
            for (i = 0; i < 5; ++i) {
                if( x[i]  > regularNumber){
                    System.out.println("Data Error: " + x[i]);
                }
                regularDistribution[x[i]] += 1.0;
            }
        }
        for (i = 0; i < regularDistribution.length; ++i)
            regularDistribution[i] /= (5 * data.size());
    }

    private void setUpPowerBallDistribution() {
        int i;
        for (i = 0; i < powerBallDistribution.length; ++i) {
            powerBallDistribution[i] = 0.0;
        }
        for (int[] x : data) {
            powerBallDistribution[x[5]] += 1.0;
        }
        for (i = 0; i < powerBallDistribution.length; ++i) {
            powerBallDistribution[i] /= data.size();
        }
    }

    private int sample(double[] distribution) {
        double random = Math.random();
        int i;
        for (i = 1; i < distribution.length; ++i) {
            random -= distribution[i];
            if (random < 0)
                break;
        }
        i = (i == distribution.length) ? i - 1 : i;
        distribution[i] = 0;
        double sum = 0.0;
        for (int j = 1; j < distribution.length; ++j)
            sum += distribution[j];
        for (int j = 1; j < distribution.length; ++j)
            distribution[j] /= sum;
        return i;
    }

    String getSample() {
        double[] distribution = regularDistribution.clone();
        ArrayList<Integer> sample = new ArrayList<Integer>();

        for (int i = 0; i < 5; ++i) {
            sample.add(sample(distribution));
        }

        Collections.sort(sample);

        distribution = powerBallDistribution.clone();

        sample.add(sample(distribution));

        return sample.toString();
    }

    private static void displayArray(byte[] array) {
        System.out.print("[");
        if (array.length >= 1) {
            System.out.print(array[0]);
            for (int i = 1; i < array.length; ++i) {
                System.out.print("," + array[i]);
            }
        }
        System.out.println("]");
    }

    private static void displayArray(int[] array) {
        System.out.print("[");
        if (array.length >= 1) {
            System.out.print(array[0]);
            for (int i = 1; i < array.length; ++i) {
                System.out.print("," + array[i]);
            }
        }
        System.out.println("]");
    }

    public PowerBallDataCollector() {
        ArrayList<String> history = new DataCollector(
                "http://www.powerball.com/powerball/winnums-text.txt")
                .getContent();
        data = new ArrayList<int[]>();
        history.remove(0);
        for (int i = 0; i < history.size(); ++i) {
            StringTokenizer main = new StringTokenizer(history.get(i));
            if (!(main.countTokens() >= 7))
                continue;
            StringTokenizer date = new StringTokenizer(main.nextToken(), "/");
            int month = Integer.parseInt(date.nextToken());
            int day = Integer.parseInt(date.nextToken());
            int year = Integer.parseInt(date.nextToken());
            if (year == 2012 && month == 1 && day < 18)
                break;
            int[] record = new int[6];
            for (int j = 0; j < 6; ++j) {
                record[j] = Integer.parseInt(main.nextToken());
                System.out.print(record[j] + "\t");
            }
            System.out.println();
            data.add(record);
        }
        if (data.size() == 0) {
            System.out.println("No data available");
            System.exit(0);
        }
        setUpRegularDistribution();
        setUpPowerBallDistribution();
        trainingData = new ArrayList<byte[]>();
        int dataNumber = data.size();
        int trainingDataLength = 2 * (regularNumber + powerBallNumber);
        for (int i = 0; i < dataNumber - 1; ++i) {
            int[] current = data.get(i);
            int[] next = data.get(i + 1);
            byte[] sample = new byte[trainingDataLength];
            for (int j = 0; j < trainingDataLength; ++j) {
                sample[j] = 0;
            }
            for (int j = 0; j < 5; ++j) {
                sample[current[j] - 1] = 1;
                sample[regularNumber + powerBallNumber + next[j] - 1] = 1;
            }
            sample[regularNumber + current[5] - 1] = 1;
            sample[2 * regularNumber + powerBallNumber + next[5] - 1] = 1;
            displayArray(current);
            displayArray(next);
            displayArray(sample);
            trainingData.add(sample);
        }
    }

    public ArrayList<byte[]> getTrainingData() {
        return trainingData;
    }

    public static void main(String[] arg) {
        PowerBallDataCollector collector = new PowerBallDataCollector();
        for (int i = 0; i < 5; ++i) {
            System.out.println(collector.getSample());
        }
    }
}
