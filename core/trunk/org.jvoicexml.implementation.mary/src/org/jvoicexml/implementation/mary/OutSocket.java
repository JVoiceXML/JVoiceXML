package org.jvoicexml.implementation.mary;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;



public class OutSocket {

	int port;
	ServerSocket socket;
	PrintWriter output = null;
	BufferedReader input = null;
	private final Socket clientSocket;

	
	
	
	
	public OutSocket(int port) throws Exception{
		
		System.out.println("creating new Socket");
		
		this.port=port;
		
		  
	    clientSocket = new Socket(InetAddress.getLocalHost(),9450);
        output = new PrintWriter(clientSocket.getOutputStream());
        input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	        
	      
	}
	

	public void sendText(String text){
	
		output.println(text);
		output.flush();
	}
	
	
	
	
	
}


