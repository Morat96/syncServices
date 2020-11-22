echo "Testing endpoints of Serverless providers"

# OpenWhisk Local

echo "Testing OpenWhisk Local"

## RSA

echo "Java"
echo "Test RSA Algorithm"

curl -X GET "https://192.168.30.151:31001/api/23bc46b1-71f6-4ed5-8c54-816aa4f8c502/rsa/java?n=9010760112349&e=65537&cipher=1234" -k | jq .
echo ""

### BenchDB API (Java)

echo "Test BenchDB API"

echo "Testing GET call"
curl -X GET https://192.168.30.151:31001/api/23bc46b1-71f6-4ed5-8c54-816aa4f8c502/benchjava/benchdb/query/benchdb/absolute -k | jq .
echo ""

echo "Testing POST call"
curl -X POST "https://192.168.30.151:31001/api/23bc46b1-71f6-4ed5-8c54-816aa4f8c502/benchjava/benchdb/benchdb?size=5&ndocs=2&sorts=5" -k | jq .
echo ""

echo "Testing DELETE call"
curl -X DELETE https://192.168.30.151:31001/api/23bc46b1-71f6-4ed5-8c54-816aa4f8c502/benchjava/benchdb/benchdb/2 -k | jq .
echo ""

echo "Testing PUT call"
curl -X PUT "https://192.168.30.151:31001/api/23bc46b1-71f6-4ed5-8c54-816aa4f8c502/benchjava/benchdb/benchdb/2?size=5&sorts=5" -k | jq .
echo ""

echo "Test VRP Algorithm"

curl -X POST -H "Content-Type: text/plain" -d "$(cat E016-03m.dat)" "https://192.168.30.151:31001/api/23bc46b1-71f6-4ed5-8c54-816aa4f8c502/vrp/java?vns=false" -k | jq .
echo ""

echo "Javascript"
echo "Test RSA Algorithm"

curl -X GET "https://192.168.30.151:31001/api/23bc46b1-71f6-4ed5-8c54-816aa4f8c502/rsa/js?n=9010760112349&e=65537&cipher=1234" -k | jq .
echo ""

### BenchDB API (Javascript)

echo "Test BenchDB API"

echo "Testing GET call"
curl -X GET https://192.168.30.151:31001/api/23bc46b1-71f6-4ed5-8c54-816aa4f8c502/benchjs/benchdb/query/benchdb/absolute -k | jq .
echo ""

echo "Testing POST call"
curl -X POST "https://192.168.30.151:31001/api/23bc46b1-71f6-4ed5-8c54-816aa4f8c502/benchjs/benchdb/benchdb?size=5&ndocs=2&sorts=5" -k | jq .
echo ""

echo "Testing DELETE call"
curl -X DELETE https://192.168.30.151:31001/api/23bc46b1-71f6-4ed5-8c54-816aa4f8c502/benchjs/benchdb/benchdb/2 -k | jq .
echo ""

echo "Testing PUT call"
curl -X PUT "https://192.168.30.151:31001/api/23bc46b1-71f6-4ed5-8c54-816aa4f8c502/benchjs/benchdb/benchdb/2?size=5&sorts=5" -k | jq .
echo ""

echo "Test VRP Algorithm"

curl -X POST -H "Content-Type: text/plain" -d "$(cat E016-03m.dat)" "https://192.168.30.151:31001/api/23bc46b1-71f6-4ed5-8c54-816aa4f8c502/vrp/js?vns=false" -k | jq .
echo ""

echo "All done."