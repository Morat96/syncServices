### A benchmark for CouchDB databases ### 

echo "Creating the package: benchdbJS"
wsk -i package update --shared yes benchdbJS -a description "A benchmark for CouchDB databases. Designed for Openwhisk, written in Javascript." \
-a parameters '[  {"name":"username", "required":true, "description": "Your Cloudant username"}, {"name":"password", "required":true, "type":"password", "description": "Your Cloudant password"}, {"name":"host", "required":true, "description": "Your Cloudant host"} ]' \
-p username 7ec84ee2-f691-4edb-a024-11e71e1153a8-bluemix -p password 3519a078d03db2a98c59f7541c935dda799d990d0f3a4c91e891f4bb34bb7991 -p host 7ec84ee2-f691-4edb-a024-11e71e1153a8-bluemix.cloudantnosqldb.appdomain.cloud

echo "Creating POST action: createBench"
cd createBench
zip -r -q action.zip *
wsk -i action update benchdbJS/createBench action.zip --kind nodejs:10 --web true
cd ..

echo "Creating DELETE action: deleteBench"
cd deleteBench
zip -r -q action.zip *
wsk -i action update benchdbJS/deleteBench action.zip --kind nodejs:10 --web true
cd ..

echo "Creating PUT action: updateBench"
cd updateBench
zip -r -q action.zip *
wsk -i action update benchdbJS/updateBench action.zip --kind nodejs:10 --web true
cd ..

echo "Creating GET action: readBench"
cd readBench
zip -r -q action.zip *
wsk -i action update benchdbJS/readBench action.zip --kind nodejs:10 --web true
cd ..

echo "All Done."