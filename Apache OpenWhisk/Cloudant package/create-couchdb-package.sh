### CouchDB database service for Openwhisk

### Package
### couchdb
### Parameters
### - host (xx.xx.xx.xx)
### - username (admin)
### - password (...)
### - dbname (example_db)

echo "Creating couchdb package"

wsk -i package update --shared yes couchdb -a description "CouchDB database service" \
-a parameters '[  {"name":"username", "required":false, "bindTime":true, "description": "Your CouchDB username"}, {"name":"password", "required":false, "type":"password", "bindTime":true, "description": "Your CouchDB password"}, {"name":"host", "required":true, "bindTime":true, "description": "Your CouchDB host"}, {"name":"dbname", "required":false, "description": "The name of your CouchDB database"}, {"name":"overwrite", "required":false, "type": "boolean"} ]' \
-p host 10.152.183.24:5984 -p username admin -p password sX5IWFOsWX3BKClsxB8G -p dbname example_db

echo "Done."

cd package

echo "Creating couchdb actions"

### Actions
### - create-database
echo "Create database action"

## copy the file
cp ../src/create-database.js index.js
## install dependencies
npm install --silent
## zip all
zip -r -q action.zip *

## create the action 
wsk -i action update couchdb/create-database --kind nodejs:10 action.zip \
-a description 'Create CouchDB database' \
-a parameters '[ {"name":"dbname", "required":true} ]' 

### - read-database
echo "Read database action"

## copy the file
cp ../src/read-database.js index.js
## zip all
zip -r -q action.zip *

## create the action 
wsk -i action update couchdb/read-database --kind nodejs:10 action.zip \
-a description 'Read CouchDB database' \
-a parameters '[ {"name":"dbname", "required":true} ]' 

### - delete-database
echo "Delete database action"

## copy the file
cp ../src/delete-database.js index.js
## zip all
zip -r -q action.zip *

## create the action 
wsk -i action update couchdb/delete-database --kind nodejs:10 action.zip \
-a description 'Delete CouchDB database' \
-a parameters '[ {"name":"dbname", "required":true} ]'

### - list-all-databases
echo "List all databases action"

## copy the file
cp ../src/list-all-databases.js index.js
## zip all
zip -r -q action.zip *

## create the action 
wsk -i action update couchdb/list-all-databases --kind nodejs:10 action.zip \
-a description 'List all CouchDB databases'

### - create-document
echo "Create document action"

## copy the file
cp ../src/create-document.js index.js
## zip all
zip -r -q action.zip *

## create the action 
wsk -i action update couchdb/create-document --kind nodejs:10 action.zip \
-a description 'Create document in database' \
-a parameters '[ {"name":"dbname", "required":true}, {"name":"doc", "required":true, "description": "The JSON document to insert"}, {"name":"params", "required":false} ]'

### - read-document
echo "Read document action"

## copy the file
cp ../src/read-document.js index.js
## zip all
zip -r -q action.zip *

## create the action 
wsk -i action update couchdb/read-document --kind nodejs:10 action.zip \
-a description 'Read document from database' \
-a parameters '[ {"name":"dbname", "required":true}, {"name":"id", "required":true, "description": "The Cloudant document id to fetch"}, {"name":"params", "required":false}]' \
-p id ''

### - write-document
echo "Write document action"

## copy the file
cp ../src/write-document.js index.js
## zip all
zip -r -q action.zip *

## create the action 
wsk -i action update couchdb/write-document --kind nodejs:10 action.zip \
-a description 'Write document in database' \
-a parameters '[ {"name":"dbname", "required":true}, {"name":"doc", "required":true} ]' \
-p doc '{}'

### - update-document
echo "Update document action"

## copy the file
cp ../src/update-document.js index.js
## zip all
zip -r -q action.zip *

## create the action 
wsk -i action update couchdb/update-document --kind nodejs:10 action.zip \
-a description 'Update document in database' \
-a parameters '[ {"name":"dbname", "required":true}, {"name":"doc", "required":true}, {"name":"params", "required":false} ]' \
-p doc '{}'

### - delete-document
echo "Delete document action"

## copy the file
cp ../src/delete-document.js index.js
## zip all
zip -r -q action.zip *

## create the action 
wsk -i action update couchdb/delete-document --kind nodejs:10 action.zip \
-a description 'Delete document from database' \
-a parameters '[ {"name":"dbname", "required":true}, {"name":"docid", "required":true, "description": "The Cloudant document id to delete"},  {"name":"docrev", "required":true, "description": "The document revision number"} ]' \
-p docid '' \
-p docrev ''

### - list-documents
echo "List document action"

## copy the file
cp ../src/list-documents.js index.js
## zip all
zip -r -q action.zip *

## create the action 
wsk -i action update couchdb/list-documents --kind nodejs:10 action.zip \
-a description 'List all docs from database' \
-a parameters '[ {"name":"dbname", "required":true}, {"name":"params", "required":false} ]'

### - exec-query-find
echo "Exec query find action"

## copy the file
cp ../src/exec-query-find.js index.js
## zip all
zip -r -q action.zip *

## create the action 
wsk -i action update couchdb/exec-query-find --kind nodejs:10 action.zip \
-a description 'Execute query against Cloudant Query index' \
-a parameters '[ {"name":"dbname", "required":true}, {"name":"query", "required":true} ]' \
-p query ''

### - exec-query-search
echo "Exec query search action"

## copy the file
cp ../src/exec-query-search.js index.js
## zip all
zip -r -q action.zip *

## create the action 
wsk -i action update couchdb/exec-query-search --kind nodejs:10 action.zip \
-a description 'Execute query against Cloudant search' \
-a parameters '[ {"name":"dbname", "required":true}, {"name":"docid", "required":true}, {"name":"indexname", "required":true}, {"name":"search", "required":true} ]' \
-p docid '' \
-p indexname '' \
-p search ''

### - exec-query-view
echo "Exec query view action"

## copy the file
cp ../src/exec-query-view.js index.js
## zip all
zip -r -q action.zip *

## create the action 
wsk -i action update couchdb/exec-query-view --kind nodejs:10 action.zip \
-a description 'Call view in design document from database' \
-a parameters '[ {"name":"dbname", "required":true}, {"name":"docid", "required":true}, {"name":"viewname", "required":true}, {"name":"params", "required":false} ]' \
-p docid '' \
-p viewname ''

### - manage-bulk-documents
echo "Manage bulk documents action"

## copy the file
cp ../src/manage-bulk-documents.js index.js
## zip all
zip -r -q action.zip *

## create the action 
wsk -i action update couchdb/manage-bulk-documents --kind nodejs:10 action.zip \
-a description 'Create, Update, and Delete documents in bulk' \
-a parameters '[ {"name":"dbname", "required":true}, {"name":"docs", "required":true}, {"name":"params", "required":false} ]' \
-p docs '{}'

echo "Done."

echo "All fine, relax!"
### END





