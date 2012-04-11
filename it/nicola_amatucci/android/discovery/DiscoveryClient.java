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
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import android.net.wifi.WifiManager;
import android.util.Log;

public class DiscoveryClient {
	public static int DEFAULT_PORT = 19876;
	
	public static ArrayList<DiscoveryServerInfo> findServer(WifiManager mWifi) {
		return DiscoveryClient.findServer(mWifi, DiscoveryServer.DEFAULT_PORT, DiscoveryServer.DEFAULT_TOKEN);
	}

	public static ArrayList<DiscoveryServerInfo> findServer(WifiManager mWifi, String token) {
		return DiscoveryClient.findServer(mWifi, DiscoveryServer.DEFAULT_PORT, token);
	}

	public static ArrayList<DiscoveryServerInfo> findServer(WifiManager mWifi, int port, String token)
	{
		ArrayList<DiscoveryServerInfo> ret = new ArrayList<DiscoveryServerInfo>();
		
		try
		{
			DatagramSocket clientSocket = new DatagramSocket();
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
						String discovered_name, discovered_ip;
						int discovered_port;
						
						String received = new String(receivePacket.getData());
						
						if (received != null)
						{
							received = received.trim().substring(0, receivePacket.getLength()).trim();
							StringTokenizer st = new StringTokenizer(received, ",");
							
							try
							{
								discovered_name = st.nextToken();							
								discovered_ip = receivePacket.getAddress().getHostAddress();
								discovered_port = Integer.parseInt(st.nextToken());
															
								Log.v("DISCOVERY_CLIENT", "discovered " + discovered_name + ", " + discovered_ip + ":" + discovered_port);
															
								boolean add = true;
								if (ret.size() > 0)
								{
									for (DiscoveryServerInfo dsi : ret)
									{
										if (dsi != null && dsi.ip.equals(discovered_ip))
										{
											add = false;
											break;
										}
									}
								}
								
								if (add) {
									ret.add( new DiscoveryServerInfo(discovered_name, discovered_ip, discovered_port) );
								}	
							}
							catch (NoSuchElementException nsee)
							{
								Log.v("DISCOVERY_CLIENT", nsee.getLocalizedMessage());
							}
							
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
