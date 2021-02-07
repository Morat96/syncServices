### TEST BenchDB API

echo "TEST for BenchDB API JS version"

echo "Testing GET call"
curl -X GET https://192.168.30.151:31001/api/23bc46b1-71f6-4ed5-8c54-816aa4f8c502/benchjs/benchdb/query/benchdb/birds -k

echo "Testing POST call"
curl -X POST -d '{"size":"100", "ndocs":"2", "sorts":"200"}' -H "Content-Type: application/json" https://192.168.30.151:31001/api/23bc46b1-71f6-4ed5-8c54-816aa4f8c502/benchjs/benchdb/benchdb -k

echo "Testing DELETE call"
curl -X DELETE https://192.168.30.151:31001/api/23bc46b1-71f6-4ed5-8c54-816aa4f8c502/benchjs/benchdb/benchdb/2 -k

echo "Testing PUT call"
curl -X PUT -d '{"size":"100", "sorts":"200"}' -H "Content-Type: application/json" https://192.168.30.151:31001/api/23bc46b1-71f6-4ed5-8c54-816aa4f8c502/benchjs/benchdb/benchdb/2 -k

echo "All done."