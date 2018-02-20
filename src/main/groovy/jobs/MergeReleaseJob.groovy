package jobs

import groovy.json.JsonSlurper
import org.gitlab.api.GitlabAPI

def gitlabAccessToken = 'tt9yY2xvPzX7ApygBJnB'



def project = 'Aleksandr_Sakhnenko/jenkinstest'//'valerii_zhuk/e360'
def projectEncoded = 'Aleksandr_Sakhnenko%2Fjenkinstest'
def branchNames = ['master']
def projectURL = "git@git.epam.com:${project}.git"

def branchApi = new URL("https://git.epam.com/api/v4/projects/${projectEncoded}/" +
        "repository/branches?private_token=$gitlabAccessToken")

def releaseBranch
for (branch in new JsonSlurper().parse(branchApi.newReader())) {
    if (branch.name.startsWith('branch1')) {
        releaseBranch = branch
        break
    }
}

def authorEmail = releaseBranch.commit.author_email
def releaseBranchName = releaseBranch.name
def version = releaseBranchName.substring(releaseBranchName.indexOf('1'), releaseBranchName.length())

for (branchName in branchNames) {
    job("merge-release-to-$branchName") {
        scm {
            git {
                remote {
                    name('origin')
                    url("$projectURL")
                    credentials('e360-ssh-Sakhnenko')
                }
                branch("${releaseBranchName}")
                extensions {
                    mergeOptions {
                        remote('origin')
                        branch("$branchName")
                    }
                }
            }
        }
        publishers {
            git {
                pushOnlyIfSuccess()
                pushMerge()
                branch('origin', "$branchName")
                tag('origin', "${version}") {
                    create()
                }
            }
            extendedEmail {
                recipientList("Aleksandr_Sakhnenko")
                defaultSubject('$DEFAULT_SUBJECT')
                defaultContent('$DEFAULT_CONTENT' + " author ========== ${authorEmail}")
                triggers {
                    failure() {
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