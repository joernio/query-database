#!/usr/bin/env bash
set -o errexit
set -o pipefail
set -o nounset
set -eu

readonly JOERN_VERSION="v1.1.66"

if [ "$(uname)" = 'Darwin' ]; then
  # get script location
  # https://unix.stackexchange.com/a/96238
  if [ "${BASH_SOURCE:-x}" != 'x' ]; then
    this_script=$BASH_SOURCE
  elif [ "${ZSH_VERSION:-x}" != 'x' ]; then
    setopt function_argzero
    this_script=$0
  elif eval '[[ -n ${.sh.file} ]]' 2>/dev/null; then
    eval 'this_script=${.sh.file}'
  else
    echo 1>&2 "Unsupported shell. Please use bash, ksh93 or zsh."
    exit 2
  fi
  relative_directory=$(dirname "$this_script")
  SCRIPT_ABS_DIR=$(cd "$relative_directory" && pwd)
else
  SCRIPT_ABS_PATH=$(readlink -f "$0")
  SCRIPT_ABS_DIR=$(dirname "$SCRIPT_ABS_PATH")
fi

# Check required tools are installed.
check_installed() {
  if ! type "$1" > /dev/null; then
    echo "Please ensure you have $1 installed."
    exit 1
  fi
}

readonly JOERN_INSTALL=$SCRIPT_ABS_DIR/joern-inst/joern-cli
readonly PLUGIN_DIR=${JOERN_INSTALL}/lib/
readonly SCHEMA_SRC_DIR=schema/src/main/resources/schema/

echo "Examining Joern installation..."

if [ ! -d "${JOERN_INSTALL}" ]; then
    echo "Cannot find Joern installation at ${JOERN_INSTALL}"
    echo "Installing..."
    check_installed "curl"
    echo "https://github.com/ShiftLeftSecurity/joern/releases/download/$JOERN_VERSION/joern-cli.zip"
    curl -L "https://github.com/ShiftLeftSecurity/joern/releases/download/$JOERN_VERSION/joern-cli.zip" -o "$SCRIPT_ABS_DIR/joern-cli.zip"
    mkdir $SCRIPT_ABS_DIR/"joern-inst"
    mv "joern-cli.zip" $SCRIPT_ABS_DIR/"joern-inst/"
    pushd $SCRIPT_ABS_DIR/"joern-inst/"
      unzip "joern-cli.zip"
    popd
    pushd $SCRIPT_ABS_DIR
    ln -s joern-inst/joern-cli/joern . || true
    ln -s joern-inst/joern-cli/joern-parse . || true
    ln -s joern-inst/joern-cli/fuzzyc2cpg.sh . || true
    popd
fi

echo "Compiling (sbt createDistribution)..."
pushd $SCRIPT_ABS_DIR
rm lib || true
sbt createDistribution
popd

pushd $SCRIPT_ABS_DIR
  ln -s joern-inst/joern-cli/lib . || true
  ./joern --remove-plugin querydb
  ./joern --add-plugin ./querydb.zip
  rm lib
popd
