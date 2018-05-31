#!/bin/bash

# Downloads released Javadoc to local directory

set -e

pushd ${TRAVIS_BUILD_DIR}

rm -rf docs/javadoc
git clone https://github.com/jgrapht/jgrapht.github.com.git
mv jgrapht.github.com/javadoc docs
rm -rf jgrapht.github.com

popd
