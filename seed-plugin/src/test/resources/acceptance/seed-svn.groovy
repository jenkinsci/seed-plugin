package acceptance
job("${SEED_PROJECT}-${SEED_BRANCH}-build") {
    parameters {
        stringParam('COMMIT', 'HEAD', 'Commit to build')
    }
    steps {
        shell '''\
echo "Commit: ${COMMIT}"
'''
    }
}

queue("${SEED_PROJECT}-${SEED_BRANCH}-build")
