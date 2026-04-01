# GitHub Actions vs Jenkins + SonarCloud – Complete User Guide

A **comprehensive, learn-by-doing** Java/Maven project that demonstrates:
- ✅ CI/CD with **GitHub Actions**
- ✅ CI/CD with **Jenkins**
- ✅ Code quality analysis with **SonarCloud** (hosted SonarQube)
- ✅ Code coverage measurement with **JaCoCo**
- ✅ Intentional bugs and security issues to teach what SonarQube catches

---

## 📋 Table of Contents

1. [Project Structure](#-project-structure)
2. [Prerequisites](#-prerequisites)
3. [Running the Project Locally](#-running-the-project-locally)
4. [Understanding the Source Files](#-understanding-the-source-files)
5. [GitHub Actions – Complete Guide](#-github-actions--complete-guide)
6. [Jenkins – Complete Guide](#-jenkins--complete-guide)
7. [SonarCloud – Complete Guide](#-sonarcloud--complete-guide)
8. [JaCoCo Code Coverage – Complete Guide](#-jacoco-code-coverage--complete-guide)
9. [GitHub Actions vs Jenkins – Key Differences](#-github-actions-vs-jenkins--key-differences)
10. [Troubleshooting](#-troubleshooting)

---

## 📁 Project Structure

```
GitHubActions-JenkinsDemoProject/
│
├── .github/
│   └── workflows/
│       └── ci.yml                  ← GitHub Actions pipeline (2 jobs)
│
├── src/
│   ├── main/java/com/demo/
│   │   ├── Calculator.java         ← Arithmetic operations (clean code)
│   │   ├── StringUtils.java        ← String helpers (clean code)
│   │   ├── BankAccount.java        ← ⚠️ Has intentional Bugs + Code Smells
│   │   └── UserValidator.java      ← ⚠️ Has intentional Security + Bugs
│   │
│   └── test/java/com/demo/
│       ├── CalculatorTest.java     ← 14 unit tests (full coverage)
│       ├── StringUtilsTest.java    ← 19 unit tests (full coverage)
│       ├── BankAccountTest.java    ← 10 unit tests (intentional coverage gap)
│       └── UserValidatorTest.java  ← 15 unit tests (intentional coverage gap)
│
├── Jenkinsfile                     ← Jenkins declarative pipeline
├── pom.xml                         ← Maven build + JaCoCo + SonarCloud config
└── README.md                       ← This guide
```

---

## 🔧 Prerequisites

| Tool | Version | Why Needed |
|------|---------|-----------|
| **Java (JDK)** | 11 or higher | Compile and run the project |
| **Maven** | 3.6 or higher | Build tool (compile, test, package) |
| **Git** | Any | Clone the repository |
| **Docker** | Any | Run Jenkins locally (optional) |
| **GitHub Account** | — | GitHub Actions + SonarCloud login |

**Check your versions:**
```bash
java -version
mvn -version
git --version
docker --version
```

---

## 🚀 Running the Project Locally

```bash
# 1. Clone the repository
git clone https://github.com/rasmikanta19/GitHubActions-JenkinsDemoProject.git
cd GitHubActions-JenkinsDemoProject

# 2. Compile the source code
mvn compile

# 3. Run all 58 unit tests
mvn test

# 4. Run tests + generate JaCoCo coverage report
mvn verify

# 5. Package into a JAR (skips tests for speed)
mvn package -DskipTests
```

After `mvn verify`, open the coverage report in your browser:
```
target/site/jacoco/index.html
```

---

## 📖 Understanding the Source Files

### Clean Code Files (no intentional issues)

#### `Calculator.java`
Provides five arithmetic operations. Well-structured, all paths tested.

| Method | Description | Example |
|--------|-------------|---------|
| `add(a, b)` | Returns `a + b` | `add(3, 4)` → `7` |
| `subtract(a, b)` | Returns `a - b` | `subtract(7, 3)` → `4` |
| `multiply(a, b)` | Returns `a × b` | `multiply(3, 4)` → `12` |
| `divide(a, b)` | Returns `a ÷ b`, throws if `b=0` | `divide(10, 2)` → `5.0` |
| `factorial(n)` | Returns `n!`, throws if `n<0` | `factorial(5)` → `120` |

#### `StringUtils.java`
Provides four string helper methods.

| Method | Description | Example |
|--------|-------------|---------|
| `reverse(s)` | Reverses the string | `reverse("hello")` → `"olleh"` |
| `isPalindrome(s)` | Checks if palindrome (case-insensitive) | `isPalindrome("racecar")` → `true` |
| `countWords(s)` | Counts space-separated words | `countWords("hi there")` → `2` |
| `toTitleCase(s)` | Capitalises each word | `toTitleCase("hello world")` → `"Hello World"` |

---

### SonarQube Teaching Files (intentional issues)

These files are designed to teach you **what SonarQube detects**.

#### `BankAccount.java` – What SonarQube will flag

```
┌─────────────────┬──────────────────────────────────────────────────┐
│ Issue Type      │ Description                                      │
├─────────────────┼──────────────────────────────────────────────────┤
│ 🐛 Bug          │ withdraw() has no "insufficient funds" check     │
│                 │ → balance silently goes negative                 │
├─────────────────┼──────────────────────────────────────────────────┤
│ 🐛 Bug          │ Constructor has no null-check on `owner`         │
│                 │ → NullPointerException risk downstream           │
├─────────────────┼──────────────────────────────────────────────────┤
│ 😷 Code Smell   │ Magic number `0` in getAccountSummary()          │
│                 │ → should be a named constant                     │
├─────────────────┼──────────────────────────────────────────────────┤
│ 📊 Coverage Gap │ getAccountSummary() has 3 branches:              │
│                 │   balance > 0  → ✅ tested                       │
│                 │   balance == 0 → ❌ NOT tested (RED in Sonar)    │
│                 │   balance < 0  → ❌ NOT tested (RED in Sonar)    │
└─────────────────┴──────────────────────────────────────────────────┘
```

**The withdraw() bug shown in code:**
```java
public void withdraw(double amount) {
    if (amount <= 0) {
        throw new IllegalArgumentException("Withdrawal amount must be positive.");
    }
    // ⚠️ BUG: No check that amount <= balance
    // SonarQube flags this line as a Bug
    balance -= amount;   // balance can become -500, -1000, etc.
}

// THE FIX:
// if (amount > balance) {
//     throw new IllegalArgumentException("Insufficient funds.");
// }
```

---

#### `UserValidator.java` – What SonarQube will flag

```
┌─────────────────┬──────────────────────────────────────────────────┐
│ Issue Type      │ Description                                      │
├─────────────────┼──────────────────────────────────────────────────┤
│ 🔒 Security     │ Hardcoded password "admin123" in source code     │
│    Hotspot      │ → OWASP Top-10 A07: Identification & Auth Fail   │
├─────────────────┼──────────────────────────────────────────────────┤
│ 🐛 Bug          │ isAdminPassword() uses == instead of .equals()   │
│                 │ → fails for runtime-constructed String objects   │
├─────────────────┼──────────────────────────────────────────────────┤
│ 🐛 Bug          │ isValidEmail() has no null-check on `email`      │
│                 │ → NullPointerException when email is null        │
├─────────────────┼──────────────────────────────────────────────────┤
│ 📊 Coverage Gap │ maskPassword() short-string branch (length ≤ 2)  │
│                 │ → ❌ NOT tested (RED line in SonarCloud)          │
└─────────────────┴──────────────────────────────────────────────────┘
```

**The == vs .equals() bug explained:**
```java
// ⚠️ BUG in UserValidator.java
public boolean isAdminPassword(String password) {
    return password == ADMIN_PASSWORD;  // compares REFERENCES, not values!
}

// WHY THIS IS SNEAKY:
String a = "admin123";             // string literal → interned by JVM
String b = new String("admin123"); // new object → different memory address

isAdminPassword(a);  // returns TRUE  (literals share same reference)
isAdminPassword(b);  // returns FALSE (different object, same value!) ← BUG

// THE FIX:
// return ADMIN_PASSWORD.equals(password);
```

---

## ⚙️ GitHub Actions – Complete Guide

### What is GitHub Actions?

GitHub Actions is a **CI/CD platform built directly into GitHub**. You define your pipeline as a YAML file stored in your repository. GitHub automatically runs it on its own cloud servers (called **runners**) whenever you push code or open a pull request.

```
Developer pushes code
        │
        ▼
GitHub detects the push event
        │
        ▼
GitHub reads .github/workflows/ci.yml
        │
        ▼
GitHub provisions a fresh ubuntu-latest VM (runner)
        │
        ▼
Runner executes your steps in order
        │
        ▼
Results appear in the GitHub Actions tab
```

### Key Concepts

| Concept | Description |
|---------|-------------|
| **Workflow** | The entire automation file (`.yml`) |
| **Trigger (`on:`)** | Event that starts the workflow (push, PR, schedule) |
| **Job** | A group of steps that run on the same runner |
| **Step** | A single task — either a shell command (`run:`) or an Action (`uses:`) |
| **Action** | A reusable community-built step (e.g. `actions/checkout@v4`) |
| **Runner** | The virtual machine that executes your job |
| **Artifact** | A file saved from the runner for download after the run |
| **Secret** | An encrypted value (password, token) stored in GitHub Settings |
| **`needs:`** | Declares a dependency between jobs (Job B waits for Job A) |

### Our Pipeline — Two Jobs

```
Push to GitHub
      │
      ▼
┌─────────────────────────────────────┐
│  JOB 1: 🔨 Build & Test             │
│                                     │
│  Step 1: Checkout source code       │
│  Step 2: Setup Java 11 (Temurin)    │
│  Step 3: mvn compile                │
│  Step 4: mvn test  (58 tests)       │
│  Step 5: mvn package                │
│  Step 6: Upload JAR artifact        │
└──────────────┬──────────────────────┘
               │ needs: (waits for Job 1 to pass ✅)
               ▼
┌─────────────────────────────────────┐
│  JOB 2: 📊 SonarCloud Analysis      │
│                                     │
│  Step 1: Checkout (full history)    │
│  Step 2: Setup Java 11              │
│  Step 3: Cache SonarCloud packages  │
│  Step 4: mvn compile                │
│  Step 5: mvn verify sonar:sonar     │
│  Step 6: Upload coverage report     │
└─────────────────────────────────────┘
```

### Why Two Jobs?

| Reason | Explanation |
|--------|-------------|
| **Fail fast** | Job 1 catches compile/test errors quickly (< 1 min). Job 2 only runs when the code is healthy. |
| **Separation of concerns** | Build = fast feedback. Analysis = deep inspection (takes longer). |
| **`needs:` dependency** | `sonarcloud-analysis` declares `needs: build-and-test`, so it only starts after Job 1 passes. |

### YAML Syntax Reference

```yaml
name: Workflow Display Name         # shown in GitHub Actions UI

on:                                 # TRIGGERS: when does this run?
  push:
    branches: ["**"]                # on every push to any branch
  pull_request:
    branches: [main]                # on every PR targeting main

permissions:                        # principle of least privilege
  contents: read                    # read source code only
  pull-requests: write              # post comments on PRs

jobs:
  my-job-id:                        # must be unique in this file
    name: "Human-readable Name"     # shown in GitHub UI
    runs-on: ubuntu-latest          # runner operating system
    needs: another-job-id           # wait for another-job-id first

    steps:
      - name: Step Label
        uses: actions/checkout@v4   # use a pre-built Action

      - name: Shell Command Step
        run: echo "hello world"     # run shell command

      - name: Action With Inputs
        uses: actions/setup-java@v4
        with:                       # inputs passed to the Action
          java-version: "11"
          distribution: "temurin"
          cache: maven

      - name: Step Using a Secret
        env:
          MY_TOKEN: ${{ secrets.MY_SECRET_NAME }}
        run: echo "Token is set: $MY_TOKEN"

      - name: Conditional Step
        if: always()                # run even if previous steps failed
        uses: actions/upload-artifact@v4
        with:
          name: my-artifact
          path: target/*.jar
          retention-days: 7
```

### Triggers Explained

```yaml
on:
  push:                             # run when code is pushed
    branches: ["**"]               # ** = match every branch
    paths-ignore:                  # skip if ONLY these paths changed
      - "**.md"

  pull_request:                    # run when a PR is opened/updated
    branches: [main]               # only PRs targeting main

  schedule:                        # run on a cron schedule (UTC)
    - cron: "0 2 * * *"           # every day at 02:00 UTC

  workflow_dispatch:               # manual trigger from GitHub UI
    inputs:
      environment:
        description: "Target env"
        required: true
        default: "staging"
```

### Viewing Results

1. Go to your GitHub repository
2. Click the **Actions** tab
3. Click a workflow run to see all jobs
4. Click a job to expand its steps
5. Click a step to see its full console output
6. Click **Summary** at the top to download uploaded artifacts (JAR, coverage report)

---

## 🏗️ Jenkins – Complete Guide

### What is Jenkins?

Jenkins is an **open-source CI/CD automation server** that you install and manage yourself. You write your pipeline as a `Jenkinsfile` (Groovy DSL) stored in your repository. Jenkins receives webhooks from GitHub and runs the pipeline on its own managed agents.

```
Developer pushes code
        │
        ▼
GitHub sends a webhook to Jenkins server
        │
        ▼
Jenkins reads Jenkinsfile from the repository
        │
        ▼
Jenkins executes pipeline stages on its agent
        │
        ▼
Results visible at http://localhost:8080
```

### Setup Guide (Docker – Quickest Path)

#### Step 1 – Start Jenkins

```bash
docker run -d \
  --name jenkins \
  -p 8080:8080 \
  -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  jenkins/jenkins:lts-jdk11
```

Open `http://localhost:8080` in your browser.

#### Step 2 – Unlock Jenkins

Get the initial admin password:
```bash
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```
Paste it into the web UI → click **Continue**.

#### Step 3 – Install Suggested Plugins

Click **"Install suggested plugins"** and wait for completion (~2 minutes).

#### Step 4 – Create Admin User

Fill in your desired **username** and **password** → click **Save and Continue**.

> 💡 **Your Jenkins credentials are the username and password you set in this step.**
> Jenkins has no default password — you define it during the first-time setup wizard.

#### Step 5 – Install Additional Plugins

Go to **Manage Jenkins → Plugins → Available plugins** and install:
- **Pipeline** (may already be installed)
- **Git**
- **Maven Integration**

#### Step 6 – Configure Tools

Go to **Manage Jenkins → Tools** and add:

| Tool | Name (must be EXACT) | Install Automatically |
|------|---------------------|----------------------|
| **JDK** | `JDK-11` | ✅ OpenJDK 11 |
| **Maven** | `Maven-3` | ✅ Maven 3.9.x |

> ⚠️ The names `JDK-11` and `Maven-3` must match **character-for-character** what is written in the `Jenkinsfile`.

#### Step 7 – Create a Pipeline Job

1. Click **New Item**
2. Enter name: `pipeline-demo` → select **Pipeline** → click **OK**
3. Under **Pipeline → Definition** → choose **Pipeline script from SCM**
4. SCM → **Git**
5. Repository URL → `https://github.com/rasmikanta19/GitHubActions-JenkinsDemoProject`
6. Credentials → **`- none -`** *(this is a public repo — no credentials needed)*
7. Branch Specifier → change `*/master` to **`*/main`**
8. Script Path → `Jenkinsfile`
9. Click **Save** → **Build Now**

### Jenkinsfile Explained Line by Line

```groovy
pipeline {
    // 'agent any' means: run on whichever Jenkins agent is free.
    // For multi-agent setups you can specify: agent { label 'linux' }
    agent any

    // Optional UI parameters — lets you customise the build from Jenkins UI
    parameters {
        string(name: 'BRANCH_NAME', defaultValue: 'main',
               description: 'Branch to build')
    }

    // Tool names MUST match Manage Jenkins → Tools configuration exactly
    tools {
        maven 'Maven-3'   // installs Maven 3.9.x on the agent
        jdk   'JDK-11'    // installs OpenJDK 11 on the agent
    }

    // Environment variables available to ALL stages
    environment {
        APP_NAME = 'github-actions-jenkins-demo'
        VERSION  = '1.0.0'
    }

    stages {

        stage('Checkout') {
            steps {
                // Jenkins clones the repo configured in the job SCM settings
                checkout scm
            }
        }

        stage('Compile') {
            steps {
                // sh = run a shell command on Linux/macOS agents
                // bat = run a batch command on Windows agents
                sh 'mvn compile'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                // post.always runs regardless of test pass/fail
                always {
                    // Parses JUnit XML reports and shows results in Jenkins UI
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
            post {
                // post.success only runs if THIS stage passed
                success {
                    // Save the JAR to Jenkins so it can be downloaded
                    archiveArtifacts artifacts: 'target/*.jar',
                                     fingerprint: true
                }
            }
        }

    } // end stages

    post {
        // These run AFTER all stages complete
        success { echo "✅ Build ${env.BUILD_NUMBER} succeeded!" }
        failure { echo "❌ Build ${env.BUILD_NUMBER} failed." }
        always  { cleanWs() }  // delete workspace to free disk space
    }
}
```

### Common Jenkins Error Fix

```
Error: fatal: couldn't find remote ref refs/heads/master

Cause:  Jenkins defaults to the 'master' branch.
        Your repo uses 'main' (modern GitHub default).

Fix:    Job Configuration → Branches to build
        Change:  */master
        To:      */main
```

---

## 📊 SonarCloud – Complete Guide

### What is SonarQube / SonarCloud?

SonarQube is a **static code analysis platform** that inspects your source code (without running it) and reports:

```
Your .java files
      │
      ▼
SonarQube Scanner (reads every line of code)
      │
      ├── 🐛 Bugs            → code that will crash or behave incorrectly
      ├── 🔒 Vulnerabilities → security weaknesses (OWASP Top-10)
      ├── 🔑 Security Hotspots → code needing human security review
      ├── 😷 Code Smells     → maintainability / readability issues
      ├── 🔁 Duplications    → copy-pasted code blocks
      └── 📊 Coverage        → which lines have NO test coverage
      │
      ▼
Dashboard on sonarcloud.io (or your own SonarQube server)
```

**SonarCloud** = SonarQube hosted in the cloud (free for public repos, zero server setup).  
**SonarQube** = self-hosted version (for private/enterprise use).

### Issue Severity Levels

```
Severity   Symbol  Example
─────────  ──────  ──────────────────────────────────────────────
Blocker    🔴      SQL injection, null dereference that WILL crash
Critical   🟠      String == comparison bug, missing null guard
Major      🟡      Method too complex, magic numbers
Minor      🔵      Missing Javadoc, redundant cast
Info       ⚪      Style suggestions
```

### Quality Gate – Pass or Fail?

The Quality Gate is a **configurable pass/fail policy**. If the code fails it, the PR check turns red and blocks merging.

```
Quality Gate: ✅ PASSED                Quality Gate: ❌ FAILED
───────────────────────────────        ───────────────────────────────
Bugs:            0   Rating: A         Bugs:            3   Rating: D
Vulnerabilities: 0   Rating: A         Vulnerabilities: 1   Rating: E
Code Smells:     1   Rating: A         Coverage:        45% (< 80% threshold)
Coverage:        86% (≥ 80%)
Duplications:    0.8%
```

### SonarCloud Setup (Step-by-Step)

#### Step 1 – Create Account

1. Go to **[sonarcloud.io](https://sonarcloud.io)**
2. Click **"Log in with GitHub"** → authorise the app
3. Click **"Import an organization"**
4. Select your GitHub account: `rasmikanta19`
5. Choose **Free plan** → click **"Create Organization"**

#### Step 2 – Create a Project

1. On the SonarCloud dashboard, click **"Analyze new project"**
2. Select `GitHubActions-JenkinsDemoProject` from the list
3. Click **"Set Up"**
4. Choose **"With GitHub Actions"** as the analysis method
5. Note down:
   - **Organization Key:** `rasmikanta19`
   - **Project Key:** `rasmikanta19_GitHubActions-JenkinsDemoProject`

#### Step 3 – Generate a Token

1. Click your **avatar** (top-right) → **My Account**
2. Click the **Security** tab
3. Under **"Generate Tokens"** → Name: `SONAR_TOKEN` → click **Generate**
4. **Copy the token now** — it is shown only once!

#### Step 4 – Add the Token to GitHub Secrets

1. Go to your GitHub repository
2. **Settings → Secrets and variables → Actions**
3. Click **"New repository secret"**
4. **Name:** `SONAR_TOKEN`
5. **Value:** *(paste the token from Step 3)*
6. Click **"Add secret"**

#### Step 5 – Push Code and View Results

Push any code change to trigger the pipeline. Once the `sonarcloud-analysis` job completes, open:
```
https://sonarcloud.io/project/overview?id=rasmikanta19_GitHubActions-JenkinsDemoProject
```

### Reading the SonarCloud Dashboard

```
┌──────────────────────────────────────────────────────────────────┐
│  Quality Gate  ✅ PASSED  (or ❌ FAILED)                          │
├──────────────┬──────────────────┬───────────────┬────────────────┤
│   Bugs       │  Vulnerabilities │  Code Smells  │   Coverage     │
│   3  (D)     │    1  (E)        │    1  (A)     │    85.3%       │
├──────────────┴──────────────────┴───────────────┴────────────────┤
│  Duplications: 0.0%          Security Hotspots: 1                │
└──────────────────────────────────────────────────────────────────┘
```

**Navigating issues in the dashboard:**

| Click | What you see |
|-------|-------------|
| **Bugs (3)** | List of all bugs with file name and line number |
| A specific bug | Exact line highlighted + explanation + recommended fix |
| **Coverage %** | Per-file breakdown; click a file to see GREEN/RED lines |
| **Security Hotspots** | Flagged code needing a human security review |
| **Issues tab** | Filter by type, severity, file, or assignee |

### What SonarCloud Finds in This Project

#### `BankAccount.java`

| Line / Method | Issue | SonarCloud Message |
|---|---|---|
| `withdraw()` — `balance -= amount` | 🐛 Bug | No guard before decrement — balance can go negative |
| Constructor — `this.owner = owner` | 🐛 Bug | `owner` used without null check |
| `getAccountSummary()` — literal `0` | 😷 Code Smell | Replace magic number with a named constant |
| `balance == 0` branch | 📊 Coverage | Line not covered (shown RED) |
| `balance < 0` branch | 📊 Coverage | Line not covered (shown RED) |

#### `UserValidator.java`

| Line / Method | Issue | SonarCloud Message |
|---|---|---|
| `ADMIN_PASSWORD = "admin123"` | 🔒 Security Hotspot | Hard-coded credential — is this intentional? |
| `password == ADMIN_PASSWORD` | 🐛 Bug | Use `.equals()` if value comparison was intended |
| `email.contains("@")` | 🐛 Bug | Potential NullPointerException — `email` not null-checked |
| `return "**"` in `maskPassword` | 📊 Coverage | Line not covered (shown RED) |

### How SonarCloud Fits in the CI Pipeline

```
ci.yml — Job 2 (sonarcloud-analysis)

Step 1: Checkout with fetch-depth: 0
         ↑ Full history needed so Sonar knows WHO introduced each bug
         ↑ and can accurately compute "new code" since last analysis

Step 2: Setup Java 11

Step 3: Cache ~/.sonar/cache
         ↑ SonarCloud downloads ~100 MB of analysis rules on first run
         ↑ Caching saves ~60 seconds on every subsequent pipeline run

Step 4: mvn compile

Step 5: mvn verify sonar:sonar
         ↑ verify    = run tests + JaCoCo generates jacoco.xml
         ↑ sonar:sonar = scanner reads source + jacoco.xml, uploads to sonarcloud.io
         ↑ Combined into ONE command so jacoco.exec is not lost between steps

Step 6: Upload target/site/jacoco/ as artifact
         ↑ So you can download and browse the HTML coverage report from GitHub
```

---

## 📏 JaCoCo Code Coverage – Complete Guide

### What is JaCoCo?

JaCoCo (**Ja**va **Co**de **Co**verage) silently instruments your compiled bytecode. When your tests run, it records which lines were executed and generates a coverage report.

```
mvn verify (internal phases)
    │
    ├── Phase: initialize
    │     JaCoCo attaches a Java agent to the JVM
    │     (like a recording device watching code execution)
    │
    ├── Phase: test
    │     JUnit 5 runs all tests
    │     JaCoCo agent records every line that is executed
    │     Writes binary data to: target/jacoco.exec
    │
    └── Phase: verify
          JaCoCo reads jacoco.exec and generates two reports:
            target/site/jacoco/jacoco.xml   ← read by SonarCloud
            target/site/jacoco/index.html   ← human-readable browser view
```

### Reading the HTML Coverage Report

Open `target/site/jacoco/index.html` after running `mvn verify`:

```
Package: com.demo
─────────────────────────────────────────────────────────────────
Class              Missed Lines  Coverage %   Missed Branches
─────────────────────────────────────────────────────────────────
Calculator              0          100% ✅         0 / 100% ✅
StringUtils             0          100% ✅         0 / 100% ✅
BankAccount             2           89% ⚠️         2 / 75%  ⚠️
UserValidator           1           94% ⚠️         1 / 88%  ⚠️
─────────────────────────────────────────────────────────────────
Total                   3           95%             3 / 90%
```

**Line colours in the report:**

| Colour | Meaning |
|--------|---------|
| 🟢 Green | Line was executed at least once during tests |
| 🔴 Red | Line was NEVER executed — blind spot in your test suite |
| 🟡 Yellow | Branch partially covered (e.g. `if` true-path tested but false-path not) |

### How JaCoCo is Configured (`pom.xml`)

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>

        <!-- EXECUTION 1: initialize phase
             Attaches the JaCoCo Java agent to the JVM.
             Without this, no coverage data is ever collected.      -->
        <execution>
            <id>prepare-agent</id>
            <goals><goal>prepare-agent</goal></goals>
        </execution>

        <!-- EXECUTION 2: verify phase (runs AFTER test phase)
             Reads the binary jacoco.exec file and outputs:
               jacoco.xml   → SonarCloud reads this for coverage %
               index.html   → you open this in a browser            -->
        <execution>
            <id>report</id>
            <phase>verify</phase>
            <goals><goal>report</goal></goals>
        </execution>

    </executions>
</plugin>
```

### Coverage Gaps in This Project (intentional for learning)

```java
// BankAccount.getAccountSummary()
if (balance > 0) {
    return owner + " has £" + balance;    // ✅ GREEN — tested
} else if (balance == 0) {
    return owner + " has empty account";  // ❌ RED  — never tested
} else {
    return owner + " overdrawn by £";     // ❌ RED  — never tested
}

// UserValidator.maskPassword()
if (password == null) {
    return "****";                         // ✅ GREEN — tested
}
if (password.length() <= 2) {
    return "**";                           // ❌ RED  — never tested
}
return "*".repeat(...) + ...;             // ✅ GREEN — tested
```

These gaps are **intentional** so you can see exactly what RED lines look like in SonarCloud.

---

## ⚡ GitHub Actions vs Jenkins – Key Differences

| Feature | GitHub Actions | Jenkins |
|---------|---------------|---------|
| **Hosting** | Fully managed by GitHub — zero infrastructure | Self-hosted server you install and maintain |
| **Configuration** | YAML file in `.github/workflows/` | `Jenkinsfile` (Groovy DSL) stored in repo |
| **Setup time** | ~5 minutes (just create the YAML file) | ~30–60 minutes (install server, plugins, tools) |
| **Server cost** | Free for public repos | Free software, but you pay for the server/VM |
| **Triggers** | GitHub events (push, PR, schedule, manual) | Webhooks, polling, or manual builds |
| **Runners/Agents** | GitHub-hosted fresh VMs (auto-provisioned) | Agents you provision and maintain |
| **Pipeline language** | YAML | Groovy (Declarative or Scripted) |
| **Plugin ecosystem** | 10,000+ GitHub Marketplace Actions | 1,800+ Jenkins plugins |
| **Scalability** | Automatic — GitHub spins up runners as needed | Manual — you add and manage agents |
| **Secrets management** | GitHub → Settings → Secrets | Jenkins → Credentials Manager |
| **Artefact storage** | `actions/upload-artifact` step | `archiveArtifacts` step |
| **Best suited for** | GitHub-hosted projects needing quick setup | Complex enterprise pipelines needing full control |

### Side-by-Side Pipeline Comparison

```
GitHub Actions (ci.yml)                  Jenkins (Jenkinsfile)
──────────────────────────────────       ────────────────────────────────────
on: push / pull_request                  Triggered by webhook or manual click
runs-on: ubuntu-latest                   agent any  (your Jenkins server)
uses: actions/checkout@v4               checkout scm
uses: actions/setup-java@v4             tools { jdk 'JDK-11'; maven 'Maven-3' }
run: mvn compile                         sh 'mvn compile'
run: mvn test                            sh 'mvn test'
run: mvn package -DskipTests            sh 'mvn package -DskipTests'
uses: actions/upload-artifact@v4        archiveArtifacts 'target/*.jar'
secrets.SONAR_TOKEN                      credentials('sonar-token')
```

---

## 🔑 Secrets Reference

| Secret Name | Where to Add | Used In | Description |
|------------|-------------|---------|-------------|
| `SONAR_TOKEN` | GitHub → Settings → Secrets | `ci.yml` Job 2 | SonarCloud authentication token (you generate this) |
| `GITHUB_TOKEN` | Auto-provided by GitHub | `ci.yml` Job 2 | Allows SonarCloud to post Quality Gate comments on PRs |

---

## 🔢 Test Summary

| Test Class | Tests | What It Covers |
|-----------|-------|---------------|
| `CalculatorTest` | 14 | All 5 Calculator methods, edge cases (divide by zero, negative factorial) |
| `StringUtilsTest` | 19 | All 4 StringUtils methods, null/empty/whitespace inputs |
| `BankAccountTest` | 10 | BankAccount methods + **proves the withdraw() bug** |
| `UserValidatorTest` | 15 | UserValidator methods + **proves == bug** + **proves NPE bug** |
| **Total** | **58** | |

---

## 🛠️ Troubleshooting

### GitHub Actions

| Problem | Cause | Fix |
|---------|-------|-----|
| `SONAR_TOKEN not set` | Secret is missing | Add `SONAR_TOKEN` in GitHub → Settings → Secrets |
| `sonarcloud-analysis` skipped | `build-and-test` job failed | Fix compile/test errors first; Job 2 only runs when Job 1 passes |
| Coverage shows 0% on SonarCloud | `fetch-depth: 0` missing | Add `fetch-depth: 0` to the checkout step in Job 2 |
| `mvn: command not found` | Wrong runner OS | Use `ubuntu-latest` — Maven is pre-installed on it |
| Workflow not triggering | Branch filter too restrictive | Check the `branches:` filter under `on: push:` |

### Jenkins

| Problem | Cause | Fix |
|---------|-------|-----|
| `fatal: couldn't find remote ref refs/heads/master` | Jenkins defaults to `master`, repo uses `main` | Job config → Branches to build → change `*/master` to `*/main` |
| `mvn: command not found` | Maven not configured in Jenkins | Manage Jenkins → Tools → add Maven → name it exactly `Maven-3` |
| `JDK-11 not found` | JDK not configured in Jenkins | Manage Jenkins → Tools → add JDK → name it exactly `JDK-11` |
| Cannot access `http://localhost:8080` | Jenkins Docker container not running | Run `docker start jenkins` |
| Blank password field in browser | Missed the initial password step | Run `docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword` |

### SonarCloud

| Problem | Cause | Fix |
|---------|-------|-----|
| `Project not found` | Wrong project key | Check `sonar.projectKey` in `pom.xml` matches exactly what SonarCloud shows |
| `Organization not found` | Wrong org key | Check `sonar.organization` in `pom.xml` matches your SonarCloud organization key |
| `Coverage: 0%` | JaCoCo report not found | Make sure `mvn verify` and `sonar:sonar` run in the **same Maven command** |
| `401 Unauthorized` | Invalid or expired token | Re-generate the token in SonarCloud → My Account → Security → add to GitHub Secrets |
| Analysis not appearing | Wrong branch analyzed | SonarCloud defaults to showing the main branch; check the branch dropdown |

---

## ✅ Quick Setup Checklist

### GitHub Actions
```
✅ Code is on GitHub
✅ .github/workflows/ci.yml exists in the repository
□  SONAR_TOKEN secret added (GitHub → Settings → Secrets)
   → Pipeline triggers automatically on every push!
```

### Jenkins
```
□ Docker installed and running
□ Jenkins container started: docker run ... jenkins/jenkins:lts-jdk11
□ Setup wizard completed:
    □ Unlocked with initialAdminPassword
    □ Suggested plugins installed
    □ Admin user created (this is YOUR Jenkins password)
□ Additional plugins installed: Pipeline, Git, Maven Integration
□ Tools configured:
    □ JDK  → name: JDK-11  (must match Jenkinsfile exactly)
    □ Maven → name: Maven-3 (must match Jenkinsfile exactly)
□ Pipeline job created:
    □ SCM = Git
    □ Repository URL = https://github.com/rasmikanta19/GitHubActions-JenkinsDemoProject
    □ Branch Specifier = */main   (NOT */master)
    □ Script Path = Jenkinsfile
□ Click Build Now → watch the stages run
```

### SonarCloud
```
□ Account created at sonarcloud.io (login with GitHub)
□ Organization created: rasmikanta19
□ Project created: GitHubActions-JenkinsDemoProject
□ SONAR_TOKEN generated (My Account → Security → Generate Tokens)
□ SONAR_TOKEN added to GitHub repository Secrets
□ Push code to trigger the pipeline
□ View full report at: sonarcloud.io/projects
```
