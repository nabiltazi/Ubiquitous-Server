/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Nabil
 */
package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

public class SimpleConnectionHandler extends Thread
{
    private Socket clientSocket;
    private HashMap<String, String> eventsMap;
    private HashMap<String, String> beaconsMap;
    
    public void initEvents(){
    	this.eventsMap = new HashMap<>();
    	
    	this.eventsMap.put("0001", "CampNou");
    	this.eventsMap.put("0002", "Louvre");
    	this.eventsMap.put("0003", "NightClub");
    }
    
    public void initBeacons(){
    	this.beaconsMap = new HashMap<>();
    	
    	this.beaconsMap.put("key1", "JuvBar");
    	this.beaconsMap.put("B940", "MonaLisa");
    	this.beaconsMap.put("key3", "MarieLaveau");
    }
    


    public SimpleConnectionHandler(Socket clientSocket)
    {
        this.clientSocket = clientSocket;
    }

    public void run()
    {
        BufferedInputStream in;
        BufferedOutputStream out;
        this.initEvents();
        this.initBeacons();
        
        try
        {
            in = new BufferedInputStream(clientSocket.getInputStream());
            out = new BufferedOutputStream(clientSocket.getOutputStream());
        } catch (IOException e)
        {
            System.out.println(e.toString());
            return;
        }

        try
        {
            int n;
            
            Boolean audioPlaying = false;
            
            while(true){
            
             byte[] msg = new byte[4096];
             int bytesRead = 0;
             String sub = null;

            while ((n = in.read(msg)) != -1)
            {
                bytesRead += n;
                if (bytesRead == 4096)
                {
                    break;
                }
                if (in.available() == 0)
                {
                    break;
                }
            }
            
            String msgString = new String(msg);
            sub = msgString.substring(0, 4);
            
            
            if((bytesRead != 0) && (!audioPlaying)){
	            if(eventsMap.containsKey(sub)){
	            	System.out.println("We found the event-key in the map");
	            	out.write(eventsMap.get(sub).getBytes());
	
	            }else if(beaconsMap.containsKey(sub)){
	            		audioPlaying = true;
	                	System.out.println("We found the beacon-key in the map");
	                	out.write(beaconsMap.get(sub).getBytes());
	            }
	            
	            else {
	            	System.out.println("Wrong key");
	            	String errorMessage = "ERROR";
	            	out.write(errorMessage.getBytes());
	            }
            }
            out.flush();
            
            }//end of infinite loop

        } catch (IOException e)
        {
            System.out.println(e.toString());
        }

        try
        {
            out.close();
            in.close();
            clientSocket.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}