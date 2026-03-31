#!/bin/sh
# Maven Wrapper script
# https://maven.apache.org/wrapper/
set -e

MAVEN_PROJECTBASEDIR="${MAVEN_BASEDIR:-$(cd "$(dirname "$0")" && pwd)}"
MAVEN_WRAPPER_JAR="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"
MAVEN_WRAPPER_PROPERTIES="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties"

if [ -r "$MAVEN_WRAPPER_PROPERTIES" ]; then
    MAVEN_URL=$(grep 'distributionUrl' "$MAVEN_WRAPPER_PROPERTIES" | cut -d'=' -f2-)
fi

MAVEN_HOME="$HOME/.m2/wrapper/dists/$(basename "$MAVEN_URL" .zip)"

if [ ! -d "$MAVEN_HOME" ]; then
    echo "Downloading Maven..."
    mkdir -p "$MAVEN_HOME"
    curl -sL "$MAVEN_URL" -o "/tmp/maven.zip"
    unzip -qo "/tmp/maven.zip" -d "$MAVEN_HOME"
    rm -f "/tmp/maven.zip"
fi

MAVEN_BIN=$(find "$MAVEN_HOME" -name 'mvn' -path '*/bin/mvn' | head -1)
exec "$MAVEN_BIN" "$@"
