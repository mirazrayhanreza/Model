# 🔥 Modol Connect - Firebase Backend Setup & Guide (ফায়ারবেস ব্যাকএন্ড গাইড)

## 📌 ১. ওভারভিউ (Overview)
Modol Connect-এ Firebase Backend সম্পূর্ণরূপে ইন্টিগ্রেট করা হয়েছে।
- **Firebase Project ID**: `modol-connect`
- **Firebase Storage Bucket**: `modol-connect.firebasestorage.app`
- **Web API Key**: `AIzaSyA14wj8tZCED9AsSbyvy_SO1zS_Q_AR9nA`
- **Google Services File**: Android অ্যাপে `app/google-services.json` কনফিগার করা আছে।

---

## 🚀 ২. অ্যাডমিন প্যানেল এবং ফায়ারবেস কন্ট্রোল হাব (Admin Panel & Firebase Hub)
অ্যাডমিন প্যানেলে লগইন করে সরাসরি ব্রাউজার থেকে Firebase Storage-এ ফাইল/ছবি আপলোড এবং Firestore সিঙ্ক করা যায়:

1. **অ্যাডমিন লগইন লিঙ্ক**:
   `http://your-server-or-domain/backend/admin/login.php`
   - **ইমেইল**: `admin@modolconnect.com`
   - **পাসওয়ার্ড**: `admin123`

2. **ফায়ারবেস কন্ট্রোল হাব**:
   - লগইন করার পর বাম পাশের মেনু থেকে **Firebase Hub**-এ ক্লিক করুন (`admin/firebase.php`)।
   - এখান থেকে মডেলের ছবি, পেমেন্ট রসিদ, চ্যাট ইমেজ সরাসরি **Firebase Storage**-এ আপলোড করতে পারবেন।
   - আপলোড করার সাথে সাথে লাইভ ডাউনলোড লিঙ্ক ও প্রিভিউ দেখতে পাবেন।
   - **Sync to Firestore** বাটনে ক্লিক করে ডেটাবেসের সকল মডেল Firestore-এ এক ক্লিকে সিঙ্ক করতে পারবেন।

---

## 📡 ৩. মোবাইল অ্যাপ ও এপিআই আপলোড (Firebase Storage Upload API)
অ্যান্ড্রয়েড অ্যাপ বা যেকোনো ক্লায়েন্ট থেকে সরাসরি Firebase Storage-এ আপলোড করার জন্য API এন্ডপয়েন্ট:

- **URL**: `POST /backend/api/firebase_upload.php`
- **Multipart Form Data**:
  - `file`: ইমেজ বা ডকুমেন্ট ফাইল
  - `folder`: `models`, `receipts`, `users`, অথবা `chats`
- **বা Base64 JSON Payload**:
  ```json
  {
    "base64_data": "data:image/jpeg;base64,...",
    "file_name": "receipt_101.jpg",
    "folder": "receipts"
  }
  ```
- **সফল রেসপন্স (200 OK)**:
  ```json
  {
    "status": "success",
    "message": "File uploaded to Firebase Storage successfully",
    "data": {
      "url": "https://firebasestorage.googleapis.com/v0/b/modol-connect.firebasestorage.app/o/receipts%2F...",
      "remote_path": "receipts/1711223344_abc123.jpg",
      "bucket": "modol-connect.firebasestorage.app"
    }
  }
  ```

---

## 🛠️ ৪. ফায়ারবেস ক্লাউড ডিপ্লয়মেন্ট (Firebase Cloud Deploy - 1 Click)
যদি আপনি সম্পূর্ণ ব্যাকএন্ড সরাসরি Google Firebase সার্ভারে (Serverless Cloud Functions + Firestore + Storage) হোস্ট করতে চান:

```bash
cd firebase
chmod +x setup_firebase.sh
./setup_firebase.sh
```
অথবা ম্যানুয়ালি:
```bash
npm install -g firebase-tools
firebase login
firebase use modol-connect
firebase deploy
```

---

## 🔐 ৫. সার্ভিস অ্যাকাউন্ট কি (Service Account Key)
যদি অ্যাডমিন ব্যাকএন্ড থেকে আনলিমিটেড প্রিভিলেজ চান:
1. Google Cloud Console বা Firebase Console-এ যান: **Project Settings > Service accounts**.
2. **Generate new private key** ক্লিক করে JSON ফাইলটি ডাউনলোড করুন।
3. ফাইলটি অ্যাডমিন প্যানেলের **Firebase Hub** (`admin/firebase.php`) থেকে আপলোড করুন অথবা `backend/config/serviceAccountKey.json` হিসেবে সেভ করুন।
