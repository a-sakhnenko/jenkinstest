package jobs

import hudson.model.FreeStyleBuild
import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.JobParent
import javaposse.jobdsl.dsl.jobs.FreeStyleJob

class JobGenerator {
    static String project
    static String projectURL
    static String credentials

    JobGenerator(String project, String projectURL, String credentials) {
        this.project = project
        this.projectURL = projectURL
        this.credentials = credentials
    }

    def createJob(String name) {
//        new FreeStyleJob(JobManagement.newInstance(), "FROMCLASS-$name")
        def job = job("FROMCLASS-" + name) {}
        job
    }

    static Job withTrigger(Job job) {
        job.with {
            triggers {
                scm('H/5 * * * *')
            }
        }
        job
    }

    static def withScm(Job job, String branchName, boolean isRelease) {
        job.with {
            scm {
                git {
                    remote {
                        name('origin')
                        url("$projectURL")
                        credentials(credentials)
                        refspec('+refs/heads/releases/*:refs/remotes/origin/releases/* +refs/heads/master:refs/remotes/origin/master')
                    }
                    branch("$branchName")
                    if (isRelease) {
                        extensions {
                            wipeOutWorkspace()
                            mergeOptions {
                                remote('origin')
                                branch('develop')
//                                fastForwardMode(FastForwardMergeMode.NO_FF)
                                strategy('recursive')
                            }
                        }
                    }
                }
            }
        }
    }

    static def createReleaseJob(Job job) {
        job.with{
            parameters {
                gitParam('RELEASE') {
                    description('description')
                    sortMode('DESCENDING_SMART')
                    type('BRANCH')
                }
            }

            publishers {
                git {
                    pushOnlyIfSuccess()
                    pushMerge()
                    branch('origin', 'master')
                    tag('origin', '$TAG') {
                        update()
                    }
                }
                git {
                    pushOnlyIfSuccess()
                    pushMerge()
                    branch('origin', 'develop')
                }
            }

        }
        withScm(job, '$RELEASE', true)
    }

    static def createBuildJob(Job job, String branchName) {
//        def job = freeStyleJob("FROMCLASS-$project-build-branch-${branchName}")
        withScm(job, branchName, false)
        job
        .with {
            publishers {
                extendedEmail {
                    defaultSubject('$DEFAULT_SUBJECT')
                    defaultContent('$DEFAULT_CONTENT')
                    triggers {
                        failure() {
                            sendTo {
                                culprits()
                            }
                        }
                    }
                }
            }
            steps {
                maven('-e clean package')
            }
        }
        withTrigger(job)
    }
}