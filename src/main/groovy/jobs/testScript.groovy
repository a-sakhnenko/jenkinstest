package jobs

job('testing-to-build') {

    scm {
        git('git://github.com/a-sakhnenko/jenkinstest')
    }
    steps {
        maven('-e clean test')
    }
}