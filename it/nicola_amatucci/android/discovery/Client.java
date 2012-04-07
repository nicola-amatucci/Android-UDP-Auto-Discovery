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
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import android.net.wifi.WifiManager;
import android.util.Log;

public class Client {
	public static int DEFAULT_PORT = 19876;
	
	public static ArrayList<String[]> findServer(WifiManager mWifi) {
		return Client.findServer(mWifi, Server.DEFAULT_PORT, Server.DEFAULT_TOKEN);
	}

	public static ArrayList<String[]> findServer(WifiManager mWifi, String token) {
		return Client.findServer(mWifi, Server.DEFAULT_PORT, token);
	}

	public static ArrayList<String[]> findServer(WifiManager mWifi, int port, String token)
	{
		ArrayList<String[]> ret = new ArrayList<String[]>();
		
		try
		{
			DatagramSocket clientSocket = new DatagramSocket(DEFAULT_PORT);
			clientSocket.setBroadcast(true);
			InetAddress IPAddress = Utils.getBroadcastAddress(mWifi);
			Log.v("DISCOVERY_CLIENT", "broadcast addr " + IPAddress.getHostAddress());
	        byte[] receiveData = new byte[128];
	        byte[] sendData = new byte[128];
			
			sendData = token.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
			Log.v("DISCOVERY_CLIENT", "sent " + token);
			clientSocket.send(sendPacket);			
						
			long t1 = System.currentTimeMillis();
			while( System.currentTimeMillis() - t1 <= 4000 ) // 4 secondi
			{
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				
				try
				{					
					clientSocket.setSoTimeout(500);
					clientSocket.receive(receivePacket);
					
					if (receivePacket.getAddress() != null && receivePacket.getAddress().getHostAddress() != null)
					{
						String[] server = new String[2];
						
						server[0] = new String(receivePacket.getData());
						server[1] = receivePacket.getAddress().getHostAddress();
						
						if (server[0] != null)
						{
							server[0] = server[0].trim().substring(0, receivePacket.getLength()).trim();
							Log.v("DISCOVERY_CLIENT", "discovered " + server[0] + "," + server[1]);
						}
						
						boolean add = true;
						if (ret.size() > 0)
						{
							for (String[] s : ret)
							{
								if (s != null && s[1] != null && server[1] != null && s[1].equals(server[1]))
								{
									add = false;
									break;
								}
							}
						}
						
						if (add) {
							ret.add(server);
						}
					}
					
				}
				catch(SocketTimeoutException tex){ /* ignored */ }				
			}
			
			clientSocket.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			Log.e("DISCOVERY_CLIENT", ex.toString());
		}
		
		return ret;
	}
}
