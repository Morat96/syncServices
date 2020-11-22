echo "Testing endpoints of Serverless providers"

# Google Cloud Functions

echo "Testing Google Cloud Functions"

## RSA

echo "Java"
echo "Test RSA Algorithm"

curl -X GET "https://europe-west1-test-functions-296217.cloudfunctions.net/rsaJava?n=9010760112349&e=65537&cipher=1234" | jq .
echo ""

### BenchDB API (Java)

echo "Test BenchDB API"

echo "Testing GET call"
curl -X GET https://europe-west1-test-functions-296217.cloudfunctions.net/readBenchJava/benchdb/absolute | jq .
echo ""

echo "Testing POST call"
curl -X POST -d '{"payload" : "payload"}' "https://europe-west1-test-functions-296217.cloudfunctions.net/createBenchJava/benchdb?size=5&ndocs=2&sorts=5" | jq . 
echo ""

echo "Testing DELETE call"
curl -X DELETE https://europe-west1-test-functions-296217.cloudfunctions.net/deleteBenchJava/benchdb/2 | jq . 
echo ""

echo "Testing PUT call"
curl -X PUT -d '{"payload" : "payload"}' "https://europe-west1-test-functions-296217.cloudfunctions.net/updateBenchJava/benchdb/2?size=5&sorts=5" | jq . 
echo ""

echo "Test VRP Algorithm"

curl -X POST -H "Content-Type: text/plain" -d "$(cat E016-03m.dat)" "https://europe-west1-test-functions-296217.cloudfunctions.net/vrpJava?vns=false" | jq . 
echo ""

echo "Javascript"
echo "Test RSA Algorithm"

curl -X GET "https://europe-west1-test-functions-296217.cloudfunctions.net/rsaJS?n=9010760112349&e=65537&cipher=1234" | jq . 
echo ""

### BenchDB API (Javascript)

echo "Test BenchDB API"

echo "Testing GET call"
curl -X GET https://europe-west1-test-functions-296217.cloudfunctions.net/readBenchJS/benchdb/absolute | jq . 
echo ""

echo "Testing POST call"
curl -X POST -d '{"payload" : "payload"}' "https://europe-west1-test-functions-296217.cloudfunctions.net/createBenchJS/benchdb?size=5&ndocs=2&sorts=5" | jq . 
echo ""

echo "Testing DELETE call"
curl -X DELETE https://europe-west1-test-functions-296217.cloudfunctions.net/deleteBenchJS/benchdb/2 | jq .
echo ""

echo "Testing PUT call"
curl -X PUT -d '{"payload" : "payload"}' "https://europe-west1-test-functions-296217.cloudfunctions.net/updateBenchJS/benchdb/2?size=5&sorts=5" | jq . 
echo ""

echo "Test VRP Algorithm"

curl -X POST -H "Content-Type: text/plain" -d "$(cat E016-03m.dat)" "https://europe-west1-test-functions-296217.cloudfunctions.net/vrpJS?vns=false" | jq . 
echo ""

echo "All done."