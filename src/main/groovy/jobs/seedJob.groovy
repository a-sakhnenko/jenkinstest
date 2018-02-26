package jobs

def project = 'Aleksandr_Sakhnenko/jenkinstest'
def credentials = 'e360-ssh-Sakhnenko'
def projectURL = "git@git.epam.com:${project}.git"

JobGenerator.project = project
JobGenerator.credentials = credentials
JobGenerator.projectURL = projectURL

JobGenerator.createBuildJob(job("FROMCLASS-jenkinstest-build-branch-develop"), 'develop')

JobGenerator.createBuildJob(job("FROMCLASS-jenkinstest-build-branch-master"),'master')

JobGenerator.createReleaseJob(job("FROMCLASS-jenkinstest-merge-release-to-master-and-develop"))

