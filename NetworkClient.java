import java.io.*;
import java.net.*;
class NetworkClient {


  public NetworkClient(){
    BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
    System.out.print("Hostname: ");
    try {
      Socket clientSocket = new Socket(inFromUser.readLine(), 8001);
      DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
      BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

      outToServer.writeBytes("Router Number: 0\n");
      outToServer.writeBytes("Cost to Router 0: 0\n");
      outToServer.writeBytes("Cost to Router 1: 1\n");
      outToServer.writeBytes("Cost to Router 2: 3\n");
      outToServer.writeBytes("Cost to Router 3: 7\n");


      System.out.println("\nClient Information");
      System.out.print("Router Number: 0\n");
      System.out.print("Cost to Router 0: 0\n");
      System.out.print("Cost to Router 1: 1\n");
      System.out.print("Cost to Router 2: 3\n");
      System.out.print("Cost to Router 3: 7\n");


      System.out.println("\nServer Response");
      for(int i=0; i<5; i++){
        System.out.println(inFromServer.readLine());
      }

      clientSocket.close();
    } catch(IOException e){
      e.printStackTrace();
    }

  }

  public static void main(String argv[]) {
    new NetworkClient();
  }
}
