# Cloud Migration Guide (AWS/GCP)

**Purpose**: This document outlines the exact steps required to migrate this project from Local Kubernetes to a Public Cloud Provider (AWS EKS or Google GKE) if required.

---

## Option 1: AWS EKS (Elastic Kubernetes Service)

### 1. Create Cluster
You can use `eksctl` (CLI tool) to create a cluster in 1 command:
```bash
eksctl create cluster --name java-cicd-cluster --region us-east-1 --nodegroup-name standard-workers --node-type t3.medium --nodes 2
```
*   *Warning*: This costs ~$0.10/hour (~$72/month) plus load balancer fees.

### 2. Update GitHub Secrets
To allow GitHub Actions to deploy to AWS, you need to add these secrets:
*   `AWS_ACCESS_KEY_ID`: Your IAM User Key.
*   `AWS_SECRET_ACCESS_KEY`: Your IAM User Secret.
*   `KUBE_CONFIG`: The content of your `~/.kube/config` file after creating the cluster.

### 3. Update Pipeline (`cd.yml`)
Remove `runs-on: self-hosted` and use standard `ubuntu-latest`.

```yaml
jobs:
  deploy-to-eks:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Configure AWS Credentials
        uses: aws-actions/configure-aws-credentials@v1
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: us-east-1
      
      - name: Update Kubeconfig
        run: aws eks update-kubeconfig --name java-cicd-cluster --region us-east-1

      - name: Deploy
        run: kubectl apply -k k8s/
```

---

## Option 2: Google GKE (Google Kubernetes Engine)

### 1. Create Cluster
Using `gcloud` CLI:
```bash
gcloud container clusters create java-cicd-cluster --zone us-central1-a --num-nodes 1
```

### 2. Update GitHub Secrets
*   `GKE_PROJECT`: Your Google Cloud Project ID.
*   `GKE_SA_KEY`: The JSON key of a Service Account with "Kubernetes Engine Developer" role.

### 3. Update Pipeline (`cd.yml`)
```yaml
jobs:
  deploy-to-gke:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: google-github-actions/auth@v1
        with:
          credentials_json: ${{ secrets.GKE_SA_KEY }}
      
      - uses: google-github-actions/get-gke-credentials@v1
        with:
          cluster_name: java-cicd-cluster
          location: us-central1-a

      - name: Deploy
        run: kubectl apply -k k8s/
```
