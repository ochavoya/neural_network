package com.ochavoya.powerball;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class DataCollector {
    private URL url;
    URLConnection connection;
    private ArrayList<String> content = new ArrayList<String>();
    
    public DataCollector(String address) {
        try{
            url = new URL(address);
            connection = url.openConnection();
            connection.connect();
            BufferedReader reader = 
                    new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
            String line;
            while( (line = reader.readLine()) != null){
                content.add(line);
            }
            reader.close();
        }catch(MalformedURLException mfurl){
            
        }
        catch(IOException ioe){
            
        }
    }
    
    public ArrayList<String> getContent(){
        return content;
    }
}
