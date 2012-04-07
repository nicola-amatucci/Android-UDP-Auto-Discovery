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

    Credits to http://code.google.com/p/boxeeremote/wiki/AndroidUDP
*/

package it.nicola_amatucci.android.discovery;

import java.io.IOException;
import java.net.InetAddress;

import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class Utils {
	public static InetAddress getBroadcastAddress(WifiManager mWifi) throws IOException {
	    DhcpInfo dhcp = mWifi.getDhcpInfo();
	    if (dhcp == null) {
	      Log.d("DISCOVERY_CLIENT", "Could not get dhcp info");
	      return null;
	    }

	    int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
	    byte[] quads = new byte[4];
	    for (int k = 0; k < 4; k++)
	      quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
	    return InetAddress.getByAddress(quads);
	  }
}
