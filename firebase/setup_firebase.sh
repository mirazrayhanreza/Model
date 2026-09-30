#!/bin/bash
# ==============================================================================
# Modol Connect - Firebase Backend 1-Click Setup & Deploy Script
# ==============================================================================

set -e

echo "🔥 ======================================================================="
echo "🔥  MODOL CONNECT - FIREBASE BACKEND SETUP & DEPLOYMENT"
echo "🔥 ======================================================================="

PROJECT_ID="modol-connect"

echo "Step 1: Checking Node.js and NPM..."
if ! command -v node &> /dev/null; then
    echo "❌ Node.js is required. Please install Node.js 18 or 20."
    exit 1
fi

echo "Step 2: Checking Firebase CLI..."
if ! command -v firebase &> /dev/null; then
    echo "📦 Installing Firebase CLI globally..."
    npm install -g firebase-tools
fi

echo "Step 3: Installing Firebase Cloud Functions dependencies..."
cd "$(dirname "$0")/functions"
npm install
cd ..

echo "Step 4: Setting active Firebase project to: $PROJECT_ID..."
firebase use "$PROJECT_ID" || {
    echo "⚠️ Not logged in or project not associated. Running 'firebase login'..."
    firebase login
    firebase use "$PROJECT_ID"
}

echo "Step 5: Deploying Firestore Rules & Indexes, Storage Rules, and Cloud Functions..."
firebase deploy --only firestore,storage,functions

echo ""
echo "✅ ======================================================================="
echo "✅  FIREBASE BACKEND DEPLOYED SUCCESSFULLY!"
echo "✅  Project: https://console.firebase.google.com/project/$PROJECT_ID/overview"
echo "✅  Storage Bucket: $PROJECT_ID.firebasestorage.app"
echo "✅ ======================================================================="
