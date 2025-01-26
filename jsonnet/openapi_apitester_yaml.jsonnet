local testsuite_builder = import 'templates/openapi/testsuite_builder.libsonnet';
local openapi = import 'workdir/openapi.json';

std.manifestYamlDoc(testsuite_builder(openapi), indent_array_in_object=true, quote_keys=false)

