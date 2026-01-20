# Java CI/CD Project

## Overview
This project demonstrates a production-grade CI/CD pipeline for a Java Spring Boot application. It integrates multiple DevOps practices including Automation, Quality Assurance (QA), Security (DevSecOps), and Containerization.

## Application Description
The application is a "Math Service" API built with:
- **Language**: Java 17
- **Framework**: Spring Boot 3
- **Build Tool**: Maven

### Endpoints
- `GET /api/math/prime/{n}`: Checks if `n` is a prime number.
- `GET /api/math/factorial/{n}`: Calculates the factorial of `n`.

## CI/CD Pipeline Architecture
The pipeline is defined in `.github/workflows/ci.yml` and includes the following stages:

| Stage | Purpose | Tool |
|-------|---------|------|
| **Checkout** | Retrieve source code from GitHub | actions/checkout |
| **Setup Runtime** | Install Java 17 environment | actions/setup-java |
| **Linting** | Enforce coding standards and style | Maven Checkstyle Plugin |
| **Unit Tests** | Validate business logic to prevent regressions | JUnit 5 |
| **SAST** | Static Application Security Testing to find code bugs | SpotBugs |
| **SCA** | Software Composition Analysis for dependency vulnerabilities | Trivy (FS Scan) |
| **Build** | Package the application into a JAR | Maven |
| **Docker Build** | Package application into a container image | Docker |
| **Image Scan** | Detect OS & library vulnerabilities in the image | Trivy (Image Scan) |
| **Runtime Test** | Basic smoke test to ensure container starts and responds | curl + docker run |
| **Registry Push** | Publish the trusted image to DockerHub | Docker CLI |

## Continuous Deployment (CD)
This project includes a secondary pipeline `.github/workflows/cd.yml` that triggers automatically after a successful CI build.

### How it works
1.  **Trigger**: Runs only when the "CI/CD Pipeline" completes successfully.
2.  **Deploy**: Uses `kubectl` to apply the manifests in `k8s/` to a live cluster.

### A Note on Local Deployment (Self-Hosted Runner)
Since the Kubernetes cluster is running on your **local machine** (Docker Desktop), we use a **GitHub Actions Self-Hosted Runner**.
- The runner acts as a bridge: it listens for jobs from GitHub but executes them on your local machine.
- This allows the `cd.yml` pipeline to execute `kubectl` commands directly against your local cluster, enabling **true automated CD** even for localhost.
- **Setup**: The runner must be installed and running (`./run.cmd`) on the host machine for the CD stage to pick up the job.

## How to Run

### Prerequisites
- Docker installed
- Java 17 installed (optional, can use Docker)
- Maven installed (optional, can use Docker)

### Local Execution
1. **Build**: `mvn clean package`
2. **Run**: `java -jar target/demo-0.0.1-SNAPSHOT.jar`
3. **Docker Build**: `docker build -t my-java-app .`
4. **Docker Run**: `docker run -p 8080:8080 my-java-app`

### CI/CD Setup (GitHub Actions)
To enable the "Registry Push" stage, you must configure the following **GitHub Secrets** in your repository settings:
1. `DOCKERHUB_USERNAME`: Your DockerHub username.
2. `DOCKERHUB_TOKEN`: Your DockerHub Access Token (Profile -> Security -> New Access Token).

## Automated CD (via Self-Hosted Runner)
To trigger the automated deployment:
1.  Ensure your **Self-Hosted Runner** is online.
2.  Push a commit to `main`.
3.  Wait for the CI pipeline to pass.
4.  The CD pipeline will automatically start, build the manifests, and deploy to your local Docker Desktop Kubernetes.

## Manual Deployment (Fallback)
If you are not using a self-hosted runner, you can deploy manually:

1.  **Prerequisites**: Ensure you have a local Kubernetes cluster running (e.g., Docker Desktop k8s, Minikube, or Kind) and `kubectl` configured.
2.  **Edit Manifest**: Open `k8s/deployment.yaml` and replace `${DOCKERHUB_USERNAME}` with your actual DockerHub username.
3.  **Deploy**:
    ```bash
    kubectl apply -f k8s/deployment.yaml
    kubectl apply -f k8s/service.yaml
    ```
4.  **Verify**:
    ```bash
    kubectl get pods
    kubectl get services
    ```
5.  **Access**:
    - If using Docker Desktop: Visit `http://localhost/api/math/prime/10`
    - If using Minikube: Run `minikube service java-cicd-service`

```
.
├── .github/workflows/ci.yml # CI/CD Pipeline Definition
├── src/                     # Source Code
├── Dockerfile               # Container Definition
├── pom.xml                  # Maven Configuration
├── checkstyle.xml           # Linting Rules
└── README.md                # Project Documentation
```

## Security & Quality Gates
- **Quality**: The build will fail if Unit Tests fail or if Checkstyle finds violations.
- **Security**: The build is configured to scan for vulnerabilities (SCA/SAST). Critical vulnerabilities in the container image can be configured to break the build (currently set to exit 0 for demo purposes).
