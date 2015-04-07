import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

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

        private final Integer myRouterNumber = 1;
        private Map<Integer, String> myLeastCostPathInterfaces;
        private Map<Integer, Integer> myLeastCostPathWeights;

        public ServerThread(Socket connectionSocket){
            this.connectionSocket = connectionSocket;
        }

        public void run(){
            myLeastCostPathInterfaces = new HashMap<Integer, String>();
            myLeastCostPathWeights = new HashMap<Integer, Integer>();

            //My Own, if this Router is Zero.
            myLeastCostPathInterfaces.put(0,"I2");
            myLeastCostPathWeights.put(0,1);

            myLeastCostPathInterfaces.put(1,"local");
            myLeastCostPathWeights.put(1,0);

            myLeastCostPathInterfaces.put(2,"I0");
            myLeastCostPathWeights.put(2,1);

            myLeastCostPathInterfaces.put(3,"I99");
            myLeastCostPathWeights.put(3,Integer.MAX_VALUE/2);

            try {

                ObjectOutputStream out = new ObjectOutputStream(connectionSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(connectionSocket.getInputStream());

                out.writeObject(myRouterNumber);
                out.writeObject(myLeastCostPathInterfaces);
                out.writeObject(myLeastCostPathWeights);


                System.out.println("\nServer Response");

                Integer routerNumber = (Integer)(in.readObject());
                Map<Integer, String> leastCostPathInterfaces = (Map<Integer, String>)(in.readObject());
                Map<Integer, Integer> leastCostPathWeight = (Map<Integer, Integer>)(in.readObject());

                if(calculateNewLeastCostPath(routerNumber, leastCostPathWeight)){
                    System.out.println("Should Send Update Here!");
                }


                connectionSocket.close();
            } catch(IOException e){
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        private boolean calculateNewLeastCostPath(Integer Router, Map<Integer, Integer> Weights){
            Integer baseValueForPath = myLeastCostPathWeights.get(Router);
            boolean changesMade = false;

            for (int i = 0; i<4; i++){
                if (Weights.get(i)+baseValueForPath < myLeastCostPathWeights.get(i)) {
                    changesMade = true;
                    myLeastCostPathWeights.replace(i, Weights.get(i)+baseValueForPath);
                    myLeastCostPathInterfaces.replace(i, myLeastCostPathInterfaces.get(Router));
                }
            }

            return changesMade;
        }
    }
}
