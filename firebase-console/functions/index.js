const functions = require("firebase-functions");

exports.expireData = functions.firestore.document()
.onWrite((event) => {
  console.log("Reached Line 5");
  var collectionReference = event.data.parent.data
  var dataReference = event.data.parent.data.orderByChild('postTime').endAt(expireDataAfter);
  console.log("Reached line 7");
  var currentTime = Date.now();
  var expireDataAfter = now - 1000 * 60 * 60 * 24 * 3;
  return dataReference.once('data', function(snapshot) {
    var dataToBeDeleted = {};
    snapshot.forEach(function(child) {
      dataToBeDeleted[child.key] = null
    });
    return collectionReference.update(dataToBeDeleted);
  });
})
