# GitHub Actions vs Jenkins – Maven Demo Project

A **simple, easy-to-understand** Java/Maven project that demonstrates how to set up and run the same CI pipeline using two popular CI/CD tools: **GitHub Actions** and **Jenkins**.

---

## 📁 Project Structure

```
GitHubActions-JenkinsDemoProject/
│
├── .github/
│   └── workflows/
│       └── ci.yml              ← GitHub Actions pipeline
│
├── src/
│   ├── main/java/com/demo/
│   │   ├── Calculator.java     ← Simple arithmetic operations
│   │   └── StringUtils.java    ← String helper methods
│   └── test/java/com/demo/
│       ├── CalculatorTest.java ← JUnit 5 tests for Calculator
│       └── StringUtilsTest.java← JUnit 5 tests for StringUtils
│
├── Jenkinsfile                 ← Jenkins declarative pipeline
├── pom.xml                     ← Maven build definition
└── README.md
```

---

## 🔧 What the Project Does

The project contains two small utility classes and their JUnit 5 tests:

| Class | Operations |
|-------|-----------|
| `Calculator` | `add`, `subtract`, `multiply`, `divide`, `factorial` |
| `StringUtils` | `reverse`, `isPalindrome`, `countWords`, `toTitleCase` |

Both CI pipelines run the same three build steps:

1. **Compile** – `mvn compile`
2. **Test** – `mvn test` (runs all JUnit 5 tests)
3. **Package** – `mvn package` (produces a JAR in `target/`)

---

## ⚡ GitHub Actions vs Jenkins – Key Differences

| Feature | GitHub Actions | Jenkins |
|---------|---------------|---------|
| **Hosting** | Fully managed by GitHub – no server to install or maintain | Requires a dedicated server (on-prem, VM, or container) that you install and manage |
| **Configuration** | YAML file (`.github/workflows/ci.yml`) stored in the repo | `Jenkinsfile` (Groovy DSL) stored in the repo; Jenkins server must also be configured |
| **Setup effort** | Near-zero – create the YAML file and it just works | Moderate – install Jenkins, configure tools (JDK, Maven), set up credentials and webhooks |
| **Cost** | Free for public repos; limited free minutes for private repos | Free open-source software, but you pay for the server/infrastructure |
| **Triggers** | Triggered automatically by GitHub events (push, PR, schedule, etc.) | Triggered by webhooks, polling, or manual builds |
| **Runners / Agents** | GitHub-hosted runners (Linux, macOS, Windows) or self-hosted | Jenkins agents (nodes) that you provision and maintain |
| **Marketplace** | 10 000+ community Actions in the GitHub Marketplace | 1 800+ plugins in the Jenkins plugin ecosystem |
| **Scalability** | Scales automatically – GitHub spins up fresh runners per job | You manage agent capacity; additional agents must be provisioned manually |
| **Pipeline language** | YAML | Groovy (Declarative or Scripted DSL) |
| **Artefact storage** | Built-in artefact upload/download steps | `archiveArtifacts` step; external storage (Nexus, Artifactory) recommended for teams |
| **Best suited for** | Projects already on GitHub that want zero-infrastructure CI | Complex enterprise pipelines, multi-technology builds, or teams needing full customisation |

---

## 🚀 Running the Project Locally

**Prerequisites:** Java 11+ and Maven 3.6+ must be installed.

```bash
# Clone the repository
git clone https://github.com/rasmikanta19/GitHubActions-JenkinsDemoProject.git
cd GitHubActions-JenkinsDemoProject

# Compile
mvn compile

# Run all tests
mvn test

# Package (produces target/github-actions-jenkins-demo-1.0.0.jar)
mvn package
```

---

## ⚙️ GitHub Actions Setup

GitHub Actions requires **no server setup**. The pipeline is defined in [`.github/workflows/ci.yml`](.github/workflows/ci.yml) and runs automatically on every push and pull request.

### How it works

