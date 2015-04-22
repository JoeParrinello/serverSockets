import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

class Router2 {

    private final Integer myRouterNumber = 2;
    private Map<Integer, String> myLeastCostPathInterfaces;
    private Map<Integer, Integer> myLeastCostPathWeights;
    private Map<Integer, String>  myHostnames;
    private boolean ReadyForUpdate = false;

    public Router2(){

        myLeastCostPathInterfaces = new HashMap<Integer, String>();
        myLeastCostPathWeights = new HashMap<Integer, Integer>();
        myHostnames = new HashMap<Integer, String>();

        myLeastCostPathInterfaces.put(0,"I2");
        myLeastCostPathWeights.put(0,3);
        myHostnames.put(0,"127.0.0.1");

        myLeastCostPathInterfaces.put(1,"I0");
        myLeastCostPathWeights.put(1,1);
        myHostnames.put(1,"127.0.0.1");

        //My Own, if this Router is Two.
        myLeastCostPathInterfaces.put(2,"local");
        myLeastCostPathWeights.put(2,0);

        myLeastCostPathInterfaces.put(3,"I1");
        myLeastCostPathWeights.put(3,2);
        myHostnames.put(3,"127.0.0.1");


        try {
            ServerSocket serverSocket = new ServerSocket(8001 + myRouterNumber);

            while(true) {
                new ServerThread(serverSocket.accept()).run();
            }
        } catch (IOException e){
            e.printStackTrace();
        }


    }

    private synchronized void calculateNewLeastCostPath(Integer Router, Map<Integer, Integer> Weights) {
        Integer baseValueForPath = myLeastCostPathWeights.get(Router);
        boolean changesMade = false;

        for (Map.Entry<Integer, Integer> entry : Weights.entrySet()) {
            if (!myLeastCostPathWeights.containsKey(entry.getKey()) || entry.getValue() + baseValueForPath < myLeastCostPathWeights.get(entry.getKey())) {
                changesMade = true;
                myLeastCostPathWeights.put(entry.getKey(), entry.getValue() + baseValueForPath);
                myLeastCostPathInterfaces.put(entry.getKey(), myLeastCostPathInterfaces.get(Router));
            }
        }
        if (!ReadyForUpdate){
            ReadyForUpdate = changesMade;
        }
    }

    public static void main(String argv[]){
        new Router2();

    }


    private class ServerThread extends Thread{

        private Socket connectionSocket;

        public ServerThread(Socket connectionSocket){
            this.connectionSocket = connectionSocket;
        }

        public void run(){


            try {
                ObjectInputStream in = new ObjectInputStream(connectionSocket.getInputStream());

                Integer routerNumber = (Integer)(in.readObject());
                Map<Integer, String> leastCostPathInterfaces = (Map<Integer, String>)(in.readObject());
                Map<Integer, Integer> leastCostPathWeight = (Map<Integer, Integer>)(in.readObject());

                calculateNewLeastCostPath(routerNumber, leastCostPathWeight);

                String command = (String)in.readObject();

                if (command.equals("UPDATE")){
                    for(Map.Entry<Integer,String> entry: myHostnames.entrySet()){
                        new UpdaterThread(entry.getValue(), entry.getKey(), entry.getKey()==(routerNumber+2)%4).run();
                    }
                }

                connectionSocket.close();
            } catch(IOException e){
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }


    }

    private class UpdaterThread extends Thread {
        private Socket connectionSocket;
        private boolean UpdateLauncher;

        public UpdaterThread(String Hostname, Integer RouterNumber, Boolean UpdateLauncher){
            try {
                connectionSocket = new Socket(Hostname, 8001+RouterNumber);
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.UpdateLauncher = UpdateLauncher;
        }

        public void run(){
            try {
                ObjectOutputStream out = new ObjectOutputStream(connectionSocket.getOutputStream());
                out.writeObject(myRouterNumber);
                out.writeObject(myLeastCostPathInterfaces);
                out.writeObject(myLeastCostPathWeights);
                out.writeObject(UpdateLauncher ? "UPDATE" : "TERMINATE");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
