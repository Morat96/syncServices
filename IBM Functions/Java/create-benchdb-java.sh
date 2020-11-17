### A benchmark for CouchDB databases ### 

echo "Creating the package: benchdbJava"
ibmcloud wsk package update --shared yes benchdbJava -a description "A benchmark for CouchDB databases. Designed for Openwhisk, written in Java." \
-a parameters '[  {"name":"username", "required":true, "description": "Your Cloudant username"}, {"name":"password", "required":true, "type":"password", "description": "Your Cloudant password"}, {"name":"host", "required":true, "description": "Your Cloudant host"} ]' \
-p username 7ec84ee2-f691-4edb-a024-11e71e1153a8-bluemix -p password 3519a078d03db2a98c59f7541c935dda799d990d0f3a4c91e891f4bb34bb7991 -p host https://7ec84ee2-f691-4edb-a024-11e71e1153a8-bluemix.cloudantnosqldb.appdomain.cloud

echo "Creating POST action: createBench"
cd createBench
mvn clean package -q
ibmcloud wsk action update benchdbJava/createBench target/main.jar --main Main --kind java:8 --web true
cd ..

echo "Creating DELETE action: deleteBench"
cd deleteBench
mvn clean package -q
ibmcloud wsk action update benchdbJava/deleteBench target/main.jar --main Main --kind java:8 --web true
cd ..

echo "Creating PUT action: updateBench"
cd updateBench
mvn clean package -q
ibmcloud wsk action update benchdbJava/updateBench target/main.jar --main Main --kind java:8 --web true
cd ..

echo "Creating GET action: readBench"
cd readBench
mvn clean package -q
ibmcloud wsk action update benchdbJava/readBench target/main.jar --main Main --kind java:8 --web true
cd ..