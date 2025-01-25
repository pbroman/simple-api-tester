local metadata = import './metadata.libsonnet';
local request = import './request.libsonnet';
local post_request = import './post_request.libsonnet';

local request_wrapper = function(path)
std.flattenArrays([
  if method.key == 'post' then post_request(path.key, method.value) else request(path.key, method) for method in std.objectKeysValuesAll(path.value)
]);

function(openapi)
metadata(
  openapi.info.title,
  'Generated from openapi. Application version: '
    + if std.objectHas(openapi.info, 'version') then openapi.info.version else 'unknown'
    + ', openapi version: ' + openapi.openapi
) +
{
  requests: std.flattenArrays([ request_wrapper(path) for path in std.objectKeysValuesAll(openapi.paths) ])
}