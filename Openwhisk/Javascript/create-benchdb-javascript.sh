### A benchmark for CouchDB databases ### 

echo "Creating POST action: createBench"
cd createBench
zip -r -q action.zip *
wsk action update createBench action.zip --kind nodejs:10 --web true
cd ..

echo "Creating DELETE action: deleteBench"
cd deleteBench
zip -r -q action.zip *
wsk action update deleteBench action.zip --kind nodejs:10 --web true
cd ..

echo "Creating PUT action: updateBench"
cd updateBench
zip -r -q action.zip *
wsk action update updateBench action.zip --kind nodejs:10 --web true
cd ..

echo "Creating GET action: readBench"
cd readBench
zip -r -q action.zip *
wsk action update readBench action.zip --kind nodejs:10 --web true
cd ..

echo "All Done."