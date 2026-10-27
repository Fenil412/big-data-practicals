// MongoDB CRUD cheat sheet. Run in mongosh.
// Sample collection: restaurants
use restaurantDB;

// Create/insert
db.restaurants.insertOne({
  restaurant_id: "R001",
  name: "Sample Dhaba",
  borough: "Manhattan",
  cuisine: "Indian",
  rating: 4.2
});

db.restaurants.insertMany([
  { restaurant_id: "R002", name: "Sample Cafe", borough: "Brooklyn", cuisine: "Cafe", rating: 4.0 },
  { restaurant_id: "R003", name: "Sample Thali House", borough: "Queens", cuisine: "Indian", rating: 4.5 }
]);

// Read
db.restaurants.find();
db.restaurants.find({ cuisine: "Indian" });
db.restaurants.find({ rating: { $gte: 4.2 } });
db.restaurants.find({ borough: "Manhattan" }, { _id: 0, name: 1, cuisine: 1, rating: 1 });

// Update
db.restaurants.updateOne(
  { restaurant_id: "R001" },
  { $set: { rating: 4.4 } }
);
db.restaurants.updateMany(
  { cuisine: "Indian" },
  { $set: { popular: true } }
);

// Delete
db.restaurants.deleteOne({ restaurant_id: "R002" });
db.restaurants.deleteMany({ rating: { $lt: 3.0 } });

// Sort, limit, count, distinct
db.restaurants.find().sort({ rating: -1 }).limit(5);
db.restaurants.countDocuments({ cuisine: "Indian" });
db.restaurants.distinct("cuisine");

// Aggregation pipeline: modern recommended way to group restaurant counts by cuisine
db.restaurants.aggregate([
  { $group: { _id: "$cuisine", restaurantCount: { $sum: 1 }, averageRating: { $avg: "$rating" } } },
  { $sort: { restaurantCount: -1 } }
]);
