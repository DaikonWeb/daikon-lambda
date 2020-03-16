#!/usr/bin/env sh
TAG=$1

sed -i -e "s|'com.github.DaikonWeb:daikon-lambda:.*'|'com.github.DaikonWeb:daikon-lambda:${TAG}'|g" README.md
sed -i -e "s|<version>.*</version>|<version>${TAG}</version>|g" README.md

git commit -am "Release ${TAG}"
git tag $TAG
git push
git push --tags
