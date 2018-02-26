package jobs

import javafx.util.Builder
import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

class JobGenerator implements Builder<Job>{
    String project
    String projectURL
    String credentials
    DslFactory dslFactory
    /* Default */
    Job job

    JobGenerator(String project, String projectURL, String credentials, DslFactory dslFactory, String name) {
        this.project = project
        this.projectURL = projectURL
        this.credentials = credentials
        this.dslFactory = dslFactory
        job = dslFactory.job(name)
    }

    JobGenerator withTrigger() {
        job.triggers {
            gitlabPush {
                buildOnMergeRequestEvents(false)
                buildOnPushEvents(false)
                enableCiSkip(false)
                setBuildDescription(false)
                rebuildOpenMergeRequest('never')
            }
            scm('H/5 * * * *')
        }
        this
    }

    JobGenerator withScm(String branchName, boolean isRelease) {
        job.scm {
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
        this
    }

    def createReleaseJob() {
        job.parameters {
            gitParam('VERSION') {
                description('description')
                sortMode('DESCENDING_SMART')
                type('TAG')
            }
        }

        job.publishers {
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
        this
    }

    Job build() {
        job
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