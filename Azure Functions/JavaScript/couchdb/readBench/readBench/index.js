/**
 * A benchmark for CouchDB databases.
 * This function updates a number of random documents and executes a series of sorts to them.
 * @author Matteo Moratello 
 */

module.exports = async function (context, req) {

    context.log('JavaScript benchmark for CouchDB databases');

    var dbname = context.bindingData.dbname;
    var query = context.bindingData.query;

    var d = new Date();
    var start = d.getTime();

    var cloudantUrl = "https://7ec84ee2-f691-4edb-a024-11e71e1153a8-bluemix:3519a078d03db2a98c59f7541c935dda799d990d0f3a4c91e891f4bb34bb7991@7ec84ee2-f691-4edb-a024-11e71e1153a8-bluemix.cloudantnosqldb.appdomain.cloud";

    const nano = require('nano')(cloudantUrl);

    // select database to use
    const couchdb = nano.db.use(dbname);

    const q = {
        selector: {
            "content": {
                "$regex": query
            }
        }
    };

    return couchdb.find(q).then((body) => {

        d = new Date();
        var end = d.getTime();
        var time = end - start;
        console.log("Time for sorting: " + time + " ms");

        return context.res = {
            status: 200,
            body: {
                "response": body,
                "time": time + ' ms'
            }
        }
    }).catch((error) => {
        console.log('Error: ' + error.message);
        return context.res = {
            status: 400,
            body: {
                "error": error
            }
        }
    });
}