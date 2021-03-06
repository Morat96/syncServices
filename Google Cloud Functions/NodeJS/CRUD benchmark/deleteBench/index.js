/**
 * A benchmark for CouchDB databases.
 * This function deletes a number of documents from a CouchDB database at random.
 * @author Matteo Moratello
 */

var rword = require('rword');

exports.deleteBench = (req, res) => {

    var db_selected;
    var size;

    // obtain the database name from the path
    if(req.path && req.path.length) {
        var parts = req.path.split('/');
        if (parts.length != 4) {
            return res.status(500).json({ error: 'The path must be: ../deleteBench/{dbname}/{count}' })
        }
        db_selected = parts[2];
        size = parts[3];
    }
    else {
        return res.status(500).json({ error: 'The path must be: ../deleteBench/{dbname}/{count}' })
    }

    var d = new Date();
    var start = d.getTime();

    var cloudantUrl = "https://7ec84ee2-f691-4edb-a024-11e71e1153a8-bluemix:3519a078d03db2a98c59f7541c935dda799d990d0f3a4c91e891f4bb34bb7991@7ec84ee2-f691-4edb-a024-11e71e1153a8-bluemix.cloudantnosqldb.appdomain.cloud";

    const nano = require('nano')(cloudantUrl);

    // select database to use
    const couchdb = nano.db.use(db_selected);

    // get list of documents in the current database
    return couchdb.list().then((body) => {

        // number of documents
        var docsCount = body.total_rows;

        if (size > docsCount) {
            return res.status(500).json({"message": "The number of docs to delete is " + size + ", but the docs in the database '" + db_selected + "' are " + docsCount });
        }

        // select a random list of indices
        var list = getRandomList(size, docsCount);

        var docToDelete;
        docToDelete = { "docs" : []};

        // create the object
        for (var i = 0; i < size; i++) {
            docToDelete.docs[i] = {};
            docToDelete.docs[i]._id = body.rows[list[i]].id;
            docToDelete.docs[i]._rev = body.rows[list[i]].value.rev;
            docToDelete.docs[i]._deleted = true;
        }

        // delete documents from the database
        return couchdb.bulk(docToDelete).then((body) => {

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
    }).catch((error) => {
        console.log('Error: ' + error.message);
        return res.status(500).json({'error': error});
    });
  };

/**
* Get an array composed of random values
* @param size size of the array to generate
* @param max generate numbers between 0 and (max - 1)
* @returns array of random numbers
*/
function getRandomList(size, max) {
    var arr = [];
    while(arr.length < size){
        var r = Math.floor(Math.random() * max);
        if(arr.indexOf(r) === -1) arr.push(r);
    }
    return arr;
}