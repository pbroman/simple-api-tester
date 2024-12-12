local metadata = import './metadata.libsonnet';
local request_definition = import './request_definition.libsonnet';

function(path, method)
metadata(
  method.value.operationId,
  if std.objectHas(method.value, 'summary') then method.value.summary else ''
) +
request_definition(
  path,
  method.key,
  {}, // TODO build a function for body from schema
  if (std.objectHas(method.value, 'requestBody') && std.objectHas(method.value.requestBody, 'content'))
    then { 'Content-Type': std.objectFields(method.value.requestBody.content)[0], } else {}, // headers
)