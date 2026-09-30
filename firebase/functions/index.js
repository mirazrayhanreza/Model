const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();
const db = admin.firestore();

/**
 * Triggered whenever a new booking is created in Firestore
 * Automatically sets escrow lock and calculates platform commission
 */
exports.onBookingCreated = functions.firestore
  .document("bookings/{bookingId}")
  .onCreate(async (snap, context) => {
    const booking = snap.data();
    const bookingId = context.params.bookingId;
    const totalAmount = Number(booking.total_amount || 0);

    const platformFee = totalAmount * 0.15; // 15% platform fee
    const modelPayout = totalAmount - platformFee;

    return snap.ref.set(
      {
        escrow_status: "LOCKED_IN_ESCROW",
        platform_fee: platformFee,
        model_payout: modelPayout,
        created_timestamp: admin.firestore.FieldValue.serverTimestamp(),
      },
      { merge: true }
    );
  });

/**
 * Triggered when a new chat message is created
 * Automatically updates lastMessage on the chat thread
 */
exports.onChatMessage = functions.firestore
  .document("chats/{chatId}/messages/{messageId}")
  .onCreate(async (snap, context) => {
    const message = snap.data();
    const chatId = context.params.chatId;

    return db.doc(`chats/${chatId}`).set(
      {
        last_message: message.content || "[Image]",
        last_sender_id: message.sender_id || "",
        last_timestamp: admin.firestore.FieldValue.serverTimestamp(),
      },
      { merge: true }
    );
  });

/**
 * HTTP Endpoint to get backend status and Firebase health
 */
exports.healthCheck = functions.https.onRequest((req, res) => {
  res.json({
    status: "online",
    project: "modol-connect",
    service: "Modol Connect Cloud Functions Backend",
    timestamp: new Date().toISOString(),
  });
});
