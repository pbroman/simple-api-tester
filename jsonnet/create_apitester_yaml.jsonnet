local testsuite = import 'templates/testsuite.libsonnet';

//local config_wrapper = import 'config/config_wrapper.libsonnet';
//local default_config = import 'config/default_config.libsonnet';

local openapi = import 'workdir/openapi.json';
//local config = config_wrapper('openapi', openapi);

//local config = openapi + default_config;

std.manifestYamlDoc(testsuite(openapi), indent_array_in_object=true, quote_keys=false)

