const functions = require("firebase-functions");

exports.expireData = functions.database.ref("weather-info/{weather-data = **}")
.onWrite((snap, context) => {
  var dataReference = snap.data.ref;
  var currentTime = Date.now();
  var expireDataAfter = now - 1000 * 60 * 60 * 24 * 3;
  var oldData = ref.orderByChild('postTime').endAt(expireDataAfter);
  return oldData.once('data', function(snapshot) {
    var dataToBeDeleted = {};
    snapshot.forEach(function(child) {
      dataToBeDeleted[child.key] = null
    });
    return ref.update(dataToBeDeleted);
  });
})
