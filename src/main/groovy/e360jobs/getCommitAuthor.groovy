package e360jobs

import groovy.json.JsonSlurper

def gitlabAccessToken = 'tt9yY2xvPzX7ApygBJnB'

def project = 'valerii_zhuk/e360'
def projectEncoded = 'valerii_zhuk%2Fe360'
def branchName = 'BDCCE360-179-jenkins_job_dsl_scripts'
def projectURL = "git@git.epam.com:${project}.git"

def branchApi = new URL("https://git.epam.com/api/v4/projects/${projectEncoded}/repository/branches/${branchName}?private_token=$gitlabAccessToken")
def branchToCheck = new JsonSlurper().parse(branchApi.newReader())
def authorEmail = branchToCheck.commit.author_email

job('get-commit-author-job') {

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
            recipientList("${authorEmail}")
            defaultSubject('$DEFAULT_SUBJECT')
            defaultContent('$DEFAULT_CONTENT')
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