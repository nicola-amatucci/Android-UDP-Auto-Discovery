package it.nicola_amatucci.android.discovery;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.net.wifi.WifiManager;
import android.util.Log;

public class Server extends Thread
{
	public static String DEFAULT_TOKEN = "DISCOVERY_DEFAULT";
	public static int DEFAULT_PORT = 9876;
	String token;
	String nome;
	int port;
	
	public Server(String nome)
	{
		this (nome, DEFAULT_PORT, DEFAULT_TOKEN);
	}
	
	public Server(String nome, String token)
	{
		this (nome, DEFAULT_PORT, token);
	}
	
	public Server(String nome, int port)
	{
		this (nome, port, DEFAULT_TOKEN);
	}	
	
	public Server(String nome, int port, String token)
	{
		this.nome = nome;		
		this.token = token;
		this.port = port;
		
		this.start();
	}

	public void disconnect()
	{
		this.interrupt();
	}
	
	public void run()
	{
		Log.v("DISCOVERY_SERVER", "SERVER STARTED");
		DatagramSocket serverSocket = null;
		
		try
		{
	        serverSocket = new DatagramSocket(port);
	        byte[] receiveData;
	        byte[] sendData;
			
	        while (this.isInterrupted() == false)
	        {
	        	receiveData = new byte[128];
	        	sendData = new byte[128];
	        	
	        	try
	        	{
	        		Log.v("DISCOVERY_SERVER", "LISTENING");
					DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			        serverSocket.receive(receivePacket);
			        String sentence = new String( receivePacket.getData());
			        
			        if (sentence != null)
			        	Log.v("DISCOVERY_SERVER", "RECEIVED: " + sentence.substring(0, receivePacket.getLength()).trim());
			        
			        if (sentence != null && sentence.substring(0, receivePacket.getLength()).trim().equals(token))
			        {
			        	Log.v("DISCOVERY_SERVER", "SEND '" + nome +"' to " + receivePacket.getAddress().getHostAddress() + ":" + receivePacket.getPort());        
				        sendData = nome.getBytes();
				        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
				        serverSocket.send(sendPacket);
			        }
			        
	        	}
	        	catch (Exception ex)
	        	{
	        		ex.printStackTrace();
	        		Log.e("DISCOVERY_SERVER", ex.toString());
	        	}
	        }
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Log.e("DISCOVERY_SERVER", e.toString());
		}
		finally
		{
			try { if (serverSocket != null) serverSocket.close(); } catch(Exception ex){}
		}
			
		
	}
}
