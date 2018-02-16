package e360jobs

import groovy.json.JsonSlurper

def projectURL = 'git@git.epam.com:valerii_zhuk/e360.git'
def branchName = 'develop'
//TODO: see dsl api - job publishers postBuildScripts onlyIfBuildFails
def branchApi = new URL("https://api.github.com/repos/${project}/branches")
def branches = new JsonSlurper().parse(branchApi.newReader())

job('testing-to-build-develop') {

    scm {
        git {
            remote {
                name('origin')
                url("$projectURL")
                credentials('e360-ssh-Sakhnenko')
            }

            branch("$branchName")
        }

    }
    publishers {
        extendedEmail {
            recipientList('$DEFAULT_RECIPIENTS')
            defaultSubject('$DEFAULT_SUBJECT')
            defaultContent('$DEFAULT_CONTENT')
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
    steps {
        maven('-e clean package')
    }
}