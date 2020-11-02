function main(message) {
    var cloudantOrError = getCloudantAccount(message);
    if (typeof cloudantOrError !== 'object') {
      return Promise.reject(cloudantOrError);
    }
    var cloudant = cloudantOrError;
    var dbName = message.dbname;
    var docId = message.docid;
    var viewName = message.viewname;
    var params = {};
  
    if(!dbName) {
      return Promise.reject('dbname is required.');
    }
    if(!docId) {
      return Promise.reject('docid is required.');
    }
    if(!viewName) {
      return Promise.reject('viewname is required.');
    }
    var cloudantDb = cloudant.use(dbName);
  
    if (typeof message.params === 'object') {
      params = message.params;
    } else if (typeof message.params === 'string') {
      try {
        params = JSON.parse(message.params);
      } catch (e) {
        return Promise.reject('params field cannot be parsed. Ensure it is valid JSON.');
      }
    }
  
    return queryView(cloudantDb, docId, viewName, params);
  }
  
  /**
   * Get view by design doc id and view name.
   */
  function queryView(cloudantDb, designDocId, designDocViewName, params) {
    return new Promise(function(resolve, reject) {
      cloudantDb.view(designDocId, designDocViewName, params, function(error, response) {
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