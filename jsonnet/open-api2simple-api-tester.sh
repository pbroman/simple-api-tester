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
  -p, --pretty-print         true/false - Pretty-print raw json body. Default: true
  -h, --help                 Show this help
EOT
    exit 3
}


args() {
  PRETTY_PRINT="true"

  while [[ "$1" != "" ]]; do
    case $1 in
      -o|--output )                 shift
                                    OUTPUT="$1"
                                    shift
                                    ;;
      -p|--pretty-print )           shift
                                    PRETTY_PRINT="$1"
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

pretty_print_raw_json() {
  array=("$@")
  IFS=''
  for line in "${array[@]}"; do
    if [[ $line =~ .*raw:.* ]]; then
      json_line_prefix=$(echo "$line" | cut -d ":" -f 1)
      indent_spaces=$(echo "$json_line_prefix" | cut -d "r" -f 1)
      echo "${json_line_prefix}: >"
      echo ${line#*raw:} | jq -r . | jq . | while read object; do
        echo "  $indent_spaces$object"
      done
    else
      echo "$line"
    fi
  done
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

  mapfile -t yaml_array < <(jsonnet --string openapi_apitester_yaml.jsonnet)

  if [[ "${PRETTY_PRINT}" != "false" ]]; then
    mapfile -t yaml_array < <(pretty_print_raw_json  "${yaml_array[@]}")
  fi

  if [[ "${OUTPUT}" = "" ]]; then
    printf '%s\n' "${yaml_array[@]}"
  else
    echo "Output file: ${OUTPUT}"
    printf '%s\n' "${yaml_array[@]}" > ${OUTPUT}
  fi

}

main "$@"
