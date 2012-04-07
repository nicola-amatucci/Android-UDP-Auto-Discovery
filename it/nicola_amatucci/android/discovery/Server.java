/*    
    Copyright (C) 2012  Nicola Amatucci

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/

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
