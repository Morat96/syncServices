echo "Testing endpoints of Serverless providers"

# Amazon Lambdas

echo "Testing Amazon Lambdas"

## RSA

echo "Java"
echo "Test RSA Algorithm"

curl -X GET "https://ugjebm1c9g.execute-api.us-east-1.amazonaws.com/Prod/rsa?n=9010760112349&e=65537&cipher=1234" | jq . 
echo ""

### BenchDB API (Java)

echo "Test BenchDB API"

echo "Testing GET call"
curl -X GET https://fqkdqb8skc.execute-api.us-east-1.amazonaws.com/Prod/readBench/benchdb/absolute | jq . 
echo ""

echo "Testing POST call"
curl -X POST "https://fqkdqb8skc.execute-api.us-east-1.amazonaws.com/Prod/createBench/benchdb?size=5&ndocs=2&sorts=5" | jq .  
echo ""

echo "Testing DELETE call"
curl -X DELETE https://fqkdqb8skc.execute-api.us-east-1.amazonaws.com/Prod/deleteBench/benchdb/2 | jq .  
echo ""

echo "Testing PUT call"
curl -X PUT "https://fqkdqb8skc.execute-api.us-east-1.amazonaws.com/Prod/updateBench/benchdb/2?size=5&sorts=5" | jq .  
echo ""

echo "Test VRP Algorithm"

curl -X POST -H "Content-Type: text/plain" -d "$(cat E016-03m.dat)" "https://jmz2r6567f.execute-api.us-east-1.amazonaws.com/Prod/vrp?vns=false" | jq .  
echo ""

echo "Javascript"
echo "Test RSA Algorithm"

curl -X GET "https://bbtf7u4zab.execute-api.us-east-1.amazonaws.com/Prod/rsa?n=9010760112349&e=65537&cipher=1234" | jq .  
echo ""

### BenchDB API (Javascript)

echo "Test BenchDB API"

echo "Testing GET call"
curl -X GET https://gr0lkd9arh.execute-api.us-east-1.amazonaws.com/Prod/readBench/benchdb/absolute | jq .  
echo ""

echo "Testing POST call"
curl -X POST "https://gr0lkd9arh.execute-api.us-east-1.amazonaws.com/Prod/createBench/benchdb?size=5&ndocs=2&sorts=5" | jq .  
echo ""

echo "Testing DELETE call"
curl -X DELETE https://gr0lkd9arh.execute-api.us-east-1.amazonaws.com/Prod/deleteBench/benchdb/2 | jq .  
echo ""

echo "Testing PUT call"
curl -X PUT "https://gr0lkd9arh.execute-api.us-east-1.amazonaws.com/Prod/updateBench/benchdb/2?size=5&sorts=5" | jq .  
echo ""

echo "Test VRP Algorithm"

curl -X POST -H "Content-Type: text/plain" -d "$(cat E016-03m.dat)" "https://cq89uu4lfk.execute-api.us-east-1.amazonaws.com/Prod/vrp?vns=false" | jq .  
echo ""

echo "All done."