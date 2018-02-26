package jobs

def project = 'Aleksandr_Sakhnenko/jenkinstest'
def credentials = 'e360-ssh-Sakhnenko'
def projectURL = "git@git.epam.com:${project}.git"

def generator = new JobGenerator(project, projectURL, credentials)


generator.createBuildJob(job("FROMCLASS-$project-build-branch-develop"), 'develop')

generator.createBuildJob(job("FROMCLASS-$project-build-branch-master"),'master')

generator.createReleaseJob(job("FROMCLASS-$project-merge-release-to-master-and-develop"))

