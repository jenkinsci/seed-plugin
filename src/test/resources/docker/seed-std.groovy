package docker
// TODO Folder path
// TODO Project/branch prefix (normalised branch name)
// TODO Chaining

freeStyleJob("${PROJECT}/${PROJECT}-${BRANCH}/${PROJECT}-${BRANCH}-build") {

}

freeStyleJob("${PROJECT}/${PROJECT}-${BRANCH}/${PROJECT}-${BRANCH}-ci") {

}

freeStyleJob("${PROJECT}/${PROJECT}-${BRANCH}/${PROJECT}-${BRANCH}-publish") {

}
