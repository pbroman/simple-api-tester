local metadata = import '../metadata.libsonnet';
local request_definition = import '../request_definition.libsonnet';
local request = import '../request.libsonnet';
local post_request_builder = import './post_request_builder.libsonnet';

local simple_request = function(path, method)
  request(
    metadata(
      method.value.operationId,
      if std.objectHas(method.value, 'summary') then method.value.summary else null
    ),
    request_definition(
      path,
      method.key,
    )
  );

local request_wrapper = function(path)
  std.flattenArrays([
    if method.key == 'post'
        then post_request_builder(path.key, method.value)
        else [ simple_request(path.key, method) ]
      for method in std.objectKeysValues(path.value)
  ]);

function(paths)
  std.flattenArrays([
    request_wrapper(path) for path in std.objectKeysValues(paths)
  ])
