package jobs

def project = 'Aleksandr_Sakhnenko/jenkinstest'
def branchNames = ['master', 'develop']
def projectURL = "git@git.epam.com:${project}.git"

job("merge-release-to-master-and-develop") {
    parameters {
        gitParam('RELEASE') {
            tagFilter('origin/release-.*')
            description('description')
            sortMode('DESCENDING_SMART')
            type('BRANCH')

        }
        stringParam('TAG')
    }
    scm {
        git {
            remote {
                name('origin')
                url("$projectURL")
                credentials('e360-ssh-Sakhnenko')
                refspec('+refs/heads/releases/*:refs/remotes/origin/releases/* +refs/heads/master:refs/remotes/origin/master')
            }
            branch('$RELEASE')
            extensions {
                wipeOutWorkspace()
                mergeOptions {
                    remote('origin')
                    branch("${branchNames[0]}")
                    fastForwardMode(FastForwardMergeMode.NO_FF)
                    strategy('recursive')
                }
            }
        }
    }
    publishers {
        git {
            pushOnlyIfSuccess()
            pushMerge()
            branch('origin', "${branchNames[0]}")
            tag('origin', '$RELEASE_BRANCH') {
                update()
            }
        }
        git {
            pushOnlyIfSuccess()
            pushMerge()
            branch('origin', "${branchNames[1]}")
        }
    }
//    steps {
//        maven('-e clean package')
//    }
}
