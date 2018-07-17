#!/usr/bin/env bash

function join { local IFS="$1"; shift; echo "$*"; }

# $1 - index, $2 - version, $3 - new value
function set_version {
splitVer=(${2//./ })
splitVer[$1]=$3
newVersion=$(join . ${splitVer[@]})
echo $newVersion;
}
# $1- index of version, $2- full version
function increment_version {
splitVer=(${2//./ })
splitVer[$1]=$((splitVer[$1]+1))
newVersion=$(join . ${splitVer[@]})
echo $newVersion;
}
function increment_major { echo `set_version 2 $(set_version 1 $(increment_version 0 $1) 0) 0`; }
function increment_minor { echo `set_version 2 $(increment_version 1 $1) 0`; }
function increment_patch { echo `increment_version 2 $1`; }

git checkout master
git pull
git merge dev

version=`grep '<app.version>' app/pom.xml | sed "s@.*<app.version>\(.*\)</app.version>.*@\1@"`

case "$1" in
        major)
            newVersion=`increment_major $version`
            ;;

        minor)
            newVersion=`increment_minor $version`
            ;;

        patch)
            newVersion=`increment_patch $version`
            ;;

        *)
            newVersion=`increment_patch $version`
            ;;
esac

echo $version '->' $newVersion

sed -i -e "s|<app.version>\(.*\)</app.version>|<app.version>${newVersion}</app.version>|g" app/pom.xml

git commit -am "increment version to ${newVersion}"

git push origin master

git tag "v${newVersion}"

git push --tag
