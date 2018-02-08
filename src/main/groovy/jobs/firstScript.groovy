package jobs

def projectURL = 'git@github.com:a-sakhnenko/jenkinstest.git'
def branchName = 'branch2'


job('testing-to-build') {

    scm {
        git {
            remote {
                name('origin')
                github('a-sakhnenko/jenkinstest', 'ssh')

                credentials('f0558399-00e7-44a0-98b5-b9e5d5d0cf9d')
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