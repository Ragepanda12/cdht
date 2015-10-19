import java.io.*;
import java.net.*;
import java.util.*;

/*
 * Server to process ping requests over UDP. 
 * The server sits in an infinite loop listening for incoming UDP packets. 
 * When a packet comes in, the server simply sends the encapsulated data back to the client.
 */

public class cdht
{
   private Peer p;
   private final static int portPlus = 50000;   
   public cdht(int self){
	   p = new Peer(self);
   }
   public Peer getPeer(){
	   return p;
   }

   private void tcpFile(int requestNo, cdht system){

   }
   public static void main(String[] args) throws Exception{
      // Get command line argument.
      if (args.length != 3) {
         System.out.println("Required arguments: port child1port child2port");
         return;
      }
      final cdht system = new cdht(Integer.parseInt(args[0]));
      system.getPeer().setFirstChild(Integer.parseInt(args[1]) + portPlus);
      system.getPeer().setSecondChild(Integer.parseInt(args[2]) + portPlus);
      final DatagramSocket socket = new DatagramSocket(system.getPeer().getPort());
      
      Thread inputListen = new Thread(new Runnable(){
         @Override
         public void run(){
            while(true){
               Scanner sc = new Scanner(System.in);
               String s = sc.nextLine();
               if(s.contains("quit")){
                  system.tcpQuit(1, system);
                  system.tcpQuit(2, system);
                  System.exit(0);
               }
               else if(s.contains("request")){
                  int requestNo = ((Integer.parseInt(s.split(" ")[1]) + 1) % 255);
                  system.tcpFile(requestNo, system);
               }
            }
         }
      });

      Thread tcpServer = new Thread(new Runnable(){
         @Override
         public void run(){
		      int serverPort = system.getPeer().getPort(); 
		      ServerSocket welcomeSocket = null;
			   try {
		   		welcomeSocket = new ServerSocket(serverPort);
		   	} catch (IOException e) {
   				e.printStackTrace();
	   		}
		      while (true){
		          // accept connection from connection queue
		         Socket connectionSocket = null;
				   try {
					   connectionSocket = welcomeSocket.accept();
				   } catch (IOException e) {	
					   e.printStackTrace();
				   }
		          // create read stream to get input
		          BufferedReader inFromClient = null;
				   try {
					   inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
				   } catch (IOException e) {
					   e.printStackTrace();
				   }
		         String clientSentence = null;
		         try {
					   clientSentence = inFromClient.readLine();
				   } catch (IOException e) {					
					   e.printStackTrace();
				   }
		          // send reply
               if(clientSentence.contains("quitting")){
                  int firstSuccess = Integer.parseInt(clientSentence.split(" ")[1]);
                  int secondSuccess = Integer.parseInt(clientSentence.split(" ")[2]);
                  int port = Integer.parseInt(clientSentence.split(" ")[3]);
                  if(system.getPeer().getFirstChild() == port){
                     system.getPeer().setFirstChild(firstSuccess);
                     system.getPeer().setSecondChild(secondSuccess);
                     System.out.println("Peer " + (port - portPlus) + " will depart from the network.");
                     System.out.println("My first successor is now peer " + (system.getPeer().getFirstChild() - portPlus));
                     System.out.println("My second successor is now peer " + (system.getPeer().getSecondChild() - portPlus));
                  }
                  else if(system.getPeer().getSecondChild() == port){
                     system.getPeer().setSecondChild(firstSuccess);
                     System.out.println("Peer " + (port - portPlus) + " will depart from the network.");
                     System.out.println("My first successor is now peer " + (system.getPeer().getFirstChild() - portPlus));
                     System.out.println("My second successor is now peer " + (system.getPeer().getSecondChild() - portPlus));
                  }
               }
		         DataOutputStream outToClient = null;
				   try {
					   outToClient = new DataOutputStream(connectionSocket.getOutputStream());
				   } catch (IOException e) {
					   e.printStackTrace();
				   }
		         try {
                  String reply = "disconnect ok \n";
					   outToClient.writeBytes(reply);
				   } catch (IOException e) {	
					   e.printStackTrace();
				   }
		      } // end of while (true)
         }
      });

      Thread pingListen = new Thread(new Runnable(){
         @Override
         public void run(){
            while (true) {
               // Create a datagram packet to hold incomming UDP packet.
               DatagramPacket request = new DatagramPacket(new byte[1024], 1024);
   
               // Block until the host receives a UDP packet.
               try {
				      socket.receive(request);
			      } catch (IOException e1) {
      				e1.printStackTrace();
		      	}
            
               // Print the recieved data.
               try {
		            // Obtain references to the packet's array of bytes.
                  byte[] buf = request.getData();
                  ByteArrayInputStream bais = new ByteArrayInputStream(buf);
                  InputStreamReader isr = new InputStreamReader(bais);
                  BufferedReader br = new BufferedReader(isr);
                  String line = br.readLine();
                  InetAddress clientHost = request.getAddress();
                  int clientPort = request.getPort();
                  if(line.startsWith("PING")){
                     // Send reply.
                	   if(line.contains("first")){
                		   system.getPeer().setFirstPredecessor(clientPort);
                	   }
                	   else if(line.contains("second")){
                		   system.getPeer().setSecondPredecessor(clientPort);
                	   }
                     byte[] buffer = new byte[1024];
                     String string = "Received from " + clientPort;
                     buffer = string.getBytes();
                     DatagramPacket reply = new DatagramPacket(buffer, buffer.length, clientHost, clientPort);
                     socket.send(reply);
                     System.out.println("A ping request message was received from Peer " + (clientPort - portPlus));
                  }else{
                     System.out.println("A ping response message was received from Peer " + (clientPort - portPlus));
                  }
      			} catch (Exception e) {
		      		e.printStackTrace();
	      		}
            }
         }
      });
      Thread pingFirstChild = new Thread(new Runnable(){
         @Override
         public void run(){
            // Create a datagram socket for receiving and sending UDP packets
            // through the port specified on the command line.
            InetAddress server = null;
			   try {
				   server = InetAddress.getByName("127.0.0.1");
			   } catch (UnknownHostException e) {
				   e.printStackTrace();
			   }
            //String to send
            String str = "PING first \n";
            byte[] buf = new byte[1024];
            buf = str.getBytes();
            // Create a datagram packet to hold incomming UDP packet.
            DatagramPacket ping = new DatagramPacket(buf, buf.length, server, system.getPeer().getFirstChild());
            // Block until the host receives a UDP packet.
            while(true) {
               ping = new DatagramPacket(buf, buf.length, server, system.getPeer().getFirstChild());
               try {
				      socket.send(ping);
			      } catch (IOException e) {
				      e.printStackTrace();
			      }
               try {
				      Thread.sleep(2000);
			      } catch (InterruptedException e) {
				      e.printStackTrace();
			      }
            }
         }
      });
      Thread pingSecondChild = new Thread(new Runnable(){
         @Override
         public void run(){
            // Create a datagram socket for receiving and sending UDP packets
            // through the port specified on the command line.
            InetAddress server = null;
			   try {
				   server = InetAddress.getByName("127.0.0.1");
			   } catch (UnknownHostException e) {
				   e.printStackTrace();
			   }
            //String to send
            String str = "PING second \n";
            byte[] buf = new byte[1024];
            buf = str.getBytes();
            // Create a datagram packet to hold incomming UDP packet.
            DatagramPacket ping = new DatagramPacket(buf, buf.length, server, system.getPeer().getSecondChild());
            // Block until the host receives a UDP packet.
            while(true) {
               ping = new DatagramPacket(buf, buf.length, server, system.getPeer().getSecondChild());
               try {
				      socket.send(ping);
			      } catch (IOException e) {
				      e.printStackTrace();
			      }
               try {
				      Thread.sleep(5000);
			      } catch (InterruptedException e) {
				      e.printStackTrace();
			      }
            }
         }
      });
      pingListen.start();
      pingFirstChild.start();
      pingSecondChild.start();
      tcpServer.start();
      inputListen.start();
   }
   private void tcpQuit(int server, cdht system){
      String serverName = "localhost";
		InetAddress serverIPAddress = null;
      try {
		   serverIPAddress = InetAddress.getByName(serverName);
	   } catch (UnknownHostException e) {
		   e.printStackTrace();
	   }
      // get server port
      int serverPort;
      if(server == 1){
         serverPort = system.getPeer().getFirstPredecessor();
      }
      else{
         serverPort = system.getPeer().getSecondPredecessor();
      } 
      // create socket which connects to server
      Socket clientSocket = null;
	   try {
	      clientSocket = new Socket(serverIPAddress, serverPort);
	   } catch (IOException e) {
		   e.printStackTrace();
	   }
		String sentence = "quitting " + system.getPeer().getFirstChild() + " " + system.getPeer().getSecondChild() + " " + system.getPeer().getPort();

		// write to server
      DataOutputStream outToServer = null;
	   try {
		   outToServer = new DataOutputStream(clientSocket.getOutputStream());
	   } catch (IOException e) {		
			e.printStackTrace();
	   }
      try {
		   outToServer.writeBytes(sentence + '\n');
	   } catch (IOException e) {					
         e.printStackTrace();
	   }
		// create read stream and receive from server
	   BufferedReader inFromServer = null;
	   try {
		   inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	   } catch (IOException e) {	
         e.printStackTrace();
	   }
      String sentenceFromServer;
      try {
		   sentenceFromServer = inFromServer.readLine();
	   } catch (IOException e) {						
			e.printStackTrace();
	   }
      // close client socket
      try {
		   clientSocket.close();
	   } catch (IOException e) {
		   e.printStackTrace();
	   }   

   }
   /* 
    * Print ping data to the standard output stream.
    */
   private static void printData(DatagramPacket request) throws Exception
   {
      // Obtain references to the packet's array of bytes.
      byte[] buf = request.getData();

      // Wrap the bytes in a byte array input stream,
      // so that you can read the data as a stream of bytes.
      ByteArrayInputStream bais = new ByteArrayInputStream(buf);

      // Wrap the byte array output stream in an input stream reader,
      // so you can read the data as a stream of characters.
      InputStreamReader isr = new InputStreamReader(bais);

      // Wrap the input stream reader in a bufferred reader,
      // so you can read the character data a line at a time.
      // (A line is a sequence of chars terminated by any combination of \r and \n.) 
      BufferedReader br = new BufferedReader(isr);

      // The message data is contained in a single line, so read this line.
      String line = br.readLine();

      // Print host address and data received from it.
      System.out.println(
         "Received from " + 
         request.getAddress().getHostAddress() + 
         ": " +
         new String(line) );
   }
}

