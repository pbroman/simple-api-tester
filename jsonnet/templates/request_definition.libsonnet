function(path, method, body, headers)
{
  requestDefinition: {
    url: '${env.baseUrl}' + path,
    'method': method,
    'headers': headers,
  },
}