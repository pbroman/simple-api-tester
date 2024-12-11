local metadata = import './metadata.libsonnet';
local request = import './request.libsonnet';

local request_wrapper = function(path)
[
  request(path.key, method) for method in std.objectKeysValuesAll(path.value)
];

function(openapi)
metadata(
  openapi.info.title,
  'Generated from openapi. Application version: ' + openapi.info.version + ', openapi version: ' + openapi.openapi
) +
{
  requests: std.flattenArrays([ request_wrapper(path) for path in std.objectKeysValuesAll(openapi.paths) ])
}