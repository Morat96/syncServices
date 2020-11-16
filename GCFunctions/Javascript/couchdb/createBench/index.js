/**
 * A benchmark for CouchDB databases.
 * This function creates a number of random documents and executes a series of sorts to them.
 * @author Matteo Moratello 
 */

var rword = require('rword');

exports.createBench = (req, res) => {

    var db_selected;

    // obtain the database name from the path
    if(req.path && req.path.length) {
        var parts = req.path.split('/');
        if (parts.length != 3) {
            return res.status(500).json({ error: 'The path must be: ../createBench/{dbname}' })
        }
        db_selected = parts[2];
    }
    else {
        return res.status(500).json({ error: 'The path must be: ../createBench/{dbname}' })
    }

    if(!req.query.size) {
        res.status(500).json({ error: 'Parameter size must be defined!' })
    }

    if(!req.query.ndocs) {
        res.status(500).json({ error: 'Parameter ndocs must be defined!' })
    }

    if(!req.query.sorts) {
        res.status(500).json({ error: 'Parameter sorts must be defined!' })
    }

    // parameters
    var size = req.query.size;
    var ndocs = req.query.ndocs;
    var sorts = req.query.sorts;

    var d = new Date();
    var start = d.getTime();

    //var all_docs = '{ "docs" : ['; 
    var all_docs = { "docs" : []};

    // number of documents 
    for (var doc = 0; doc < ndocs; doc++) {
        
        // create a document with random words of size "size"
        var words = rword.rword.generate(size);

        if (sorts > 1) {
            // some sorting of documents
            for (var i = 0; i < sorts - 1; i++) {
                // do sort
                words.sort();
                // do shuffle
                shuffle(words);
            }
        }

        words.sort();

        var content = words.join(' ');

        all_docs.docs[doc] = {};
        all_docs.docs[doc].content = content;
    }

    console.log(all_docs);

    var cloudantUrl = "https://7ec84ee2-f691-4edb-a024-11e71e1153a8-bluemix:3519a078d03db2a98c59f7541c935dda799d990d0f3a4c91e891f4bb34bb7991@7ec84ee2-f691-4edb-a024-11e71e1153a8-bluemix.cloudantnosqldb.appdomain.cloud";

    const nano = require('nano')(cloudantUrl);

    // select database to use
    const couchdb = nano.db.use(db_selected);

    // delete documents from the database
    return couchdb.bulk(all_docs).then((body) => {

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

/**
* Shuffle an array
* @param array array of string
*/
function shuffle(array) {
    array.sort(() => Math.random() - 0.5);
}