1. A developer pushes code (or opens a PR) to GitHub.
2. GitHub detects the push event and reads `.github/workflows/ci.yml`.
3. GitHub provisions a fresh `ubuntu-latest` runner automatically.
4. The runner executes the steps: **Checkout → Compile → Test → Package**.
5. The built JAR is uploaded as a workflow artefact and available on the run summary page.

### Pipeline file highlights

```yaml
on:
  push:
    branches: ["**"]       # trigger on every branch push
  pull_request:
    branches: [main]       # trigger on PRs targeting main

jobs:
  build-and-test:
    runs-on: ubuntu-latest  # GitHub-hosted runner – no server needed!
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: "11"
          distribution: "temurin"
          cache: maven       # caches ~/.m2 between runs
      - run: mvn compile
      - run: mvn test
      - run: mvn package -DskipTests
      - uses: actions/upload-artifact@v4
        with:
          name: app-jar
          path: target/*.jar
```

---

## 🏗️ Jenkins Setup

Jenkins requires a running server. Follow the steps below to set it up locally with Docker (the quickest path).

### Step 1 – Start Jenkins with Docker

```bash
docker run -d \
  --name jenkins \
  -p 8080:8080 \
  -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  jenkins/jenkins:lts-jdk11
```

Open `http://localhost:8080` in your browser and complete the setup wizard.

### Step 2 – Install required plugins

Go to **Manage Jenkins → Plugins → Available** and install:
- **Pipeline** (if not already installed)
- **Git**
- **Maven Integration**

### Step 3 – Configure tools

Go to **Manage Jenkins → Tools** and add:
- **JDK** → Name: `JDK-11` → Install automatically (OpenJDK 11)
- **Maven** → Name: `Maven-3` → Install automatically (Maven 3.9.x)

> The names `JDK-11` and `Maven-3` must match exactly what is written in the `Jenkinsfile`.

### Step 4 – Create a Pipeline job

1. Click **New Item**, enter a name (e.g. `demo-pipeline`), and select **Pipeline**.
2. Under **Pipeline → Definition**, choose **Pipeline script from SCM**.
3. Select **Git** and enter the repository URL.
4. Set **Script Path** to `Jenkinsfile`.
5. Click **Save** → **Build Now**.

### Pipeline file highlights

```groovy
pipeline {
    agent any
    tools {
        maven 'Maven-3'   // name defined in Jenkins → Tools
        jdk   'JDK-11'
    }
    stages {
        stage('Checkout') { steps { checkout scm } }
        stage('Compile')  { steps { sh 'mvn compile' } }
        stage('Test') {
            steps { sh 'mvn test' }
            post  { always { junit '**/target/surefire-reports/*.xml' } }
        }
        stage('Package') {
            steps { sh 'mvn package -DskipTests' }
            post  { success { archiveArtifacts artifacts: 'target/*.jar' } }
        }
    }
    post {
        success { echo '✅ Build succeeded!' }
        failure { echo '❌ Build failed.' }
        always  { cleanWs() }
    }
}
```

---

## 📊 Side-by-Side Pipeline Comparison

```
GitHub Actions (.github/workflows/ci.yml)     Jenkins (Jenkinsfile)
─────────────────────────────────────────     ──────────────────────────────
on: push / pull_request                       Triggered by webhook / manual
runs-on: ubuntu-latest (GitHub cloud)         agent any (your Jenkins server)
uses: actions/checkout@v4                     checkout scm
uses: actions/setup-java@v4                   tools { jdk 'JDK-11' }
run: mvn compile                              sh 'mvn compile'
run: mvn test                                 sh 'mvn test'
uses: actions/upload-artifact@v4              archiveArtifacts 'target/*.jar'
```

Both pipelines produce the same outcome – a tested, packaged JAR – but differ in **where** they run and **how much infrastructure** you manage.

---

## 📝 Summary

- **Choose GitHub Actions** when your code is on GitHub and you want the simplest, zero-infrastructure CI setup.
- **Choose Jenkins** when you need deep customisation, complex enterprise integrations, or want to run CI on your own infrastructure.
