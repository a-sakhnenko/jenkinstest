package jobs

def projectURL = 'git@github.com:a-sakhnenko/jenkinstest.git'
def branchName = 'branch2'


job('testing-to-build') {

    scm {
        git {
            remote {
                name('origin')
                url("$projectURL")
                credentials('a-sakhnenko')
            }
            branch("$branchName")
        }

    }
    steps {
        maven('-e clean package')
    }
    publishers {
        extendedEmail {
            attachBuildLog(true)
            triggers {
                failure() {
                    sendTo {
                        firstFailingBuildSuspects()
                    }
                }
                success() {
                    sendTo {
                        developers()
                    }
                }
            }
        }
    }
}