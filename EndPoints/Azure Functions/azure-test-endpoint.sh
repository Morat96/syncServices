echo "Testing endpoints of Serverless providers"

# Azure Functions

echo "Testing Azure Functions"

## RSA

echo "Java"
echo "Test RSA Algorithm"

curl -X GET "https://rsajava.azurewebsites.net/api/rsa?n=9010760112349&e=65537&cipher=1234" | jq .
echo ""

### BenchDB API (Java)

echo "Test BenchDB API"

echo "Testing GET call"
curl -X GET https://couchdbjava.azurewebsites.net/api/readBench/benchdb/absolute | jq . 
echo ""

echo "Testing POST call"
curl -X POST -d '{"payload" : "payload"}' "https://couchdbjava.azurewebsites.net/api/createBench/benchdb?size=5&ndocs=2&sorts=5" | jq . 
echo ""

echo "Testing DELETE call"
curl -X DELETE https://couchdbjava.azurewebsites.net/api/deleteBench/benchdb/2 | jq . 
echo ""

echo "Testing PUT call"
curl -X PUT -d '{"payload" : "payload"}' "https://couchdbjava.azurewebsites.net/api/updateBench/benchdb/2?size=5&sorts=5" | jq . 
echo ""

echo "Test VRP Algorithm"

curl -X POST -H "Content-Type: text/plain" -d "$(cat E016-03m.dat)" "https://vrpjava.azurewebsites.net/api/vrp?vns=false" | jq . 
echo ""

echo "Javascript"
echo "Test RSA Algorithm"

curl -X GET "https://rsajs.azurewebsites.net/api/rsa?n=9010760112349&e=65537&cipher=1234" | jq . 
echo ""

### BenchDB API (Javascript)

echo "Test BenchDB API"

echo "Testing GET call"
curl -X GET https://couchdbjs.azurewebsites.net/api/readBench/benchdb/absolute | jq . 
echo ""

echo "Testing POST call"
curl -X POST -d '{"payload" : "payload"}' "https://couchdbjs.azurewebsites.net/api/createBench/benchdb?size=5&ndocs=2&sorts=5" | jq . 
echo ""

echo "Testing DELETE call"
curl -X DELETE https://couchdbjs.azurewebsites.net/api/deleteBench/benchdb/2 | jq . 
echo ""

echo "Testing PUT call"
curl -X PUT -d '{"payload" : "payload"}' "https://couchdbjs.azurewebsites.net/api/updateBench/benchdb/2?size=5&sorts=5" | jq . 
echo ""

echo "Test VRP Algorithm"

curl -X POST -H "Content-Type: text/plain" -d "$(cat E016-03m.dat)" "https://vrpjs.azurewebsites.net/api/vrp?vns=false" | jq . 
echo ""

echo "All done."