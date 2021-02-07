function main(message) {
    var cloudantOrError = getCloudantAccount(message);
    if (typeof cloudantOrError !== 'object') {
      return Promise.reject(cloudantOrError);
    }
    var cloudant = cloudantOrError;
    var dbName = message.dbname;
    var docId = message.docid;
    var docRev = message.docrev;
  
    if(!dbName) {
      return Promise.reject('dbname is required.');
    }
    if(!docId) {
      return Promise.reject('docid is required.');
    }
    if(!docRev) {
      return Promise.reject('docrev is required.');
    }
    var cloudantDb = cloudant.use(dbName);
  
    return destroy(cloudantDb, docId, docRev);
  
  }
  
  /**
   * Delete document by id and rev.
   */
  function destroy(cloudantDb, docId, docRev) {
    return new Promise(function(resolve, reject) {
      cloudantDb.destroy(docId, docRev, function(error, response) {
        if (!error) {
          resolve(response);
        } else {
          reject(error);
        }
      });
    });
  }
  
  function getCloudantAccount(message) {
    // full cloudant URL - Cloudant NPM package has issues creating valid URLs
    // when the username contains dashes (common in Bluemix scenarios)
    var cloudantUrl;
  
    if (message.url) {
      // use bluemix binding
      cloudantUrl = message.url;
    } else {
      if (!message.host) {
        return 'cloudant account host is required.';
      }
      if (!message.username) {
        return 'cloudant account username is required.';
      }
      if (!message.password) {
        return 'cloudant account password is required.';
      }
  
      cloudantUrl = "http://" + message.username + ":" + message.password + "@" + message.host;
    }
  
    return require('@cloudant/cloudant')({
      url: cloudantUrl
    });
  }

  exports.main = main;