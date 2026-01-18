# How to Deploy to Local Kubernetes from GitHub Actions

**The Problem**: GitHub's servers (in the cloud) cannot see your laptop (where your Kubernetes is running).

**The Solution**: Use a **Self-Hosted Runner**.
This turns your laptop into a "worker" for GitHub. When the pipeline runs, GitHub sends the command to your laptop, and your laptop executes `kubectl apply`.

## Order of Operations for Success
1.  **Setup the Runner** on your laptop (Steps 1 & 2 below).
2.  **Verify** it says "Listening for Jobs" in your PowerShell.
3.  **Push** the `.github/workflows/cd.yml` file to GitHub.
4.  **Watch** the "Actions" tab. The job will get picked up by your laptop instantly!

## Step 1: Create Runner in GitHub
1.  Go to your GitHub Repository.
2.  **Settings** -> **Actions** -> **Runners**.
3.  Click **New self-hosted runner**.
4.  Select your OS (**Windows**).

## Step 2: Install Runner on Laptop
Run the commands GitHub gives you in PowerShell (as Administrator):
1.  Create a folder: `mkdir actions-runner; cd actions-runner`
2.  Download the runner (copy the command from GitHub).
3.  Extract it (`tar` command).
4.  Configure it (`./config.cmd`). Accept all defaults (just press Enter).
5.  Run it: `./run.cmd`.

## Step 3: Update Pipeline
We need to change the pipeline to use your laptop instead of ubuntu-latest.

**File**: `.github/workflows/cd.yml`
```yaml
jobs:
  deploy-to-k8s:
    runs-on: self-hosted  # <--- CHANGED FROM ubuntu-latest
    steps:
      ...
```
Now, when the pipeline runs, it executes ON YOUR LAPTOP, so it has access to your local `kubectl`!
