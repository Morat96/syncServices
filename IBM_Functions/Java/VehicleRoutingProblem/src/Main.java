import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.net.URL;
import java.io.InputStream;
import java.io.FileOutputStream;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class Main {

    public static JsonObject main(JsonObject args) throws Exception {

        boolean vns = false;
        int k_opt = 1;
        int n_iter = 1;

        JsonObject response = new JsonObject();
        JsonObject header = new JsonObject();
        header.addProperty("Content-Type", "application/json");
        JsonObject body = new JsonObject();

        if (args.has("vns")) {
            vns = args.getAsJsonPrimitive("vns").getAsBoolean();
        }
        else {
            response.addProperty("statusCode", 400);
            response.add("headers", header);
            body.addProperty("message", "The parameter 'vns' must be defined!");
            response.add("body", body);
            return response;
        }
        if (args.has("k_opt")) {
            k_opt = args.getAsJsonPrimitive("k_opt").getAsInt();
        }
        if (args.has("n_iter")) {
            n_iter = args.getAsJsonPrimitive("n_iter").getAsInt();
        }

        String content = "";

        if (args.has("__ow_body")) {
            content = args.getAsJsonPrimitive("__ow_body").getAsString();
        }
        else {
            response.addProperty("statusCode", 400);
            response.add("headers", header);
            body.addProperty("message", "You must POST a VRP instance file!");
            response.add("body", body);
            return response;
        }

        BufferedWriter out = new BufferedWriter(new FileWriter("instance.txt"));

        try {
            out.write(content);
        }
        catch (IOException e) {
    		System.out.println("Exception Occurred:");
            e.printStackTrace();
        }
        finally
        {
            out.close();
        }
	  
        String name = "";
        int nodes = 0;
        int capacity = 0;
        int numberOfVehicles = 0;
        int depot_index = 0;

        // Supported file types
        String edge_type = "";

        ArrayList <Customer> customers = new ArrayList<>();

        try {
            BufferedReader in = new BufferedReader(new FileReader("instance.txt"));
            String line;
            boolean nodeCoordSection = false;
            boolean demandSection = false;
            boolean depotSection = false;

            while( (line = in.readLine()) != null ) {
                if( !line.equalsIgnoreCase("EOF") && !line.equalsIgnoreCase(" EOF") && !line.equals("") ) {

                    String[] wordsInLine = line.split(" ");

                    if( wordsInLine[0].equalsIgnoreCase("NAME")) {
                        name = wordsInLine[2];
                        continue;
                    }
                    if( wordsInLine[0].equalsIgnoreCase("DIMENSION")) {
                        nodes = Integer.parseInt(wordsInLine[2]);
                        continue;
                    }
                    if( wordsInLine[0].equalsIgnoreCase("CAPACITY")) {
                        capacity = Integer.parseInt(wordsInLine[2]);
                        continue;
                    }
                    if( wordsInLine[0].equalsIgnoreCase("VEHICLES")) {
                        numberOfVehicles = Integer.parseInt(wordsInLine[2]);
                        continue;
                    }
                    if( wordsInLine[0].equalsIgnoreCase("EDGE_WEIGHT_TYPE")) {
                        edge_type = wordsInLine[2];
                        System.out.println("EDGE_WEIGHT_TYPE: " + edge_type);
                        continue;
                    }

                    if( wordsInLine[0].equalsIgnoreCase("NODE_COORD_SECTION")) {
                        System.out.println("NODE_COORD_SECTION");
                        nodeCoordSection = true;
                        continue;
                    }

                    if( wordsInLine[0].equalsIgnoreCase("DEMAND_SECTION")) {
                        nodeCoordSection = false;
                        demandSection = true;
                        continue;
                    }

                    if( wordsInLine[0].equalsIgnoreCase("DEPOT_SECTION")) {
                        demandSection = false;
                        depotSection = true;
                        continue;
                    }

                    if (nodeCoordSection) {

                        StringTokenizer strTok = new StringTokenizer(line, " \t");
                        try {
                            strTok.nextToken(); // Discard the city number
                            if( edge_type.equals("EUC_2D") || edge_type.equals("GEO") ) {
                                double x = Double.valueOf( strTok.nextToken() ).doubleValue();
                                double y = Double.valueOf( strTok.nextToken() ).doubleValue();
                                Customer cust = new Customer();
                                cust.x = x;
                                cust.y = y;
                                customers.add(cust);
                            } else
                                throw new Exception( "Unrecognized file format!" );
                        } catch(NoSuchElementException e) {
                            throw new Exception( "Could not parse file " + "'" + "filename" + "'!" );
                        }
                    }

                    if (demandSection) {

                        //System.out.println(wordsInLine[0]);
                        StringTokenizer strTok = new StringTokenizer(line, " \t");
                        try {

                            int key = Integer.valueOf( strTok.nextToken() ).intValue();
                            int value = Integer.valueOf( strTok.nextToken() ).intValue();
                            customers.get(key-1).demand = value;
                            customers.get(key-1).ID = (key - 1);
                            customers.get(key-1).isRouted = false;
                        } catch(NoSuchElementException e) {
                            throw new Exception( "Could not parse file " + "'" + "filename" + "'!" );
                        }
                    }

                    if(depotSection) {

                        StringTokenizer strTok = new StringTokenizer(line, " \t");
                        try {
                            int value = Integer.valueOf( strTok.nextToken() ).intValue();
                            depot_index = value - 1;
                        } catch(NoSuchElementException e) {
                            throw new Exception( "Could not parse file " + "'" + "filename" + "'!" );
                        }
                    }
                }
            }
        } catch(FileNotFoundException e) {
            throw new Exception( "File " + "'" + "filename" + "'" + " not found in the current directory!" );
        } catch(IOException e) {
            throw new Exception( "Could not read from file " + "'" + "filename" + "'!" );
        }

        // Check the instance
        System.out.println("NAME: " + name);
        System.out.println("DIMENSION: " + nodes);
        System.out.println("CAPACITY: " + capacity);
        System.out.println("VEHICLES: " + numberOfVehicles);

        
        for(Customer cust: customers) {
            System.out.println("x: " + cust.x);
            System.out.println("y: " + cust.y);
            System.out.println("demand: " + cust.demand);
            System.out.println("ID: " + cust.ID);
            System.out.println("isRouted: " + cust.isRouted);
        }


        // This is a 2-D array which will hold the distances between node pairs
        // The [i][j] element of this array is the distance required for moving
        // from the i-th node of allNodes (node with id : i)
        // to the j-th node of allNodes list (node with id : j)
        double [][] distanceMatrix = new double [customers.size()][customers.size()];
        for (int i = 0 ; i < customers.size(); i++)
        {
            Customer from = customers.get(i);

            for (int j = 0 ; j < customers.size(); j++)
            {
                Customer to = customers.get(j);

                double Delta_x = (from.x - to.x);
                double Delta_y = (from.y - to.y);
                double distance = Math.sqrt((Delta_x * Delta_x) + (Delta_y * Delta_y));

                distance = Math.round(distance);

                distanceMatrix[i][j] = distance;

            }
        }

        Customer depot = new Customer();

        depot = customers.get(depot_index);

        customers.remove(depot_index);

        int numberOfCustomers = customers.size();

        // This is the solution object - It will store the solution as it is iteratively generated
        // The constructor of Solution class will be executed
        Solution s = new Solution();

        //int numberOfVehicles = 10;

        //Let rtList be the ArrayList of Vehicles assigned to the solution "s".
        ArrayList<Route> rtList = s.routes;
        for (int i = 1 ; i <= numberOfVehicles; i++)
        {
            Route routeTemp = new Route();
            routeTemp.load = 0;
            routeTemp.capacity = capacity;
            routeTemp.cost = 0;
            rtList.add(routeTemp);
        }

        // indicate that all customers are non-routed
        for (int i = 0 ; i < customers.size(); i++)
        {
            customers.get(i).isRouted = false;
        }

        // Greedy solution with Nearest Neighbour algorithm
        NearestNeighbour(s, distanceMatrix, rtList, depot, customers, numberOfVehicles, numberOfCustomers);

        // Refine the solution found with a local search (2-OPT)
        TwoOpt(s, distanceMatrix, numberOfVehicles);

        printSolution(rtList, s, numberOfVehicles);

        Solution bestSolution = new Solution();

        int n_swaps = 3;

        // compute "n_iter" iterations of Variable Neighbor Search Algorithm
        if (vns) {

            bestSolution = VNS(s, distanceMatrix, numberOfVehicles, n_iter, k_opt, n_swaps, nodes);
    
            printSolution(rtList, bestSolution, numberOfVehicles);
        }
        else {
            bestSolution = (Solution) s.clone();

            printSolution(rtList, bestSolution, numberOfVehicles);
        }

        // Build the solution in a JSON format
        JsonArray Allsolutions = new JsonArray();
        for (int j = 0; j < bestSolution.routes.size(); j++) {
            JsonObject element = new JsonObject();
            JsonArray solution = new JsonArray();
            for (int i = 0; i < bestSolution.routes.get(j).customers.size(); i++) {
                JsonObject coord = new JsonObject();
                coord.addProperty("id", "vehicle_" + j);
                coord.addProperty("x", bestSolution.routes.get(j).customers.get(i).x);
                coord.addProperty("y", bestSolution.routes.get(j).customers.get(i).y);
                solution.add(coord);
            }
            element.add("coords", solution);
            element.addProperty("cost", bestSolution.routes.get(j).cost);
            element.addProperty("load", bestSolution.routes.get(j).load);
            Allsolutions.add(element);
        }
        
        response.addProperty("statusCode", 200);
        body.addProperty("cost", bestSolution.cost);
        body.add("solution", Allsolutions);
        response.add("body", body);
        return response;
    }

    /**
     * This algorithm build a solution with a greedy approach which select at each iteration the nearest customer to serve.
     */
    private static void NearestNeighbour(Solution s, double [][] distanceMatrix, ArrayList<Route> rtList, Customer depot,
                                         ArrayList <Customer> customers, int numberOfVehicles, int numberOfCustomers) {

        // Setting a count for customers who are not inserted in the solution yet
        int notInserted = numberOfCustomers;

        for (int j=0; j < numberOfVehicles; j++)
        {
            ArrayList <Customer> nodeSequence = rtList.get(j).customers;
            nodeSequence.add(depot);

            int capacity = rtList.get(j).capacity; // The capacity of this vehicle (=50)
            int load = rtList.get(j).load; // The initial load of this vehicle (=0)
            // Setting a boolean variable that shows the assignment is not final yet.
            boolean isFinal = false;
            // If we have no more customers to insert, we add the depot at the end of the sequence.
            if (notInserted == 0) {
                isFinal = true;
                nodeSequence.add(depot);
            }
            while (!isFinal) {
                //this will be the position of the nearest neighbor customer -- initialization to -1
                int positionOfTheNextOne = -1;
                // This will hold the minimal cost for moving to the next customer - initialized to something very large
                double bestCostForTheNextOne = Double.MAX_VALUE;
                //This is the last customer of the route (or the depot if the route is empty)
                Customer lastInTheRoute = nodeSequence.get(nodeSequence.size() - 1);
                //identify nearest non-routed customer
                for (int k = 0 ; k < customers.size(); k++)
                {
                    // The examined customer is called candidate
                    Customer candidate = customers.get(k);
                    // if this candidate has not been visited by a vehicle
                    if (!candidate.isRouted) {
                        //This is the cost for moving from the last visited customer to the candidate customer
                        double trialCost = distanceMatrix[lastInTheRoute.ID][candidate.ID];
                        //If this is the minimal cost found so far -> store this cost and the position of this best candidate
                        if (trialCost < bestCostForTheNextOne && candidate.demand<= capacity)
                        {
                            positionOfTheNextOne = k;
                            bestCostForTheNextOne = trialCost;
                        }
                    }
                } // moving on to the next (candidate) customer

                // Step 2: Push the customer in the solution
                // We have found the customer to be pushed.
                // He is located in the positionOfTheNextOne position of the customers list
                // Let's inert him and update the cost of the solution and of the route, accordingly

                if (positionOfTheNextOne != -1 )
                {
                    Customer insertedNode = customers.get(positionOfTheNextOne);
                    //Push the customer in the sequence
                    nodeSequence.add(insertedNode);

                    rtList.get(j).cost = rtList.get(j).cost + bestCostForTheNextOne;
                    s.cost = s.cost + bestCostForTheNextOne;
                    insertedNode.isRouted = true;
                    capacity = capacity - insertedNode.demand;
                    rtList.get(j).load = load + insertedNode.demand;
                    load = load + insertedNode.demand;
                    notInserted = notInserted - 1;

                } else
                {
                    //if the positionOfTheNextOne = -1, it means there is no suitable candidate for this vehicle. So, we add the depot.
                    nodeSequence.add(depot);
                    rtList.get(j).cost = rtList.get(j).cost + distanceMatrix[lastInTheRoute.ID][0];
                    s.cost = s.cost + distanceMatrix[lastInTheRoute.ID][0];
                    isFinal = true;
                }
            }
        }

    }

    /**
     * Variable Neighbour Search algorithm for VRP. 
     * Two possibile ways to escape from local optima:
     * - Shuffle at random each tour of the current solution.
     * - Swap two or more random cities of the current solution respecting the capacity constraints.
     * @return Solution found.
     */
    private static Solution VNS(Solution s, double[][] distanceMatrix, int numberOfVehicles,
                                int n_iter, int k_opt, int n_swaps, int nodes) throws CloneNotSupportedException {

        System.out.println("VNS Algorithm with " + n_iter + " iterations");

        Random rnd = new Random();

        Solution bestSolution = new Solution();

        bestSolution = copySolution(s);

        int counter = 0;

        // iterations
        for (int iter = 0; iter < n_iter; iter++) {

            float randNumber = rnd.nextFloat();

            if (randNumber < 0.5) {
                ShuffleRoutes(s, distanceMatrix, k_opt, numberOfVehicles);
                counter++;
            }
            else {
                ExchangeCustomers(s, n_swaps, nodes, distanceMatrix);
            }

            // refine the current solution with a local search (2-OPT)
            TwoOpt(s, distanceMatrix, numberOfVehicles);

            // update the best solution if a better was found
            if (s.cost < bestSolution.cost) {
                bestSolution = copySolution(s);
                System.out.println("* Found a new incumbent with cost = " + bestSolution.cost);
            }
        }

        System.out.println("Number of ShuffleRoutes calls: " + counter);
        System.out.println("Number of ExchangeCustomers calls: " + (n_iter - counter));

        return bestSolution;
    }

    /**
     * Method that copies a Solution object.
     * @return A Solution copy.
     */
    private static Solution copySolution(Solution s) {

        Solution bestSolution = new Solution();

        bestSolution.cost = s.cost;

        for (int i = 0; i < s.routes.size(); i++) {
            Route route = new Route();
            route.cost = s.routes.get(i).cost;
            route.capacity = s.routes.get(i).capacity;
            route.load = s.routes.get(i).load;
            for (int j = 0; j < s.routes.get(i).customers.size(); j++) {
                Customer cust = new Customer();
                cust.ID = s.routes.get(i).customers.get(j).ID;
                cust.isRouted = s.routes.get(i).customers.get(j).isRouted;
                cust.x = s.routes.get(i).customers.get(j).x;
                cust.y = s.routes.get(i).customers.get(j).y;
                cust.demand = s.routes.get(i).customers.get(j).demand;
                route.customers.add(cust);
            }
            bestSolution.routes.add(route);
        }
        return bestSolution;
    }

    /**
     * First Heuristic for VRP.
     * Swap two or more random cities of the current solution respecting the capacity constraints.
     */
    private static void ExchangeCustomers(Solution s, int n_swaps, int nodes, double[][] distanceMatrix) {

        int count = 0;
        int iters = 0;

        while(count != n_swaps) {

            List<Integer> vertices = getRandomList(2, nodes - 2);

            int firstCity = vertices.get(0);
            int secondCity = vertices.get(1);

            firstCity++;
            secondCity++;

            int firstRoute = 0;
            int secondRoute = 0;
            int firstCustomer = 0;
            int secondCustomer = 0;

            // O(n) -> linear in instance size
            for (int i = 0; i < s.routes.size(); i++) {
                for (int j = 0; j < s.routes.get(i).customers.size(); j++) {
                    if (s.routes.get(i).customers.get(j).ID == firstCity) {
                        firstRoute = i;
                        firstCustomer = j;
                    }
                    if (s.routes.get(i).customers.get(j).ID == secondCity) {
                        secondRoute = i;
                        secondCustomer = j;
                    }
                }
            }


            if ((s.routes.get(firstRoute).load - s.routes.get(firstRoute).customers.get(firstCustomer).demand
                    + s.routes.get(secondRoute).customers.get(secondCustomer).demand) <= s.routes.get(firstRoute).capacity &&
                    (s.routes.get(secondRoute).load - s.routes.get(secondRoute).customers.get(secondCustomer).demand
                            + s.routes.get(firstRoute).customers.get(firstCustomer).demand) <= s.routes.get(secondRoute).capacity) {

                count ++;

                //System.out.println("Swap");
                //System.out.println("From route " + firstRoute + " of customer " + firstCustomer);
                //System.out.println("From route " + secondRoute + " of customer " + secondCustomer);

                Customer temp = s.routes.get(firstRoute).customers.get(firstCustomer);

                s.routes.get(firstRoute).load = s.routes.get(firstRoute).load - s.routes.get(firstRoute).customers.get(firstCustomer).demand
                        + s.routes.get(secondRoute).customers.get(secondCustomer).demand;

                s.routes.get(firstRoute).customers.set(firstCustomer, s.routes.get(secondRoute).customers.get(secondCustomer));

                // recompute the cost of the tour
                double newCost = 0;
                for (int k = 0; k < s.routes.get(firstRoute).customers.size() - 1; k++) {
                    int A = s.routes.get(firstRoute).customers.get(k).ID;
                    int B = s.routes.get(firstRoute).customers.get(k + 1).ID;
                    newCost += distanceMatrix[A][B];
                }
                s.routes.get(firstRoute).cost = newCost;

                s.routes.get(secondRoute).load = s.routes.get(secondRoute).load - s.routes.get(secondRoute).customers.get(secondCustomer).demand
                        + temp.demand;

                s.routes.get(secondRoute).customers.set(secondCustomer, temp);

                newCost = 0;
                for (int k = 0; k < s.routes.get(secondRoute).customers.size() - 1; k++) {
                    int A = s.routes.get(secondRoute).customers.get(k).ID;
                    int B = s.routes.get(secondRoute).customers.get(k + 1).ID;
                    newCost += distanceMatrix[A][B];
                }
                s.routes.get(secondRoute).cost = newCost;

                double total_cost = 0;
                for (int k = 0; k < s.routes.size(); k++) {
                    total_cost += s.routes.get(k).cost;
                }
                s.cost = total_cost;
            }

            iters++;

            if (iters == nodes) break;
        }
    }

    /**
     * Second Heuristic for VRP.
     * Shuffle at random each tour of the current solution.
     */
    private static void ShuffleRoutes(Solution s, double[][] distanceMatrix, int k_opt, int numberOfVehicles) {

        double totalCost = 0.0;

        // for each vehicle
        for (int vs = 0; vs < numberOfVehicles; vs++) {

            if (s.routes.get(vs).customers.size() > (k_opt + 1)) {

                ArrayList<Customer> succCustomers = new ArrayList<>();

                // create succ array of customers
                for (int i = 0; i < s.routes.get(vs).customers.size() - 1; i++) {
                    succCustomers.add(s.routes.get(vs).customers.get(i + 1));
                }
                succCustomers.add(s.routes.get(vs).customers.get(0));

                // generate k_opt random indices
                List<Integer> vertices = getRandomList(k_opt, s.routes.get(vs).customers.size() - 2);
                // sort the indices
                Collections.sort(vertices);

                // Swap the k_opt edges w.r.t. random indices
                Customer first = succCustomers.get(vertices.get(0));
                for (int i = 0; i < k_opt - 1; i++) {
                    succCustomers.set(vertices.get(i), succCustomers.get(vertices.get(i + 1)));
                }
                succCustomers.set(vertices.get(k_opt - 1), first);

                // update the solution
                for (int i = 1; i < s.routes.get(vs).customers.size() - 1; i++) {
                    s.routes.get(vs).customers.set(i, succCustomers.get(i - 1));
                }

                // recompute the cost of the tour
                double newCost = 0;
                for (int i = 0; i < s.routes.get(vs).customers.size() - 1; i++) {
                    int A = s.routes.get(vs).customers.get(i).ID;
                    int B = s.routes.get(vs).customers.get(i + 1).ID;
                    newCost += distanceMatrix[A][B];
                }

                // update the total cost
                totalCost += newCost;

                // update the vehicle cost
                s.routes.get(vs).cost = newCost;
            }
            else {
                totalCost += s.routes.get(vs).cost;
            }
        }

        // update the total cost of the problem
        s.cost = totalCost;
    }

    /**
     * Print the solution.
     */
    private static void printSolution(ArrayList<Route> rtList, Solution s, int numberOfVehicles) {

        /*
        for (int j=0; j<numberOfVehicles; j++)
        {
            int vehicle_number = j+1;
            System.out.println("Route for Vehicle #" + vehicle_number);
            for (int k=0; k<s.routes.get(j).customers.size(); k++)
            {
                System.out.print(s.routes.get(j).customers.get(k).ID + "  ");
            }
            System.out.println("");
            System.out.println("Route Cost = " + s.routes.get(j).cost);
            System.out.println("Final Load: " + s.routes.get(j).load);
            System.out.println("Final Remaining Capacity = " + (rtList.get(j).capacity - s.routes.get(j).load));
            System.out.println("----------------------------------------");

        }*/
        System.out.println("Total Solution Cost = " + s.cost);

    }

    /**
     * Two-Opt method for VRP.
     */
    private static void TwoOpt(Solution s, double [][] distanceMatrix, int numberOfVehicles) {

        //this is a boolean flag (true/false) for terminating the local search procedure
        boolean terminationCondition = false;

        //this is a counter for holding the local search iterator
        int localSearchIterator = 0;

        //Here we apply the best relocation move local search scheme
        //This is an object for holding the best relocation move that can be applied to the candidate solution
        RelocationMove rm = new RelocationMove(); // in order to apply one relocation  move for all routes - dont want to lose previous if i change vehicle

        //Initialize the relocation move rm
        rm.positionOfRelocated = -1;
        rm.positionToBeInserted = -1;
        rm.fromRoute = 0;
        rm.toRoute = 0;
        rm.fromMoveCost = Double.MAX_VALUE;
        rm.toMoveCost = Double.MAX_VALUE;

        // Until the termination condition is set to true repeat the following block of code
        while (!terminationCondition)
        {
            //With this function we look for the best relocation move
            //the characteristics of this move will be stored in the object rm
            findBestRelocationMove(rm, s, distanceMatrix, numberOfVehicles);

            // If rm (the identified best relocation move) is a cost improving move, or in other words
            // if the current solution is not a local optimum
            if (rm.moveCost < 0)
            {
                //This is a function applying the relocation move rm to the candidate solution
                applyRelocationMove(rm, s, distanceMatrix);

            }
            else
            {
                //if no cost improving relocation move was found,
                //or in other words if the current solution is a local optimum
                //terminate the local search algorithm
                terminationCondition = true;
            }

            localSearchIterator = localSearchIterator + 1;
        }
    }

    /**
     * Find best swaps for the current VRP solution until reach a local optima.
     */
    private static void findBestRelocationMove(RelocationMove rm, Solution s, double [][] distanceMatrix, int numberOfVehicles)
    {
        //This is a variable that will hold the cost of the best relocation move
        double bestMoveCost = Double.MAX_VALUE;

        //We will iterate through all available vehicles

        //Vehicles to relocate FROM
        for (int from = 0; from<numberOfVehicles; from++)
        {
            // Vehicles to relocate TO
            for (int to = 0; to<numberOfVehicles; to++)
            {

                for (int fromIndex = 1; fromIndex < s.routes.get(from).customers.size() - 1; fromIndex++)
                {
                    //Node A is the predecessor of B
                    Customer A = s.routes.get(from).customers.get(fromIndex - 1);

                    //Node B is the relocated node
                    Customer B = s.routes.get(from).customers.get(fromIndex);

                    //Node C is the successor of B
                    Customer C = s.routes.get(from).customers.get(fromIndex + 1);

                    //We will iterate through all possible re-insertion positions for B
                    for (int afterIndex = 0; afterIndex < s.routes.get(to).customers.size() -1; afterIndex ++)
                    {

                        if ((afterIndex != fromIndex && afterIndex != fromIndex - 1)||from != to)
                        {
                            //Node F the node after which B is going to be reinserted
                            Customer F = s.routes.get(to).customers.get(afterIndex);

                            //Node G the successor of F
                            Customer G = s.routes.get(to).customers.get(afterIndex + 1);

                            //The arcs A-B, B-C, and F-G break
                            double costRemovedFrom = distanceMatrix[A.ID][B.ID] + distanceMatrix[B.ID][C.ID];
                            double costRemovedTo = distanceMatrix[F.ID][G.ID];

                            //The arcs A-C, F-B and B-G are created
                            double costAddedFrom = distanceMatrix[A.ID][C.ID];
                            double costAddedTo  = distanceMatrix[F.ID][B.ID] + distanceMatrix[B.ID][G.ID];

                            //This is the cost of the move, or in other words
                            //the change that this move will cause if applied to the current solution
                            double fromMoveCost = costAddedFrom - costRemovedFrom;
                            double toMoveCost = costAddedTo - costRemovedTo;

                            //If this move is the best found so far
                            double moveCost = fromMoveCost+toMoveCost;
                            if ((moveCost < bestMoveCost)&&(from == to || (s.routes.get(to).load + s.routes.get(from).customers.get(fromIndex).demand<=s.routes.get(to).capacity)))
                            {
                                //set the best cost equal to the cost of this solution
                                bestMoveCost = moveCost;

                                //store its characteristics
                                rm.positionOfRelocated = fromIndex;
                                rm.positionToBeInserted = afterIndex;
                                rm.toMoveCost = toMoveCost;
                                rm.fromMoveCost = fromMoveCost;
                                rm.fromRoute = from;
                                rm.toRoute = to;
                                rm.moveCost = moveCost;
                                if (from != to) {
                                    rm.fromUpdLoad = s.routes.get(from).load - s.routes.get(from).customers.get(fromIndex).demand;
                                    rm.toUpdLoad = s.routes.get(to).load + s.routes.get(from).customers.get(fromIndex).demand;
                                }
                                else {
                                    rm.fromUpdLoad = s.routes.get(from).load;
                                    rm.toUpdLoad = s.routes.get(to).load;
                                }


                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * This function applies the relocation move rm to solution s
     */
    private static void applyRelocationMove(RelocationMove rm, Solution s, double[][] distanceMatrix)
    {
        //This is the node to be relocated
        Customer relocatedNode = s.routes.get(rm.fromRoute).customers.get(rm.positionOfRelocated);

        //Take out the relocated node
        s.routes.get(rm.fromRoute).customers.remove(rm.positionOfRelocated);

        //Reinsert the relocated node into the appropriate position
        //Where??? -> after the node that WAS (!!!!) located in the rm.positionToBeInserted of the route

        //Watch out!!!
        //If the relocated customer is reinserted backwards we have to re-insert it in (rm.positionToBeInserted + 1)
        if (((rm.positionToBeInserted < rm.positionOfRelocated)) || (rm.toRoute!=rm.fromRoute))
        {
            s.routes.get(rm.toRoute).customers.add(rm.positionToBeInserted + 1, relocatedNode);
        }
        ////else (if it is reinserted forward) we have to re-insert it in (rm.positionToBeInserted)
        else
        {
            s.routes.get(rm.toRoute).customers.add(rm.positionToBeInserted, relocatedNode);
        }

        //System.out.println("FROM: Vehicle #" + (rm.fromRoute+1) + " Position: " + (rm.positionOfRelocated+1) + " --> Updated Load = " + rm.fromUpdLoad);
        //System.out.println("TO:   Vehicle #" + (rm.toRoute+1) + " Position: " + (rm.positionToBeInserted+1) + " --> Updated Load = " + rm.toUpdLoad);
        //System.out.println("--------------------------------------------------");


        //update the cost of the solution and the corresponding cost of the route object in the solution
        s.cost = s.cost + rm.moveCost;
        s.routes.get(rm.toRoute).cost = s.routes.get(rm.toRoute).cost + rm.toMoveCost;
        s.routes.get(rm.fromRoute).cost = s.routes.get(rm.fromRoute).cost + rm.fromMoveCost;
        if  (rm.toRoute != rm.fromRoute) {
            s.routes.get(rm.toRoute).load = rm.toUpdLoad;
            s.routes.get(rm.fromRoute).load = rm.fromUpdLoad;
        }
        else {
            s.routes.get(rm.toRoute).load = rm.toUpdLoad;
        }
    }

    /**
     * Get an array composed of random values
     * @param size size of the array to generate
     * @param max generate numbers between 0 and (max - 1)
     * @return array of random numbers
     */
    private static List<Integer> getRandomList(int size, int max) {

        ArrayList<Integer> arr = new ArrayList<>(max);
        for (int i = 0; i < max; i++) arr.add(i);
        Collections.shuffle(arr);
        return arr.subList(0, size);

    }
}