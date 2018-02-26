package jobs

import javaposse.jobdsl.dsl.DslFactory

def project = 'Aleksandr_Sakhnenko/jenkinstest'
def credentials = 'e360-ssh-Sakhnenko'
def projectURL = "git@git.epam.com:${project}.git"

def generator = new JobGenerator(project, projectURL, credentials, this as DslFactory, 'FROMCLASS-jenkinstest-merge-release-to-master-and-develop')

//generator.project = project
//generator.credentials = credentials
//generator.projectURL = projectURL

//JobGenerator.createBuildJob(job("FROMCLASS-jenkinstest-build-branch-develop"), 'develop', credentials)
//
//JobGenerator.createBuildJob(job("FROMCLASS-jenkinstest-build-branch-master"),'master', credentials)

generator.createReleaseJob().withScm('release', false).withTrigger().build()

