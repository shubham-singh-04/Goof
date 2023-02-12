// Jenkinsfile solution based on the pipeline plugin:
// https://www.jenkins.io/solutions/pipeline/

// Results are output in SARIF format & processed using the Warnings NG plugin:
// https://plugins.jenkins.io/warnings-ng/

// Please read the contents of this file carefully & ensure the URLs, tokens etc match your organisations needs.


pipeline {
    agent any

    // Requires a configured NodeJS installation via https://plugins.jenkins.io/nodejs/
    tools { nodejs "NodeJS 18.9.1" }

    stages {
        stage('git clone') {
            steps {
                git url: 'https://github.com/jennysnyk/goof.git'
            }
        }
        
        // Install the Snyk CLI with npm. For more information, check:
        // https://docs.snyk.io/snyk-cli/install-the-snyk-cli
        stage('Install snyk CLI') {
            steps {
                script {
                    sh 'npm install -g snyk'
                }
            }
        }
        
        // Install Snyk Filter
        // https://docs.snyk.io/snyk-api-info/other-tools/tool-snyk-filter
        stage('Install snyk filter') {
            steps {
                sh 'npm i -g snyk-filter'
            }
        }

        // This OPTIONAL step will configure the Snyk CLI to connect to the EU instance of Snyk.
        // stage('Configure Snyk for EU data center') {
        //     steps {
        //         sh './snyk config set use-base64-encoding=true'
        //         sh './snyk config set endpoint='https://app.eu.snyk.io/api'
        //     }
        // }
        
        // Authorize the Snyk CLI
        stage('Authorize Snyk CLI') {
            steps {
                withCredentials([string(credentialsId: 'SNYK_TOKEN', variable: 'SNYK_TOKEN')]) {
                    sh 'snyk auth ${SNYK_TOKEN}'
                }
            }
        }

        stage('Build App') {
            steps {
                // Replace this with your build instructions, as necessary.
                sh 'echo no-op'
            }
        }

        stage('Snyk') {
            parallel {
                stage('Snyk Open Source') {
                    steps {
                            sh 'snyk test --json | snyk-filter -f /path/to/example-cvss-9-or-above.yml' // this template had been used https://github.com/snyk-labs/snyk-filter/blob/develop/sample-filters/example-cvss-9-or-above.yml
                        }
                    }
                }
                // stage('Snyk Code') {
                //     steps {
                //             sh './snyk code test'
                //         }
                //     }
                // }
            }
        }
    }
