package jobs

def project = 'Aleksandr_Sakhnenko/jenkinstest'
def credentials = 'e360-ssh-Sakhnenko'
def projectURL = "git@git.epam.com:${project}.git"

def generator = new JobGenerator(project, projectURL, credentials)

generator.createBuildJob('develop')

generator.createBuildJob('master')

generator.createReleaseJob()

