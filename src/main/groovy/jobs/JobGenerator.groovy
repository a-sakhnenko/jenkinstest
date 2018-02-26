package jobs

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.*
import javaposse.jobdsl.dsl.Job

class JobGenerator {
    String project
    String projectURL
    String credentials
    DslFactory dslFactory
    Job job

    Job withTrigger() {
        job.with {
            triggers {
                gitlabPush {
                    buildOnMergeRequestEvents(false)
                    buildOnPushEvents(false)
                    enableCiSkip(false)
                    setBuildDescription(false)
                    rebuildOpenMergeRequest('never')
                }
                scm('H/5 * * * *')
            }
        }
        job
    }

    def withScm(String branchName, boolean isRelease) {
        job.with {
            scm {
                git {
                    remote {
                        name('origin')
                        url("$projectURL")
                        credentials('e360-ssh-Sakhnenko')
                        println()
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

    def createReleaseJob(DslFactory factory) {
        dslFactory = factory
        this.job = dslFactory.job("FROMCLASS-jenkinstest-merge-release-to-master-and-develop")
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
        withScm('release-$VERSION', false)
        withTrigger()
    }

//    static def createBuildJob(Job job, String branchName, String cred) {
////        def job = freeStyleJob("FROMCLASS-$project-build-branch-${branchName}")
//        withScm(job, branchName, false, cred)
//        job
//        .with {
//            publishers {
//                extendedEmail {
//                    defaultSubject('$DEFAULT_SUBJECT')
//                    defaultContent('$DEFAULT_CONTENT')
//                    triggers {
//                        failure() {
//                            sendTo {
//                                culprits()
//                            }
//                        }
//                    }
//                }
//            }
//            steps {
//                maven('-e clean package')
//            }
//        }
//        withTrigger(job)
//    }
}