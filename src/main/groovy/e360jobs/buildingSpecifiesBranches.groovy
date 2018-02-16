package e360jobs

import groovy.json.JsonSlurper

def gitlabAccessToken = 'tt9yY2xvPzX7ApygBJnB'

def project = 'valerii_zhuk/e360'
def projectEncoded = 'valerii_zhuk%2Fe360'
def branchNames = ['develop','master']
def projectURL = "git@git.epam.com:${project}.git"

for (branchName in branchNames) {
    def branchApi = new URL("https://git.epam.com/api/v4/projects/${projectEncoded}/repository/branches/${branchName}?private_token=$gitlabAccessToken")
    def branchToCheck = new JsonSlurper().parse(branchApi.newReader())
    def authorEmail = branchToCheck.commit.author_email

    job("build-branch-$branchName") {
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
        triggers {
            scm('5 * * * *')
        }
        publishers {
            extendedEmail {
                recipientList("Aleksandr_Sakhnenko")
                defaultSubject('$DEFAULT_SUBJECT')
                defaultContent('$DEFAULT_CONTENT' + " author ========== ${authorEmail}")
                triggers {
                    success() {
                        sendTo {
                            recipientList()
                        }
                    }
                }
            }
        }
        steps {
            maven('-e clean package')
        }
    }
}