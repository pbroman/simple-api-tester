local requests_builder = import './requests_builder.libsonnet';
local testsuite = import '../testsuite.libsonnet';
local metadata = import '../metadata.libsonnet';

function(openapi)
  testsuite(
    metadata(
      openapi.info.title,
      'Generated from openapi. Application version: '
        + if std.objectHas(openapi.info, 'version') then openapi.info.version else 'unknown'
        + ', openapi version: ' + openapi.openapi
    ),
    requests_builder(openapi.paths)
  )