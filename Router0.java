import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

class Router0 {

    private final Integer myRouterNumber = 0;
    private Map<Integer, String> myLeastCostPathInterfaces;
    private Map<Integer, Integer> myLeastCostPathWeights;
    private Map<Integer, String>  myHostnames;
    private boolean ReadyForUpdate = true;

    public Router0(){

        myLeastCostPathInterfaces = new HashMap<Integer, String>();
        myLeastCostPathWeights = new HashMap<Integer, Integer>();
        myHostnames = new HashMap<Integer, String>();


        //My Own, if this Router is Zero.
        myLeastCostPathInterfaces.put(0,"local");
        myLeastCostPathWeights.put(0,0);

        myLeastCostPathInterfaces.put(1,"I0");
        myLeastCostPathWeights.put(1,1);
        myHostnames.put(1,"osl24.njit.edu");

        myLeastCostPathInterfaces.put(2,"I1");
        myLeastCostPathWeights.put(2,3);
        myHostnames.put(2,"osl17.njit.edu");

        myLeastCostPathInterfaces.put(3,"I2");
        myLeastCostPathWeights.put(3,7);
        myHostnames.put(3,"osl31.njit.edu");

        output();

        for(Map.Entry<Integer,String> entry: myHostnames.entrySet()){
            new UpdaterThread(entry.getValue(), entry.getKey(), entry.getKey()==1).run();
        }

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
        if(changesMade){
            ReadyForUpdate = changesMade;
            output();
        }
    }

    public static void main(String argv[]){
        new Router0();

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

                if (command.equals("UPDATE") && ReadyForUpdate){
                    ReadyForUpdate = false;
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



    private void output(){
        System.out.printf("|R%-10c|I%-9c|Weight%n",'#','#');
        for(int i = 0; i < 4; i++){
            System.out.printf("|R%-10d|%-10s|%-10d%n", i, myLeastCostPathInterfaces.get(i), myLeastCostPathWeights.get(i));
        }
    }


}