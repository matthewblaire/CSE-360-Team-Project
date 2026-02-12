#!/bin/bash

set -e  # Exit on any error

# UPDATE THESE TWO FIELDS FIRST
BRANCH_NAME="code-edits"
COMMIT_MSG="Small edits to title bar and git-pr.sh clarification."

# Stash any uncommitted changes first
echo "💾 Checking for uncommitted changes..."
if ! git diff --quiet || ! git diff --cached --quiet; then
    echo "Stashing uncommitted changes..."
    git stash push -m "Auto-stash before PR workflow on $(date)"
fi

echo "🔄 Switching to master branch..."
git checkout master || { echo "❌ Failed to checkout master"; exit 1; }

echo "📥 Pulling latest changes from origin/master..."
git pull origin master || { echo "❌ Failed to pull from master"; exit 1; }

echo "🌿 Creating and switching to $BRANCH_NAME..."
git checkout -b "$BRANCH_NAME" || { echo "❌ Failed to create branch (may already exist)"; exit 1; }

# Restore stashed changes if any
if git stash list | grep -q "Auto-stash before PR workflow"; then
    echo "📤 Restoring stashed changes..."
    git stash pop || { echo "⚠️ Stash pop had conflicts - resolve them manually"; exit 1; }
fi

echo "📂 Staging all changes..."
git add .

echo "📝 Committing changes..."
git commit -m "$COMMIT_MSG" || { echo "❌ Nothing to commit or commit failed"; exit 1; }

echo "🚀 Pushing branch to remote..."
git push -u origin "$BRANCH_NAME" || { echo "❌ Failed to push to remote"; exit 1; }

echo ""
echo "✅ Success! Go to GitHub to create your pull request."
echo "   Branch: $BRANCH_NAME"
