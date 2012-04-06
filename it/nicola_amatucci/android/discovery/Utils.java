package it.nicola_amatucci.android.discovery;

/**
 * Credits to http://code.google.com/p/boxeeremote/wiki/AndroidUDP
 */

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
