echo "Testing endpoints of Serverless providers"

# OpenWhisk IBM Cloud functions

echo "Testing IBM Cloud functions"

## RSA

echo "Java"
echo "Test RSA Algorithm"

curl -X GET "https://service.eu.apiconnect.ibmcloud.com/gws/apigateway/api/f41ddfcced3ac2255a3c599ca51140bf9130534da8fcf791fb5c25a0e7d09f98/rsa/java?n=9010760112349&e=65537&cipher=1234" | jq .
echo ""

### BenchDB API (Java)

echo "Test BenchDB API"

echo "Testing GET call"
curl -X GET https://service.eu.apiconnect.ibmcloud.com/gws/apigateway/api/f41ddfcced3ac2255a3c599ca51140bf9130534da8fcf791fb5c25a0e7d09f98/benchjava/benchdb/query/benchdb/absolute | jq . 
echo ""

echo "Testing POST call"
curl -X POST "https://service.eu.apiconnect.ibmcloud.com/gws/apigateway/api/f41ddfcced3ac2255a3c599ca51140bf9130534da8fcf791fb5c25a0e7d09f98/benchjava/benchdb/benchdb?size=5&ndocs=2&sorts=5" | jq . 
echo ""

echo "Testing DELETE call"
curl -X DELETE https://service.eu.apiconnect.ibmcloud.com/gws/apigateway/api/f41ddfcced3ac2255a3c599ca51140bf9130534da8fcf791fb5c25a0e7d09f98/benchjava/benchdb/benchdb/2 | jq . 
echo ""

echo "Testing PUT call"
curl -X PUT "https://service.eu.apiconnect.ibmcloud.com/gws/apigateway/api/f41ddfcced3ac2255a3c599ca51140bf9130534da8fcf791fb5c25a0e7d09f98/benchjava/benchdb/benchdb/2?size=5&sorts=5" | jq . 
echo ""

echo "Test VRP Algorithm"

curl -X POST -H "Content-Type: text/plain" -d "$(cat E016-03m.dat)" "https://service.eu.apiconnect.ibmcloud.com/gws/apigateway/api/f41ddfcced3ac2255a3c599ca51140bf9130534da8fcf791fb5c25a0e7d09f98/vrp/java?vns=false" | jq . 
echo ""

echo "Javascript"
echo "Test RSA Algorithm"

curl -X GET "https://service.eu.apiconnect.ibmcloud.com/gws/apigateway/api/f41ddfcced3ac2255a3c599ca51140bf9130534da8fcf791fb5c25a0e7d09f98/rsa/js?n=9010760112349&e=65537&cipher=1234" | jq . 
echo ""

### BenchDB API (Javascript)

echo "Test BenchDB API"

echo "Testing GET call"
curl -X GET https://service.eu.apiconnect.ibmcloud.com/gws/apigateway/api/f41ddfcced3ac2255a3c599ca51140bf9130534da8fcf791fb5c25a0e7d09f98/benchjs/benchdb/query/benchdb/absolute | jq . 
echo ""

echo "Testing POST call"
curl -X POST "https://service.eu.apiconnect.ibmcloud.com/gws/apigateway/api/f41ddfcced3ac2255a3c599ca51140bf9130534da8fcf791fb5c25a0e7d09f98/benchjs/benchdb/benchdb?size=5&ndocs=2&sorts=5" | jq . 
echo ""

echo "Testing DELETE call"
curl -X DELETE https://service.eu.apiconnect.ibmcloud.com/gws/apigateway/api/f41ddfcced3ac2255a3c599ca51140bf9130534da8fcf791fb5c25a0e7d09f98/benchjs/benchdb/benchdb/2 | jq . 
echo ""

echo "Testing PUT call"
curl -X PUT "https://service.eu.apiconnect.ibmcloud.com/gws/apigateway/api/f41ddfcced3ac2255a3c599ca51140bf9130534da8fcf791fb5c25a0e7d09f98/benchjs/benchdb/benchdb/2?size=5&sorts=5" | jq . 
echo ""

echo "Test VRP Algorithm"

curl -X POST -H "Content-Type: text/plain" -d "$(cat E016-03m.dat)" "https://service.eu.apiconnect.ibmcloud.com/gws/apigateway/api/f41ddfcced3ac2255a3c599ca51140bf9130534da8fcf791fb5c25a0e7d09f98/vrp/js?vns=false" | jq . 
echo ""

echo "All done."