# DevOps CI/CD Project Report

**Project Title**: Advanced Java CI/CD Pipeline with DevSecOps
**Student Name**: Vinay Reddy
**Date**: January 16, 2026

---

## 1. Problem Background & Motivation

### Background
In modern software development, manual deployment processes are prone to human error, security vulnerabilities, and slow release cycles. Traditional "waterfall" builds often discover bugs and security issues too late in the development lifecycle, leading to costly fixes and delayed features.

### Motivation
The goal of this project is to implement a **DevSecOps** approach by building a fully automated Continuous Integration and Continuous Deployment (CI/CD) pipeline. The motivation is to shift security and quality checks "left" (earlier in the process), ensuring that only verified, secure, and production-ready code is deployed. This project aims to demonstrate how automation can reduce technical debt and improve software delivery speed and reliability.

---

## 2. Application Overview

The project utilizes a **Spring Boot** application named "Math Service".

*   **Architecture**: RESTful Microservice
*   **Language**: Java 17
*   **Framework**: Spring Boot 3.2.1
*   **Build Tool**: Maven

### Key Features
*   **Prime Number API**: `GET /api/math/prime/{n}` - Determines if a number is prime.
*   **Factorial API**: `GET /api/math/factorial/{n}` - Calculates the factorial of a number.
*   **Health Check**: Standard business logic validation used for smoke testing.

### Source Code
The application includes:
*   `MathService.java`: Encapsulates business logic, making it effectively unit-testable.
*   `MathController.java`: A thin controller layer exposing endpoints.
*   `MathServiceTest.java`: Comprehensive JUnit tests covering edge cases (negative numbers, 0, 1).

---

## 3. CI/CD Architecture Diagram

```mermaid
graph LR
    User[Developer] -->|Push Code| GitHub[GitHub Repository]
    GitHub -->|Trigger| GA[GitHub Actions Runner]
    
    subgraph "CI Pipeline (Build & Test)"
        GA --> Checkout
        Checkout --> Setup[Setup Java 17]
        Setup --> Lint[Checkstyle Linting]
        Lint --> UnitTests[JUnit Tests]
        UnitTests --> Build[Maven Build (JAR)]
    end
    
    subgraph "Security Gates (DevSecOps)"
        Build --> SAST[SpotBugs SAST]
        SAST --> SCA[Trivy FS Scan]
    end
    
    subgraph "Containerization & Delivery"
        SCA --> DockerBuild[Docker Build]
        DockerBuild --> ImageScan[Trivy Image Scan]
        ImageScan --> Push[Push to DockerHub]
    end
    
    subgraph "CD (Deployment)"
        Push --> K8s[Local Kubernetes Cluster]
        K8s --> Pods[Running Pods]
    end
```

---

## 4. CI/CD Pipeline Design & Stages

The pipeline is implemented using **GitHub Actions** (`.github/workflows/ci.yml`). It is triggered on every push to the `main` branch.

### Stage 1: Checkout & Setup
*   **Purpose**: Prepares a clean build environment.
*   **Action**: `actions/checkout` fetches the latest code, and `actions/setup-java` installs JDK 17 with Maven caching to speed up subsequent builds.

### Stage 2: Quality Gates (Linting & Testing)
*   **Linting**: Uses **Maven Checkstyle Plugin**.
    *   *Why*: Enforces coding standards (naming conventions, formatting) to prevent technical debt.
*   **Unit Tests**: Uses **JUnit 5**.
    *   *Why*: Validates business logic (`isPrime`, `factorial`) to catch regression bugs immediately.

### Stage 3: Build & Package
*   **Maven Build**: Compiles the source code and packages it into an executable JAR file (`mvn package`).

### Stage 4: DevSecOps (Security Scanning)
*   **SAST (Static Application Security Testing)**: Uses **SpotBugs**.
    *   *Why*: Scans source code for potential bugs and security flaws (e.g., null pointer dereferences, infinite loops) without executing the code.
*   **SCA (Software Composition Analysis)**: Uses **Aquasecurity Trivy**.
    *   *Why*: Scans the file system for dependencies with known CVEs (Common Vulnerabilities and Exposures), preventing supply-chain attacks.

