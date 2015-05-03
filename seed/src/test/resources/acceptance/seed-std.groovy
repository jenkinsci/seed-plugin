package acceptance
/**
 * Sample pipeline generation, used for acceptance tests
 */

// TODO Folder path
// TODO Project/branch prefix (normalised branch name)
// TODO Document parameters

freeStyleJob("${PROJECT}/${PROJECT}-${BRANCH}/${PROJECT}-${BRANCH}-build") {
    parameters {
        stringParam('COMMIT', 'HEAD', 'Commit to build')
    }
    steps {
        shell '''\
echo "Commit: ${COMMIT}"
'''
    }
    publishers {
        downstreamParameterized {
            trigger("${PROJECT}/${PROJECT}-${BRANCH}/${PROJECT}-${BRANCH}-ci", 'SUCCESS', true) {
            }
        }
    }
}

freeStyleJob("${PROJECT}/${PROJECT}-${BRANCH}/${PROJECT}-${BRANCH}-ci") {
    publishers {
        downstreamParameterized {
            trigger("${PROJECT}/${PROJECT}-${BRANCH}/${PROJECT}-${BRANCH}-publish", 'SUCCESS', true) {
            }
        }
    }
}

freeStyleJob("${PROJECT}/${PROJECT}-${BRANCH}/${PROJECT}-${BRANCH}-publish") {

}
