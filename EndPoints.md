# Serverless EndPoints
## Openwhisk Local Synclab
### Java
---
#### RSA Decryption
```
https://192.168.30.151:31001/api/23bc46b1-71f6-4ed5-8c54-816aa4f8c502/rsa/java
```
---
#### Couchdb
##### GET
```
https://192.168.30.151:31001/api/23bc46b1-71f6-4ed5-8c54-816aa4f8c502/benchjava/benchdb/query/{dbname}/{query}
```
##### POST
```
https://192.168.30.151:31001/api/23bc46b1-71f6-4ed5-8c54-816aa4f8c502/benchjava/benchdb/{dbname}
```
##### PUT
```
https://192.168.30.151:31001/api/23bc46b1-71f6-4ed5-8c54-816aa4f8c502/benchjava/benchdb/{dbname}/{count}
```
##### DELETE
```
https://192.168.30.151:31001/api/23bc46b1-71f6-4ed5-8c54-816aa4f8c502/benchjava/benchdb/{dbname}/{count}
```
---
#### Vehicle Routing Problem
```
https://192.168.30.151:31001/api/23bc46b1-71f6-4ed5-8c54-816aa4f8c502/vrp/java
```
---
### Javascript
#### RSA Decryption
```
https://192.168.30.151:31001/api/23bc46b1-71f6-4ed5-8c54-816aa4f8c502/rsa/js
```
---
#### Couchdb
##### GET
```
https://192.168.30.151:31001/api/23bc46b1-71f6-4ed5-8c54-816aa4f8c502/benchjs/benchdb/query/{dbname}/{query}
```
##### POST
```
https://192.168.30.151:31001/api/23bc46b1-71f6-4ed5-8c54-816aa4f8c502/benchjs/benchdb/{dbname}
```
##### PUT
```
https://192.168.30.151:31001/api/23bc46b1-71f6-4ed5-8c54-816aa4f8c502/benchjs/benchdb/{dbname}/{count}
```
##### DELETE
```
https://192.168.30.151:31001/api/23bc46b1-71f6-4ed5-8c54-816aa4f8c502/benchjs/benchdb/{dbname}/{count}
```
---
#### Vehicle Routing Problem
```
https://192.168.30.151:31001/api/23bc46b1-71f6-4ed5-8c54-816aa4f8c502/vrp/js
```
---
## Openwhisk IBM Functions
### Java
---
#### RSA Decryption
```
https://service.eu.apiconnect.ibmcloud.com/gws/apigateway/api/f41ddfcced3ac2255a3c599ca51140bf9130534da8fcf791fb5c25a0e7d09f98/rsa/java
```
---
#### Couchdb
##### GET
```
https://service.eu.apiconnect.ibmcloud.com/gws/apigateway/api/f41ddfcced3ac2255a3c599ca51140bf9130534da8fcf791fb5c25a0e7d09f98/benchjava/benchdb/query/{dbname}/{query}
```
##### POST 
```
https://service.eu.apiconnect.ibmcloud.com/gws/apigateway/api/f41ddfcced3ac2255a3c599ca51140bf9130534da8fcf791fb5c25a0e7d09f98/benchjava/benchdb/{dbname}
```
##### PUT 
```
https://service.eu.apiconnect.ibmcloud.com/gws/apigateway/api/f41ddfcced3ac2255a3c599ca51140bf9130534da8fcf791fb5c25a0e7d09f98/benchjava/benchdb/{dbname}/{count}
```
##### DELETE 
```
https://service.eu.apiconnect.ibmcloud.com/gws/apigateway/api/f41ddfcced3ac2255a3c599ca51140bf9130534da8fcf791fb5c25a0e7d09f98/benchjava/benchdb/{dbname}/{count}
```
---
#### Vehicle Routing Problem
```
https://service.eu.apiconnect.ibmcloud.com/gws/apigateway/api/f41ddfcced3ac2255a3c599ca51140bf9130534da8fcf791fb5c25a0e7d09f98/vrp/java
```
---
### Javascript
#### RSA Decryption
```
https://service.eu.apiconnect.ibmcloud.com/gws/apigateway/api/f41ddfcced3ac2255a3c599ca51140bf9130534da8fcf791fb5c25a0e7d09f98/rsa/js
```
---
#### Couchdb
##### GET
```
https://service.eu.apiconnect.ibmcloud.com/gws/apigateway/api/f41ddfcced3ac2255a3c599ca51140bf9130534da8fcf791fb5c25a0e7d09f98/benchjs/benchdb/query/{dbname}/{query}
```
##### POST 
```
https://service.eu.apiconnect.ibmcloud.com/gws/apigateway/api/f41ddfcced3ac2255a3c599ca51140bf9130534da8fcf791fb5c25a0e7d09f98/benchjs/benchdb/{dbname}
```
##### PUT 
```
https://service.eu.apiconnect.ibmcloud.com/gws/apigateway/api/f41ddfcced3ac2255a3c599ca51140bf9130534da8fcf791fb5c25a0e7d09f98/benchjs/benchdb/{dbname}/{count}
```
##### DELETE 
```
https://service.eu.apiconnect.ibmcloud.com/gws/apigateway/api/f41ddfcced3ac2255a3c599ca51140bf9130534da8fcf791fb5c25a0e7d09f98/benchjs/benchdb/{dbname}/{count}
```
---
#### Vehicle Routing Problem
```
https://service.eu.apiconnect.ibmcloud.com/gws/apigateway/api/f41ddfcced3ac2255a3c599ca51140bf9130534da8fcf791fb5c25a0e7d09f98/vrp/js
```
---
## Azure Functions
### Java
---
#### RSA Decryption
```
https://rsajava.azurewebsites.net/api/rsa
```
---
#### Couchdb
##### GET
```
https://couchdbjava.azurewebsites.net/api/readBench/{dbname}/{query}
```
##### POST 
```
https://couchdbjava.azurewebsites.net/api/createBench/{dbname}
```
##### PUT 
```
https://couchdbjava.azurewebsites.net/api/updateBench/{dbname}/{count}
```
##### DELETE 
```
https://couchdbjava.azurewebsites.net/api/deleteBench/{dbname}/{count}
```
---
#### Vehicle Routing Problem
```
https://vrpjava.azurewebsites.net/api/vrp
```
---
### Javascript
#### RSA Decryption
```
https://rsajs.azurewebsites.net/api/rsa
```
---
#### Couchdb
##### GET
```
https://couchdbjs.azurewebsites.net/api/readBench/{dbname:alpha}/{query:alpha}
```
##### POST 
```
https://couchdbjs.azurewebsites.net/api/createBench/{dbname:alpha}
```
##### PUT 
```
https://couchdbjs.azurewebsites.net/api/updateBench/{dbname:alpha}/{count:int}
```
##### DELETE 
```
https://couchdbjs.azurewebsites.net/api/deleteBench/{dbname:alpha}/{count:int}
```
---
#### Vehicle Routing Problem
```
https://vrpjs.azurewebsites.net/api/vrp
```
