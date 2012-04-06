# Description

Basic Android Client/Server Library for finding a Local Network Server IP throught auto-discovery

# Using client

	WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
	ArrayList<String[]> ret = Client.findServer(wifiManager, "MYAPP_TOKEN");

stringArray[0] is the name, stringArray[1] is the ip

# Using server

	it.nicmaxdev.discovery.Server("NICOLA", "MYAPP_TOKEN");

# Thanks

	http://code.google.com/p/boxeeremote/wiki/AndroidUDP for getBroadcastAddress() function

Have fun!
