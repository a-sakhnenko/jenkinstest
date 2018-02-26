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

    static Job withTrigger(Job job) {
        job.with {
            triggers {
                scm('H/5 * * * *')
            }
        }
        job
    }

    static def withScm(Job job, String branchName, boolean isRelease, String cred) {
        job.with {
            scm {
                git {
                    remote {
                        name('origin')
                        url("$projectURL")
                        credentials(cred)
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

    static def createReleaseJob(Job job, String cred) {
        job.with{
            parameters {
                gitParam('VERSION') {
                    description('description')
                    sortMode('DESCENDING_SMART')
                    type('TAG')
                }
            }

            publishers {
                git {
                    pushOnlyIfSuccess()
                    pushMerge()
                    branch('origin', 'master')
                    tag('origin', '$VERSION') {
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
        withScm(job, 'release-$VERSION', false, cred)
    }

    static def createBuildJob(Job job, String branchName, String cred) {
//        def job = freeStyleJob("FROMCLASS-$project-build-branch-${branchName}")
        withScm(job, branchName, false, cred)
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