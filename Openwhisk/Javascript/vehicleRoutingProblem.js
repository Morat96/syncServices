/**
 * A benchmark for CouchDB databases.
 * This function creates a number of random documents and executes a series of sorts to them.
 * @author Matteo Moratello 
 */
function main(params) {

    var vns = false;
    var k_opt = 1;
    var n_iter = 1;

    if (!params.vns) {
        return {
            statusCode: 400,
            headers: { 'Content-Type': 'application/json' },
            body: {
                "message": "The parameter 'vns' must be defined!"
            }
        };
    }

    vns = params.vns;

    if (params.k_opt) {
        k_opt = params.k_opt;
    }

    if (params.n_iter) {
        n_iter = params.n_iter;
    }

    var content;

    if(params.__ow_body && params.__ow_body.length) {
        content = params.__ow_body;
    }
    else {
        return {
            statusCode: 400,
            headers: { 'Content-Type': 'application/json' },
            body: {
                "message": "An instance of a Vehicle Routing Problem must be upload with a POST method."
            }
        };
    }

    var fs = require('fs');

    fs.writeFileSync('instance.txt', content);

    var nodes = 0;
    var capacity = 0;
    var numberOfVehicles = 0;
    var depot_index = 0;

    // Supported file types
    var edge_type = "";

    var nodeCoordSection = false;
    var demandSection = false;
    var depotSection = false;

    var customers = ([]);

    var file = fs.readFileSync('instance.txt').toString().split("\n");

    file.forEach(lines => {
        var line = lines.trim().replace( /\r/, " " ).split(" ");
        if (line[0]!="EOF") {
            if (line[0]=="DIMENSION") nodes = line[2];
            if (line[0]=="CAPACITY") capacity = line[2];
            if (line[0]=="VEHICLES") numberOfVehicles = line[2];
            if (line[0]=="EDGE_WEIGHT_TYPE") edge_type = line[2];
            
            if (line[0]=="NODE_COORD_SECTION") {    
                nodeCoordSection = true;
                return;
            }
            if (line[0]=="DEMAND_SECTION") {   
                nodeCoordSection = false;
                demandSection = true;
                return;
            }
            if (line[0]=="DEPOT_SECTION") { 
                demandSection = false;
                depotSection = true;
                return;
            }

            if (nodeCoordSection) {

                var str = lines.replace( /\r/, "" ).trim().split(/\s+/);

                if( edge_type=="EUC_2D" || edge_type=="GEO" ) {
                var x = parseFloat(str[1])
                var y = parseFloat(str[2])

                var cust = new Customer();
                cust.x = x;
                cust.y = y;
                customers.push(cust);
                }
            }

            if (demandSection) {

                var str = lines.replace( /\r/, "" ).trim().split(/\s+/);

                var key = parseInt(str[0]);
                var value = parseInt(str[1]);
                customers[key-1].demand = value;
                customers[key-1].ID = (key - 1);
                customers[key-1].isRouted = false;
            }
            
            if(depotSection) {

                var str = lines.replace( /\r/, "" ).trim().split(/\s+/);
                var value = parseInt(str[0]);
                depot_index = value - 1;
                depotSection = false;
            }
        }
    });

    console.info("DIMENSION: " + nodes);
    console.info("CAPACITY: " + capacity);
    console.info("VEHICLES: " + numberOfVehicles);

    var distanceMatrix = (function (dims) { var allocate = function (dims) { if (dims.length == 0) {
        return 0;
    }
    else {
        var array = [];
        for (var i = 0; i < dims[0]; i++) {
            array.push(allocate(dims.slice(1)));
        }
        return array;
    } }; return allocate(dims); })([/* size */ customers.length, /* size */ customers.length]);

    for (var i = 0; i < customers.length; i++) {
        var from = customers[i];
        for (var j = 0; j < customers.length; j++) {
            var to = customers[j];
            var Delta_x = (from.x - to.x);
            var Delta_y = (from.y - to.y);
            var distance = Math.sqrt((Delta_x * Delta_x) + (Delta_y * Delta_y));
            distance = Math.round(distance);
            distanceMatrix[i][j] = distance;
        }

    }

    var depot = new Customer();
    depot = customers[depot_index];
    /* remove */ customers.splice(depot_index, 1)[0];
    var numberOfCustomers = customers.length;
    var s = new Solution();
    var rtList = s.routes;
    for (var i = 1; i <= numberOfVehicles; i++) {  

        var routeTemp = new Route();
        routeTemp.load = 0;
        routeTemp.capacity = capacity;
        routeTemp.cost = 0;
        /* add */ (rtList.push(routeTemp) > 0);  
    }
    for (var i = 0; i < customers.length; i++) {

        /* get */ customers[i].isRouted = false;
    }

    NearestNeighbour(s, distanceMatrix, rtList, depot, customers, numberOfVehicles, numberOfCustomers);
    TwoOpt(s, distanceMatrix, numberOfVehicles);
    printSolution(rtList, s, numberOfVehicles);

    var n_swaps = 3;

    var bestSolution = new Solution();

    if (vns) {
        bestSolution = VNS(s, distanceMatrix, numberOfVehicles, n_iter, k_opt, n_swaps, nodes);
        printSolution(rtList, bestSolution, numberOfVehicles);
    }
    else {
        bestSolution = copySolution(s);
        printSolution(rtList, bestSolution, numberOfVehicles);
    }

    var response = {"solution" : []};

    response.cost = bestSolution.cost;
    // create the object
    for (var i = 0; i < numberOfVehicles; i++) {
        response.solution[i] = {};
        response.solution[i].cost = bestSolution.routes[i].cost;
        response.solution[i].load = bestSolution.routes[i].load;
        response.solution[i].coords = [];
        for (var j = 0; j < bestSolution.routes[i].customers.length ; j++) {
            response.solution[i].coords[j] = {};
            response.solution[i].coords[j].id = "vehicle_" + i;
            response.solution[i].coords[j].x = bestSolution.routes[i].customers[j].x;
            response.solution[i].coords[j].y = bestSolution.routes[i].customers[j].y;
        }
    }

    return {
        statusCode: 200,
        headers: { 'Content-Type': 'application/json' },
        body: response
    };
};

