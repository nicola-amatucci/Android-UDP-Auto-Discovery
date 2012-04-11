# Description

Basic Android Client/Server Library for finding a Local Network Server IP throught auto-discovery

# Using client

	WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
	ArrayList<DiscoveryServerInfo> ret = Client.findServer(wifiManager, "MYAPP_TOKEN");

# Using server

	it.nicmaxdev.discovery.Server("NICOLA", 123456, "MYAPP_TOKEN");

# Thanks

	http://code.google.com/p/boxeeremote/wiki/AndroidUDP for getBroadcastAddress() function

Have fun!
