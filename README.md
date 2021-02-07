# Microservices and Serverless Benchmarking

## Serverless platforms
- Apache OpenWhisk;
- Azure Functions;
- IBM Cloud Functions;
- AWS Lambda;
- Google Cloud Functions.

For each platform, the folders present are the following:
- <b>Java</b>
 - RSA Decryption;
 - CRUD benchmark;
 - Vehicle Routing Problem.
- <b>NodeJS</b>
 - RSA Decryption;
 - CRUD benchmark;
 - Vehicle Routing Problem.

## Algorithms

### RSA Decryption
This algorithm finds from a <i>public key</i> composed by the couple (n, e), the <i>private key</i> d. This problem in NP-Hard.

### CouchDB CRUD benchmark
This benchmark performs the four <b>crud operations</b> against a <i>couchdb</i> NoSql database, in order to measure the speed of architectures when are involved in actions that deal with read/write/delete/update operations.
- <b>Create:</b> This algorithm performs a <i>POST request</i> to a couchdb database. The algorithm creates a set of documents composed of random words and to then are applied a set of sort operations. Then the documents are added to the database with a <i>bulk operation</i> (from couchdb).
- <b>Update:</b> This algorithm performs a <i>PUT request</i> to a couchdb database. The algorithm gets all the documents of the database, chooses a set of documents randomly and re-creates the documents with the same operations as in Create. Then the documents are updated with a <i>bulk operation</i> (from couchdb). 
- <b>Remove:</b> This algorithm performs a <i>DELETE request</i> to a couchdb database. The algorithm gets all the documents of the database, chooses a set of documents randomly and deletes the documents, with a <i>bulk operation</i> (from couchdb).
- <b>Query:</b> This algorithm performs a <i>GET request</i> to a couchdb database. The algorithm performs a Query to the database, in particular the query searches for a word in the "content" key of each document present in the database and returns all the documents that have the word in their content. The algorithm shows the result to the user in a Json format. 
 
### Vehicle Routing Problem
This algorithm solves a VRP instance with a set of heuristic algorithms. The first solution is build the <i>Nearest Neighbour algorithm</i> for VRP. Then the 2-optimality is reached with a <i>2-OPT algorithm</i> which examines all pairs of arcs of the solution choosing the optimal way to reorder the archs. After that a Metaheuristic algorithm can refine the solution with the <i>Variable Neighbour Search</i> algorithm. The VNS implements two algorithms: The first swaps randomply three or more cities of the entire solution by respecting the demand constraints; The second one performs a shuffle of each tour (of each vehicle) swapping three or more edges of the tour randomly. With the combination of the two algorithms (chosen with equal probability) one can escape from the local optima and finds a better solution (with a lower cost).