/**
     * This algorithm build a solution with a greedy approach which select at each iteration the nearest customer to serve.
     * @param {Solution} s
     * @param {Array} distanceMatrix
     * @param {Route[]} rtList
     * @param {Customer} depot
     * @param {Customer[]} customers
     * @param {number} numberOfVehicles
     * @param {number} numberOfCustomers
     * @private
     */
    /*private*/ NearestNeighbour = function (s, distanceMatrix, rtList, depot, customers, numberOfVehicles, numberOfCustomers) {
        var notInserted = numberOfCustomers;
        for (var j = 0; j < numberOfVehicles; j++) {
            {
                var nodeSequence = rtList[j].customers;
                /* add */ (nodeSequence.push(depot) > 0);
                var capacity = rtList[j].capacity;
                var load = rtList[j].load;
                var isFinal = false;
                if (notInserted === 0) {
                    isFinal = true;
                    /* add */ (nodeSequence.push(depot) > 0);
                }
                while ((!isFinal)) {
                    {
                        var positionOfTheNextOne = -1;
                        var bestCostForTheNextOne = 1.7976931348623157E308;
                        var lastInTheRoute = nodeSequence[nodeSequence.length - 1];
                        for (var k = 0; k < customers.length; k++) {
                            {
                                var candidate = customers[k];
                                if (!candidate.isRouted) {
                                    var trialCost = distanceMatrix[lastInTheRoute.ID][candidate.ID];
                                    if (trialCost < bestCostForTheNextOne && candidate.demand <= capacity) {
                                        positionOfTheNextOne = k;
                                        bestCostForTheNextOne = trialCost;
                                    }
                                }
                            }
                            ;
                        }
                        if (positionOfTheNextOne !== -1) {
                            var insertedNode = customers[positionOfTheNextOne];
                            /* add */ (nodeSequence.push(insertedNode) > 0);
                            /* get */ rtList[j].cost = rtList[j].cost + bestCostForTheNextOne;
                            s.cost = s.cost + bestCostForTheNextOne;
                            insertedNode.isRouted = true;
                            capacity = capacity - insertedNode.demand;
                            /* get */ rtList[j].load = load + insertedNode.demand;
                            load = load + insertedNode.demand;
                            notInserted = notInserted - 1;
                        }
                        else {
                            /* add */ (nodeSequence.push(depot) > 0);
                            /* get */ rtList[j].cost = rtList[j].cost + distanceMatrix[lastInTheRoute.ID][0];
                            s.cost = s.cost + distanceMatrix[lastInTheRoute.ID][0];
                            isFinal = true;
                        }
                    }
                }
                ;
            }
            ;
        }
    };
    /**
     * Variable Neighbour Search algorithm for VRP.
     * Two possibile ways to escape from local optima:
     * - Shuffle at random each tour of the current solution.
     * - Swap two or more random cities of the current solution respecting the capacity constraints.
     * @return {Solution} Solution found.
     * @param {Solution} s
     * @param {Array} distanceMatrix
     * @param {number} numberOfVehicles
     * @param {number} n_iter
     * @param {number} k_opt
     * @param {number} n_swaps
     * @param {number} nodes
     * @private
     */
    /*private*/ VNS = function (s, distanceMatrix, numberOfVehicles, n_iter, k_opt, n_swaps, nodes) {
        console.info("VNS Algorithm with " + n_iter + " iterations");
        
        var bestSolution = new Solution();
        bestSolution = copySolution(s);
        var counter = 0;
        for (var iter = 0; iter < n_iter; iter++) {
            {
                var randNumber = Math.random();
                if (randNumber < 0.5) {
                    ShuffleRoutes(s, distanceMatrix, k_opt, numberOfVehicles);
                    counter++;
                }
                else {
                    ExchangeCustomers(s, n_swaps, nodes, distanceMatrix);
                }
                TwoOpt(s, distanceMatrix, numberOfVehicles);
                if (s.cost < bestSolution.cost) {
                    bestSolution = copySolution(s);
                    console.info("* Found a new incumbent with cost = " + bestSolution.cost);
                }
            }
            ;
        }
        console.info("Number of ShuffleRoutes calls: " + counter);
        console.info("Number of ExchangeCustomers calls: " + (n_iter - counter));
        return bestSolution;
    };
    /**
     * Method that copies a Solution object.
     * @return {Solution} A Solution copy.
     * @param {Solution} s
     * @private
     */
    /*private*/ copySolution = function (s) {
        var bestSolution = new Solution();
        bestSolution.cost = s.cost;
        for (var i = 0; i < s.routes.length; i++) {
            {
                var route = new Route();
                route.cost = s.routes[i].cost;
                route.capacity = s.routes[i].capacity;
                route.load = s.routes[i].load;
                for (var j = 0; j < s.routes[i].customers.length; j++) {
                    {
                        var cust = new Customer();
                        cust.ID = s.routes[i].customers[j].ID;
                        cust.isRouted = s.routes[i].customers[j].isRouted;
                        cust.x = s.routes[i].customers[j].x;
                        cust.y = s.routes[i].customers[j].y;
                        cust.demand = s.routes[i].customers[j].demand;
                        /* add */ (route.customers.push(cust) > 0);
                    }
                    ;
                }
                /* add */ (bestSolution.routes.push(route) > 0);
            }
            ;
        }
        return bestSolution;
    };
    /**
     * First Heuristic for VRP.
     * Swap two or more random cities of the current solution respecting the capacity constraints.
     * @param {Solution} s
     * @param {number} n_swaps
     * @param {number} nodes
     * @param {Array} distanceMatrix
     * @private
     */
    /*private*/ ExchangeCustomers = function (s, n_swaps, nodes, distanceMatrix) {
        var count = 0;
        var iters = 0;
        while ((count !== n_swaps)) {
            {
                var vertices = getRandomList(2, nodes - 2);
                var firstCity = vertices[0];
                var secondCity = vertices[1];
                firstCity++;
                secondCity++;
                var firstRoute = 0;
                var secondRoute = 0;
                var firstCustomer = 0;
                var secondCustomer = 0;
                for (var i = 0; i < s.routes.length; i++) {
                    {
                        for (var j = 0; j < s.routes[i].customers.length; j++) {
                            {
                                if (s.routes[i].customers[j].ID === firstCity) {
                                    firstRoute = i;
                                    firstCustomer = j;
                                }
                                if (s.routes[i].customers[j].ID === secondCity) {
                                    secondRoute = i;
                                    secondCustomer = j;
                                }
                            }
                            ;
                        }
                    }
                    ;
                }
                if ((s.routes[firstRoute].load - s.routes[firstRoute].customers[firstCustomer].demand + s.routes[secondRoute].customers[secondCustomer].demand) <= s.routes[firstRoute].capacity && (s.routes[secondRoute].load - s.routes[secondRoute].customers[secondCustomer].demand + s.routes[firstRoute].customers[firstCustomer].demand) <= s.routes[secondRoute].capacity) {
                    count++;
                    var temp = s.routes[firstRoute].customers[firstCustomer];
                    /* get */ s.routes[firstRoute].load = s.routes[firstRoute].load - s.routes[firstRoute].customers[firstCustomer].demand + s.routes[secondRoute].customers[secondCustomer].demand;
                    /* set */ (s.routes[firstRoute].customers[firstCustomer] = s.routes[secondRoute].customers[secondCustomer]);
                    var newCost = 0;
                    for (var k = 0; k < s.routes[firstRoute].customers.length - 1; k++) {
                        {
                            var A = s.routes[firstRoute].customers[k].ID;
                            var B = s.routes[firstRoute].customers[k + 1].ID;
                            newCost += distanceMatrix[A][B];
                        }
                        ;
                    }
                    /* get */ s.routes[firstRoute].cost = newCost;
                    /* get */ s.routes[secondRoute].load = s.routes[secondRoute].load - s.routes[secondRoute].customers[secondCustomer].demand + temp.demand;
                    /* set */ (s.routes[secondRoute].customers[secondCustomer] = temp);
                    newCost = 0;
                    for (var k = 0; k < s.routes[secondRoute].customers.length - 1; k++) {
                        {
                            var A = s.routes[secondRoute].customers[k].ID;
                            var B = s.routes[secondRoute].customers[k + 1].ID;
                            newCost += distanceMatrix[A][B];
                        }
                        ;
                    }
                    /* get */ s.routes[secondRoute].cost = newCost;
                    var total_cost = 0;
                    for (var k = 0; k < s.routes.length; k++) {
                        {
                            total_cost += s.routes[k].cost;
                        }
                        ;
                    }
                    s.cost = total_cost;
                }
                iters++;
                if (iters === nodes)
                    break;
            }
        }
        ;
    };
    /**
     * Second Heuristic for VRP.
     * Shuffle at random each tour of the current solution.
     * @param {Solution} s
     * @param {Array} distanceMatrix
     * @param {number} k_opt
     * @param {number} numberOfVehicles
     * @private
     */
    /*private*/ ShuffleRoutes = function (s, distanceMatrix, k_opt, numberOfVehicles) {
        var totalCost = 0.0;
        for (var vs = 0; vs < numberOfVehicles; vs++) {
            {
                if (s.routes[vs].customers.length > (k_opt + 1)) {
                    var succCustomers = ([]);
                    for (var i = 0; i < s.routes[vs].customers.length - 1; i++) {
                        {
                            /* add */ (succCustomers.push(/* get */ /* get */ s.routes[vs].customers[i + 1]) > 0);
                        }
                        ;
                    }
                    /* add */ (succCustomers.push(/* get */ /* get */ s.routes[vs].customers[0]) > 0);
                    var vertices = getRandomList(k_opt, s.routes[vs].customers.length - 2);
                    /* sort */ vertices.sort();
                    var first = succCustomers[vertices[0]];
                    for (var i = 0; i < k_opt - 1; i++) {
                        {
                            /* set */ (succCustomers[vertices[i]] = succCustomers[vertices[i + 1]]);
                        }
                        ;
                    }
                    /* set */ (succCustomers[vertices[k_opt - 1]] = first);
                    for (var i = 1; i < s.routes[vs].customers.length - 1; i++) {
                        {
                            /* set */ (s.routes[vs].customers[i] = succCustomers[i - 1]);
                        }
                        ;
                    }
                    var newCost = 0;
                    for (var i = 0; i < s.routes[vs].customers.length - 1; i++) {
                        {
                            var A = s.routes[vs].customers[i].ID;
                            var B = s.routes[vs].customers[i + 1].ID;
                            newCost += distanceMatrix[A][B];
                        }
                        ;
                    }
                    totalCost += newCost;
                    /* get */ s.routes[vs].cost = newCost;
                }
                else {
                    totalCost += s.routes[vs].cost;
                }
            }
            ;
        }
        s.cost = totalCost;
    };
    /**
     * Print the solution.
     * @param {Route[]} rtList
     * @param {Solution} s
     * @param {number} numberOfVehicles
     * @private
     */
    /*private*/ printSolution = function (rtList, s, numberOfVehicles) {

        for (var j = 0; j < numberOfVehicles; j++) {
            {
                var vehicle_number = j + 1;
                console.info("Route for Vehicle #" + vehicle_number);
                for (var k = 0; k < s.routes[j].customers.length; k++) {
                    {
                        process.stdout.write(/* get */ /* get */ s.routes[j].customers[k].ID + "  ");
                    }
                    ;
                }
                console.info("");
                console.info("Route Cost = " + s.routes[j].cost);
                console.info("Final Load: " + s.routes[j].load);
                console.info("Final Remaining Capacity = " + (rtList[j].capacity - s.routes[j].load));
                console.info("----------------------------------------");
            };
        }
        console.info("Total Solution Cost = " + s.cost);
    };
    /**
     * Two-Opt method for VRP.
     * @param {Solution} s
     * @param {Array} distanceMatrix
     * @param {number} numberOfVehicles
     * @private
     */
    /*private*/ TwoOpt = function (s, distanceMatrix, numberOfVehicles) {
        var terminationCondition = false;
        var localSearchIterator = 0;
        var rm = new RelocationMove();
        rm.positionOfRelocated = -1;
        rm.positionToBeInserted = -1;
        rm.fromRoute = 0;
        rm.toRoute = 0;
        rm.fromMoveCost = 1.7976931348623157E308;
        rm.toMoveCost = 1.7976931348623157E308;
        while ((!terminationCondition)) {
            {
                findBestRelocationMove(rm, s, distanceMatrix, numberOfVehicles);
                if (rm.moveCost < 0) {
                    applyRelocationMove(rm, s, distanceMatrix);
                }
                else {
                    terminationCondition = true;
                }
                localSearchIterator = localSearchIterator + 1;
            }
        }
        ;
    };
    /**
     * Find best swaps for the current VRP solution until reach a local optima.
     * @param {RelocationMove} rm
     * @param {Solution} s
     * @param {Array} distanceMatrix
     * @param {number} numberOfVehicles
     * @private
     */
    /*private*/ findBestRelocationMove = function (rm, s, distanceMatrix, numberOfVehicles) {
        var bestMoveCost = 1.7976931348623157E308;
        for (var from = 0; from < numberOfVehicles; from++) {
            {
                for (var to = 0; to < numberOfVehicles; to++) {
                    {
                        for (var fromIndex = 1; fromIndex < s.routes[from].customers.length - 1; fromIndex++) {
                            {
                                var A = s.routes[from].customers[fromIndex - 1];
                                var B = s.routes[from].customers[fromIndex];
                                var C = s.routes[from].customers[fromIndex + 1];
                                for (var afterIndex = 0; afterIndex < s.routes[to].customers.length - 1; afterIndex++) {
                                    {
                                        if ((afterIndex !== fromIndex && afterIndex !== fromIndex - 1) || from !== to) {
                                            var F = s.routes[to].customers[afterIndex];
                                            var G = s.routes[to].customers[afterIndex + 1];
                                            var costRemovedFrom = distanceMatrix[A.ID][B.ID] + distanceMatrix[B.ID][C.ID];
                                            var costRemovedTo = distanceMatrix[F.ID][G.ID];
                                            var costAddedFrom = distanceMatrix[A.ID][C.ID];
                                            var costAddedTo = distanceMatrix[F.ID][B.ID] + distanceMatrix[B.ID][G.ID];
                                            var fromMoveCost = costAddedFrom - costRemovedFrom;
                                            var toMoveCost = costAddedTo - costRemovedTo;
                                            var moveCost = fromMoveCost + toMoveCost;
                                            if ((moveCost < bestMoveCost) && (from === to || (s.routes[to].load + s.routes[from].customers[fromIndex].demand <= s.routes[to].capacity))) {
                                                bestMoveCost = moveCost;
                                                rm.positionOfRelocated = fromIndex;
                                                rm.positionToBeInserted = afterIndex;
                                                rm.toMoveCost = toMoveCost;
                                                rm.fromMoveCost = fromMoveCost;
                                                rm.fromRoute = from;
                                                rm.toRoute = to;
                                                rm.moveCost = moveCost;
                                                if (from !== to) {
                                                    rm.fromUpdLoad = s.routes[from].load - s.routes[from].customers[fromIndex].demand;
                                                    rm.toUpdLoad = s.routes[to].load + s.routes[from].customers[fromIndex].demand;
                                                }
                                                else {
                                                    rm.fromUpdLoad = s.routes[from].load;
                                                    rm.toUpdLoad = s.routes[to].load;
                                                }
                                            }
                                        }
                                    }
                                    ;
                                }
                            }
                            ;
                        }
                    }
                    ;
                }
            }
            ;
        }
    };
    /**
     * This function applies the relocation move rm to solution s
     * @param {RelocationMove} rm
     * @param {Solution} s
     * @param {Array} distanceMatrix
     * @private
     */
    /*private*/ applyRelocationMove = function (rm, s, distanceMatrix) {
        var relocatedNode = s.routes[rm.fromRoute].customers[rm.positionOfRelocated];
        /* remove */ /* get */ s.routes[rm.fromRoute].customers.splice(rm.positionOfRelocated, 1)[0];
        if (((rm.positionToBeInserted < rm.positionOfRelocated)) || (rm.toRoute !== rm.fromRoute)) {
            /* add */ /* get */ s.routes[rm.toRoute].customers.splice(rm.positionToBeInserted + 1, 0, relocatedNode);
        }
        else {
            /* add */ /* get */ s.routes[rm.toRoute].customers.splice(rm.positionToBeInserted, 0, relocatedNode);
        }
        s.cost = s.cost + rm.moveCost;
        /* get */ s.routes[rm.toRoute].cost = s.routes[rm.toRoute].cost + rm.toMoveCost;
        /* get */ s.routes[rm.fromRoute].cost = s.routes[rm.fromRoute].cost + rm.fromMoveCost;
        if (rm.toRoute !== rm.fromRoute) {
            /* get */ s.routes[rm.toRoute].load = rm.toUpdLoad;
            /* get */ s.routes[rm.fromRoute].load = rm.fromUpdLoad;
        }
        else {
            /* get */ s.routes[rm.toRoute].load = rm.toUpdLoad;
        }
    };


