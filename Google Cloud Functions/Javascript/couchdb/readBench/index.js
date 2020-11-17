/**
 * A benchmark for CouchDB databases.
 * This function execute a query that search a specific word in the database documents.
 * @author Matteo Moratello
 */

var rword = require('rword');

exports.readBench = (req, res) => {

    var db_selected;
    var query;

    // obtain the database name from the path
    if(req.path && req.path.length) {
        var parts = req.path.split('/');
        if (parts.length != 4) {
            return res.status(500).json({ error: 'The path must be: ../deleteBench/{dbname}/{query}' })
        }
        db_selected = parts[2];
        query = parts[3];
    }
    else {
        return res.status(500).json({ error: 'The path must be: ../deleteBench/{dbname}/{query}' })
    }

    var d = new Date();
    var start = d.getTime();

    var cloudantUrl = "https://7ec84ee2-f691-4edb-a024-11e71e1153a8-bluemix:3519a078d03db2a98c59f7541c935dda799d990d0f3a4c91e891f4bb34bb7991@7ec84ee2-f691-4edb-a024-11e71e1153a8-bluemix.cloudantnosqldb.appdomain.cloud";

    const nano = require('nano')(cloudantUrl);

    // select database to use
    const couchdb = nano.db.use(db_selected);

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
        var out = {
            "response": body,
            "time": time + ' ms'
        }
        return res.status(200).json(out);
    }).catch((error) => {
        console.log('Error: ' + error.message);
        return res.status(500).json({'error': error});
    });
  };