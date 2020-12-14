#!/usr/bin/env bash
set -o errexit
set -o pipefail
set -o nounset

readonly JOERN_INSTALL=~/bin/joern/joern-cli
readonly JAR_INSTALL_DIR=${JOERN_INSTALL}/lib/
readonly SCHEMA_SRC_DIR=schema/src/main/resources/schema/

echo "Examining Joern installation..."

if [ ! -d "${JOERN_INSTALL}" ]; then
    echo "Cannot find Joern installation at ${JOERN_INSTALL}"
    echo "Please install Joern first"
    exit
fi

echo "Compiling (sbt stage)..."
sbt stage

if compgen -G "${JAR_INSTALL_DIR}/io.joern.batteries*.jar" > /dev/null; then
    echo "Already installed. Uninstalling first."
    rm ${JAR_INSTALL_DIR}/io.joern.batteries*.jar
fi

echo "Installing jars into: ${JAR_INSTALL_DIR}"
cp target/universal/stage/lib/io.joern.batteries*.jar ${JAR_INSTALL_DIR}

echo "Adapting CPG schema"
cp ${SCHEMA_SRC_DIR}/*.json ${JOERN_INSTALL}/schema-extender/schemas/
pushd $JOERN_INSTALL
./schema-extender.sh
popd
