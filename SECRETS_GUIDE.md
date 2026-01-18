# How to Configure DockerHub Secrets

To allow GitHub Actions to push your Docker image to DockerHub, you need to provide it with your credentials securely.

## Step 1: Get your DockerHub Token

1.  Log in to [Docker Hub](https://hub.docker.com/).
2.  Click on your **Profile Picture** in the top right corner and select **Account Settings**.
3.  Go to the **Security** tab (in the left menu).
4.  Click the blue **New Access Token** button.
5.  **Description**: Give it a name like `github-actions-cicd`.
6.  **Access permissions**: Select **Read & Write**.
7.  Click **Generate**.
8.  **COPY THIS TOKEN IMMEDIATELY**. You will not be able to see it again.

## Step 2: Add Secrets to GitHub

1.  Go to your project's repository on **GitHub**.
2.  Click on the **Settings** tab (usually the last tab at the top).
3.  In the left sidebar, verify you are in the "Security" section, find **Secrets and variables**, and click **Actions**.
4.  Click the green **New repository secret** button.

### Secret 1: Username
-   **Name**: `DOCKERHUB_USERNAME`
-   **Secret**: Enter your DockerHub username (e.g., `vinay123` - *not* your email).
-   Click **Add secret**.

### Secret 2: Token
-   **Name**: `DOCKERHUB_TOKEN`
-   **Secret**: Paste the long token string you copied in Step 1.
-   Click **Add secret**.

## Step 3: (Optional) Kubernetes Config
For the CD Pipeline (`cd.yml`).

> [!IMPORTANT]
> Since you are running Kubernetes LOCALLY (Docker Desktop), **you cannot use this secret**. GitHub servers cannot talk to your laptop's `localhost`.
> Do **NOT** add this secret if you are just demonstrating locally. The pipeline is smart enough to skip the deploy step if this is missing.

If you had a **Real Cloud Cluster** (like AWS EKS), you would:
1.  Run `cat ~/.kube/config` (Linux/Mac) or `type %USERPROFILE%\.kube\config` (Windows).
2.  Copy the entire content.
3.  Add it as a secret named `KUBE_CONFIG`.
