function(path, method, body, auth, headers)
{
  requestDefinition: {
    url: '${env.baseUrl}' + path,
    'method': method,
  },
}