### Stage 5: Containerization
*   **Docker Build**: Uses a **Multi-Stage Dockerfile**.
    *   *Stage 1*: Build the JAR using `maven` image.
    *   *Stage 2*: Run the JAR using a lightweight `openjdk-slim` image.
    *   *Why*: Removes build tools from the final image, reducing size and attack surface.

### Stage 6: Image Security & Release
*   **Image Scan**: Uses **Trivy**.
    *   *Why*: Scans the final OS layer (Debian/Alpine) for system vulnerabilities before shipping.
*   **Registry Push**: Pushes the verified image to **DockerHub**.
    *   *Why*: Makes the immutable artifact available for deployment.

---

## 5. Security & Quality Controls

| Control | Tool Used | Purpose |
| :--- | :--- | :--- |
| **Code Style** | Checkstyle | Enforces consistent code formatting and rules. |
| **Logic Verification** | JUnit 5 | Ensures the code behaves as expected (Pass rate: 100%). |
| **Static Analysis (SAST)** | SpotBugs (Java) | Detects bugs & security flaws (Equivalent to CodeQL). |
| **Dependency Security** | Trivy (FS) | Identifies vulnerable libraries (e.g., Log4Shell). |
| **Container Security** | Trivy (Image) | Identifies OS-level vulnerabilities in the Docker image. |
| **Secrets Management** | GitHub Secrets | Protects DockerHub credentials (`DOCKERHUB_TOKEN`) from leaking. |

---

## 6. Results & Observations

### Automated Workflow
The pipeline successfully runs end-to-end in approximately **2-3 minutes**.
*   **Build**: Successfully compiles Java 17 code.
*   **Tests**: 6/6 Tests Passed (Prime and Factorial logic verified).
*   **Security**: Scans verified no Critical vulnerabilities in the current dependencies.
*   **Artifact**: Docker Image `vinayreddy18/java-cicd-project:latest` published to DockerHub.

### Kubernetes Deployment
The application was successfully deployed to a local Kubernetes cluster.
*   **Deployment**: 1 Replica running.
*   **Service**: LoadBalancer/NodePort exposing port 8080.
*   **Verification**: `curl http://localhost/api/math/prime/17` returns `true`.

---

## 6.1 Deployment Strategy Justification (Viva Defense)

**Why Local Kubernetes instead of AWS/GCP?**
For this project, we chose a **Hybrid Cloud Architecture**:
1.  **CI (Cloud)**: GitHub Actions runs in the cloud (Microsoft Azure infrastructure).
2.  **CD (Local)**: The deployment target is a local Kubernetes cluster (Docker Desktop) accessed via a Self-Hosted Runner.

**Reasoning**:
*   **Cost Efficiency**: Cloud clusters (EKS/GKE) cost ~$70-100/month. Local deployment demonstrates the exact same `kubectl` principles without incurring cloud costs.
*   **Security**: By keeping the runtime environment local, we avoid exposing a student project to the public internet, reducing the attack surface.
*   **Self-Hosted Runner**: We implemented a specific DevOps pattern (Self-Hosted Agent) to bridge the Cloud-to-Local network gap, which is a common enterprise pattern for accessing private on-premise clusters.

---

## 7. Limitations & Improvements

### Limitations
1.  **Local CD**: The deployment phase is currently manual/local (`kubectl apply`). In a real production environment, this should be automated using a CD tool.
2.  **Basic Rule Sets**: Checkstyle and SpotBugs are configured with standard rules. Custom enterprise rules could be added.

### Future Improvements
1.  **GitOps (ArgoCD)**: Implement ArgoCD to watch the Git repository and automatically sync changes to the Kubernetes cluster, replacing the manual `kubectl apply` step.
2.  **Code Coverage**: Integrate JaCoCo to enforce a minimum test coverage percentage (e.g., 80%) to block PRs.
3.  **Dynamic Environments**: Spin up ephemeral environments for every Pull Request to allow manual QA before merging.

---
