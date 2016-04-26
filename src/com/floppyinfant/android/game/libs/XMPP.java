package com.floppyinfant.android.game.libs;

public class XMPP {
	
	/* XMPP Library - Smack
	 * ====================
	 * @see smack_4_1_6/releasedocs/documentation/index.html
	 */

	/*
	AbstractXMPPConnection connection = new XMPPTCPConnection("mtucker", "password", "jabber.org"); 
	connection.connect().login();

	Chat chat = ChatManager.getInstanceFor(connection) .createChat("jsmith@jivesoftware.com", new MessageListener() {

		public void processMessage(Chat chat, Message message) {
		    System.out.println("Received message: " + message);
		}
	}); 

	chat.sendMessage("Howdy!");


	// Create a connection to the jabber.org server.
	AbstractXMPPConnection conn1 = **new** XMPPTCPConnection("username", "password" "jabber.org");
	conn1.connect();

	// Create a connection to the jabber.org server on a specific port.
	XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
	  .setUsernameAndPassword("username", "password")
	  .setServiceName("jabber.org")
	  .setHost("earl.jabber.org")
	  .setPort("8222")
	  .build();

	AbstractXMPPConnection conn2 = **new** XMPPTCPConnection(config);
	conn2.connect();



	// Create a new presence. Pass in false to indicate we're unavailable._
	Presence presence = new Presence(Presence.Type.unavailable);
	presence.setStatus("Gone fishing");
	// Send the packet (assume we have an XMPPConnection instance called "con").
	con.sendStanza(presence);
	*/
	
}
