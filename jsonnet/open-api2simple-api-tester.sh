#!/usr/bin/env bash

##############################################
#
# Script executing jsonnet creating simple-api-tester yaml from an openapi json.
#
#
# Prerequisites:
# Options: see ./open-api2simple-api-tester.sh -h
#
##############################################

#set -x
set -e

usage() {
    cat <<EOT

$0 - create simple-api-tester yaml stub

Usage: $0 [OPTIONS] <input openapi file>

Options:
  -o, --output               Output file path. If none given, the output will be to stdout
  -h, --help                 Show this help
EOT
    exit 3
}


args() {

  while [[ "$1" != "" ]]; do
    case $1 in
      -o|--output )                 shift
                                    OUTPUT="$1"
                                    shift
                                    ;;
      -h|--help )                   usage
                                    ;;
      -*)                           echo "Unrecognized option $1"
                                    usage
                                    ;;
      *)                            [[ "${INPUT}" != "" ]] && echo "Duplicated input file argument: '${INPUT}' and '$1'" && usage
                                    INPUT="$1"
                                    shift
                                    ;;
    esac
  done

  if [[ "${INPUT}" == "" ]]; then
    usage
  fi

}

main() {

  args "$@"

  if [[ ! -f ${INPUT} ]]; then
    echo "Openapi input file '${INPUT}' not found"
    exit 1
  fi

  mkdir -p workdir
  cp ${INPUT} workdir/openapi.json

  echo "Converting openapi ${INPUT} to simple-api-tester yaml."
  if [[ "${OUTPUT}" = "" ]]; then
    jsonnet --string create_apitester_yaml.jsonnet
  else
    echo "Output file: ${OUTPUT}"
    jsonnet --string create_apitester_yaml.jsonnet > ${OUTPUT}
  fi

}

main "$@"
