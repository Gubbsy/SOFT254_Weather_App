const functions = require('firebase-functions');
const admin = require('firebase-admin');

exports.expireData = functions.database.ref("{weather-data = **}")
.onWrite((snap, context) => {
  var ref = event.data.ref.parent; // reference to the items
  var now = Date.now();
  var cutoff = now - 2 * 60 * 60 * 1000;
  var oldItemsQuery = ref.orderByChild('timestamp').endAt(cutoff);
  return oldItemsQuery.once('value', function(snapshot) {
    // create a map with all children that need to be removed
    var updates = {};
    snapshot.forEach(function(child) {
      updates[child.key] = null
    });
    // execute all updates in one go and return the result to end the function
    return ref.update(updates);
  });
});

exports.makeUppercase = functions.database.ref('/weather-info/{data}')
    .onCreate((snapshot, context) => {
      // Grab the current value of what was written to the Realtime Database.
    //  const original = snapshot.val();
      console.log("Pleeeeeeeeeeeeease woooooooooork!");
      //const uppercase = original.toUpperCase();
      // You must return a Promise when performing asynchronous tasks inside a Functions such as
      // writing to the Firebase Realtime Database.
      // Setting an "uppercase" sibling in the Realtime Database returns a Promise.
      //return snapshot.ref.parent.child('uppercase').set(uppercase);
    });
