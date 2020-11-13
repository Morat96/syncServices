### TEST BenchDB API

echo "TEST for BenchDB API JS version"

echo "Testing GET call"
curl -X GET https://service.eu.apiconnect.ibmcloud.com/gws/apigateway/api/f41ddfcced3ac2255a3c599ca51140bf9130534da8fcf791fb5c25a0e7d09f98/benchjs/benchdb/query/benchdb/birds 

echo "Testing POST call"
curl -X POST -d '{"size":"100", "ndocs":"2", "sorts":"200"}' -H "Content-Type: application/json" https://service.eu.apiconnect.ibmcloud.com/gws/apigateway/api/f41ddfcced3ac2255a3c599ca51140bf9130534da8fcf791fb5c25a0e7d09f98/benchjs/benchdb/benchdb 

echo "Testing DELETE call"
curl -X DELETE https://service.eu.apiconnect.ibmcloud.com/gws/apigateway/api/f41ddfcced3ac2255a3c599ca51140bf9130534da8fcf791fb5c25a0e7d09f98/benchjs/benchdb/benchdb/2

echo "Testing PUT call"
curl -X PUT -d '{"size":"100", "sorts":"200"}' -H "Content-Type: application/json" https://service.eu.apiconnect.ibmcloud.com/gws/apigateway/api/f41ddfcced3ac2255a3c599ca51140bf9130534da8fcf791fb5c25a0e7d09f98/benchjs/benchdb/benchdb/2 

echo "All done."