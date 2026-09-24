param(
    [string]$Message = "Fix Render Docker deployment"
)

$ErrorActionPreference = "Stop"

git add -A
git diff --cached --quiet
if ($LASTEXITCODE -eq 0) {
    Write-Host "No deployment changes are staged. Nothing to push."
    exit 0
}

git commit -m $Message
git push origin main

Write-Host "Pushed to GitHub. In Render, set the service Runtime to Docker and redeploy the latest commit."