var Customer = (function () {
    function Customer() {
        if (this.x === undefined)
            this.x = 0;
        if (this.y === undefined)
            this.y = 0;
        if (this.ID === undefined)
            this.ID = 0;
        if (this.demand === undefined)
            this.demand = 0;
        if (this.isRouted === undefined)
            this.isRouted = false;
    }
    return Customer;
}());
Customer["__class"] = "Customer";
var Route = (function () {
    function Route() {
        if (this.customers === undefined)
            this.customers = null;
        if (this.cost === undefined)
            this.cost = 0;
        if (this.load === undefined)
            this.load = 0;
        if (this.capacity === undefined)
            this.capacity = 0;
        this.cost = 0;
        this.load = 0;
        this.capacity = 50;
        this.customers = ([]);
    }
    return Route;
}());
Route["__class"] = "Route";
var Solution = (function () {
    function Solution() {
        if (this.cost === undefined)
            this.cost = 0;
        if (this.routes === undefined)
            this.routes = null;
        this.routes = ([]);
        this.cost = 0;
    }
    return Solution;
}());
Solution["__class"] = "Solution";
var RelocationMove = (function () {
    function RelocationMove() {
        if (this.fromRoute === undefined)
            this.fromRoute = 0;
        if (this.toRoute === undefined)
            this.toRoute = 0;
        if (this.positionOfRelocated === undefined)
            this.positionOfRelocated = 0;
        if (this.positionToBeInserted === undefined)
            this.positionToBeInserted = 0;
        if (this.fromMoveCost === undefined)
            this.fromMoveCost = 0;
        if (this.toMoveCost === undefined)
            this.toMoveCost = 0;
        if (this.moveCost === undefined)
            this.moveCost = 0;
        if (this.fromUpdLoad === undefined)
            this.fromUpdLoad = 0;
        if (this.toUpdLoad === undefined)
            this.toUpdLoad = 0;
    }
    return RelocationMove;
}());
RelocationMove["__class"] = "RelocationMove";


/**
* Get an array composed of random values
* @param size size of the array to generate
* @param max generate numbers between 0 and (max - 1)
* @returns array of random numbers
*/
function getRandomList(size, max) {
    var arr = [];
    while(arr.length < size){
        var r = Math.floor(Math.random() * max);
        if(arr.indexOf(r) === -1) arr.push(r);
    }
    return arr;
}