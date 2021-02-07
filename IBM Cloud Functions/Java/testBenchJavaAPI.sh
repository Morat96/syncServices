### TEST BenchDB API (Javascript)

echo "TEST for BenchDB API Javascript version"

echo "Testing GET call"
curl -X GET https://service.eu.apiconnect.ibmcloud.com/gws/apigateway/api/f41ddfcced3ac2255a3c599ca51140bf9130534da8fcf791fb5c25a0e7d09f98/benchjava/benchdb/query/benchdb/absolute -k

echo "Testing POST call"
curl -X POST -d '{"size":"100", "ndocs":"2", "sorts":"200"}' -H "Content-Type: application/json" https://service.eu.apiconnect.ibmcloud.com/gws/apigateway/api/f41ddfcced3ac2255a3c599ca51140bf9130534da8fcf791fb5c25a0e7d09f98/benchjava/benchdb/benchdb -k

echo "Testing DELETE call"
curl -X DELETE https://service.eu.apiconnect.ibmcloud.com/gws/apigateway/api/f41ddfcced3ac2255a3c599ca51140bf9130534da8fcf791fb5c25a0e7d09f98/benchjava/benchdb/benchdb/2 -k

echo "Testing PUT call"
curl -X PUT -d '{"size":"100", "sorts":"200"}' -H "Content-Type: application/json" https://service.eu.apiconnect.ibmcloud.com/gws/apigateway/api/f41ddfcced3ac2255a3c599ca51140bf9130534da8fcf791fb5c25a0e7d09f98/benchjava/benchdb/benchdb/2 -k

echo "All done."