import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

class Router0 {

    public final Integer myRouterNumber = 0;
    public Map<Integer, String> myLeastCostPathInterfaces;
    public Map<Integer, Integer> myLeastCostPathWeights;

    public Router0(){
        myLeastCostPathInterfaces = new HashMap<Integer, String>();
        myLeastCostPathWeights = new HashMap<Integer, Integer>();

        //My Own, if this Router is Zero.
        myLeastCostPathInterfaces.put(0,"local");
        myLeastCostPathWeights.put(0,0);

        myLeastCostPathInterfaces.put(1,"I0");
        myLeastCostPathWeights.put(1,1);

        myLeastCostPathInterfaces.put(2,"I1");
        myLeastCostPathWeights.put(2,3);

        myLeastCostPathInterfaces.put(3,"I2");
        myLeastCostPathWeights.put(3,7);

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Hostname: ");
        try {
            Socket clientSocket = new Socket(inFromUser.readLine(), 8001);

            output();

            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

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

            output();

            clientSocket.close();
        } catch(IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private boolean calculateNewLeastCostPath(Integer Router, Map<Integer, Integer> Weights){
        Integer baseValueForPath = myLeastCostPathWeights.get(Router);
        boolean changesMade = false;

        for(Map.Entry<Integer, Integer> entry: Weights.entrySet()){
            if(!myLeastCostPathWeights.containsKey(entry.getKey()) || entry.getValue()+baseValueForPath < myLeastCostPathWeights.get(entry.getKey())){
                changesMade = true;
                myLeastCostPathWeights.put(entry.getKey(),entry.getValue()+baseValueForPath);
                myLeastCostPathInterfaces.put(entry.getKey(), myLeastCostPathInterfaces.get(Router));
            }
        }

        return changesMade;
    }

    private void output(){
        System.out.printf("|R%-10c|I%-9c|Weight%n",'#','#');
        for(int i = 0; i < 4; i++){
            System.out.printf("|R%-10d|%-10s|%-10d%n", i, myLeastCostPathInterfaces.get(i), myLeastCostPathWeights.get(i));
        }
    }

    public static void main(String argv[]) {
        new Router0();
    }
}
