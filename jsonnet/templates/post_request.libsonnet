local metadata = import './metadata.libsonnet';
local request_definition = import './request_definition.libsonnet';

local single = function(path, requests)
[
metadata(
  requests.operationId,
  if std.objectHas(requests, 'summary') then requests.summary else ''
) +
request_definition(
  path,
  'POST',
  if (std.objectHas(requests, 'requestBody') && std.objectHas(requests.requestBody, 'content'))
    then { 'Content-Type': std.objectFields(requests.requestBody.content)[0], } else {}, // headers
  {},
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
  metadata(
    example.request.key,
    example.request.value.description
  ) +
  request_definition(
    path,
    'POST',
    { 'Content-Type': example.content_type },
    example.request.value.value,
  )
  for example in exs
]
