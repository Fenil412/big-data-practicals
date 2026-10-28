PRACTICAL 11 — MONGODB MAPREDUCE ON A DOCUMENT COLLECTION
Use the restaurantDB.restaurants collection created by MongoDB_Queries.js.

Legacy MapReduce example (works only in MongoDB environments that still support mapReduce):
In mongosh, run:
var mapCuisine = function () { emit(this.cuisine, 1); };
var reduceCuisine = function (key, values) { return Array.sum(values); };
db.restaurants.mapReduce(mapCuisine, reduceCuisine, { out: "cuisine_counts" });
db.cuisine_counts.find();

Expected shape (depends on inserted data):
{ "_id": "Indian", "value": 2 }
{ "_id": "Cafe", "value": 1 }

IMPORTANT: MongoDB deprecated mapReduce starting in version 5.0; it may not be available in your current environment. For modern MongoDB, use the aggregation pipeline:
db.restaurants.aggregate([
  { $group: { _id: "$cuisine", count: { $sum: 1 } } },
  { $out: "cuisine_counts_aggregation" }
]);
db.cuisine_counts_aggregation.find();

Concept:
Map emits key/value pairs, Reduce combines values for each key. In the example, cuisine is the key and 1 is emitted for each document.
