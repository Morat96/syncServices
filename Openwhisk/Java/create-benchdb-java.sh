### A benchmark for CouchDB databases ### 

echo "Creating the package: benchdbJava"
wsk -i package update --shared yes benchdbJava -a description "A benchmark for CouchDB databases. Designed for Openwhisk, written in Java."

echo "Creating POST action: createBench"
wsk action update benchdbJava/createBench createBench/target/main.jar --main Main --kind java:8 --web true

echo "Creating DELETE action: deleteBench"
wsk action update benchdbJava/deleteBench deleteBench/target/main.jar --main Main --kind java:8 --web true

echo "Creating PUT action: updateBench"
wsk action update benchdbJava/updateBench updateBench/target/main.jar --main Main --kind java:8 --web true

echo "Creating GET action: readBench"
