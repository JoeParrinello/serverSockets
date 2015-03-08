import java.io.*;
import java.net.*;

class NetworkServer {

  public NetworkServer(){
    try {
      ServerSocket serverSocket = new ServerSocket(8001);

      while(true) {
        new ServerThread(serverSocket.accept()).run();
      }
    } catch (IOException e){
      e.printStackTrace();
    }


  }

  public static void main(String argv[]){
    new NetworkServer();

  }


  private class ServerThread extends Thread{

    private Socket connectionSocket;

    public ServerThread(Socket connectionSocket){
      this.connectionSocket = connectionSocket;
    }

    public void run(){
      try {
        BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

        DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
        for(int i=0; i<5; i++) {
          inFromClient.readLine();
        }
        outToClient.writeBytes("Router Number: 1\n");
        outToClient.writeBytes("Cost to Router 0: 1\n");
        outToClient.writeBytes("Cost to Router 1: 0\n");
        outToClient.writeBytes("Cost to Router 2: 1\n");
        outToClient.writeBytes("Cost to Router 3: N/A\n");
        System.out.println("Finished");
      } catch(IOException e){
        e.printStackTrace();
      }
    }
  }
}
