#!/bin/bash

set -e  # Exit on any error

BRANCH_NAME="feature/frontend-redesign002"
COMMIT_MSG="(repush) Added improved front-end design for guiFirstAdmin and guiUserLogin"

echo "🔄 Switching to master branch..."
git checkout master || { echo "❌ Failed to checkout master"; exit 1; }

echo "⬇️  Pulling latest changes from origin/master..."
git pull origin master || { echo "❌ Failed to pull from master"; exit 1; }

echo "🌿 Creating and switching to $BRANCH_NAME..."
git checkout -b "$BRANCH_NAME" || { echo "❌ Failed to create branch (may already exist)"; exit 1; }

echo "📦 Staging all changes..."
git add .

echo "💾 Committing changes..."
git commit -m "$COMMIT_MSG" || { echo "❌ Nothing to commit or commit failed"; exit 1; }

echo "🚀 Pushing branch to remote..."
git push -u origin "$BRANCH_NAME" || { echo "❌ Failed to push to remote"; exit 1; }

echo ""
echo "✅ Success! Go to GitHub to create your pull request."
echo "   Branch: $BRANCH_NAME"
