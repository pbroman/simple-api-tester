function(path, method, headers={}, body=null)
{
  requestDefinition: {
    url: '${env.baseUrl}' + path,
    'method': method,
    'headers': headers,
  } +
  if body != null then { 'body': { raw: std.toString(body) }}
  else if (std.objectHas(headers, 'Content-Type') && std.startsWith(['headers.Content-Type'], 'application/json')) then {'body': { raw: '{}' }}
  else {},
}