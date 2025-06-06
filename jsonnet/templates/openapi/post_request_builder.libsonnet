local metadata = import '../metadata.libsonnet';
local request = import '../request.libsonnet';
local request_definition = import '../request_definition.libsonnet';

local single = function(path, requests)
[
  request(
    metadata(
      requests.operationId,
      if std.objectHas(requests, 'summary') then requests.summary else null,
      if std.objectHas(requests, 'description') then requests.description else null
    ),
    request_definition(
      path,
      'POST',
      if (std.objectHas(requests, 'requestBody') && std.objectHas(requests.requestBody, 'content'))
        then { 'Content-Type': std.objectFields(requests.requestBody.content)[0], } else {},
      {},
    )
  )
];

local example_wrapper = function(content_type)
if std.objectHas(content_type.value, 'examples')
  then [{ 'content_type': content_type.key, request: example} for example in std.objectKeysValuesAll(content_type.value.examples)]
  else []
;

local examples = function(requests)
if std.objectHas(requests, 'requestBody') && std.objectHas(requests.requestBody, 'content')
  then std.flattenArrays([example_wrapper(content_type) for content_type in std.objectKeysValues(requests.requestBody.content) ])
  else []
;

function(path, requests)
local exs = examples(requests);
if std.length(exs) == 0 then single(path, requests)
else
[
  request(
    metadata(
      example.request.key,
      example.request.value.description
    ),
    request_definition(
      path,
      'POST',
      { 'Content-Type': example.content_type },
      example.request.value.value,
    )
  )
  for example in exs
]
