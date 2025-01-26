function(name='unknown', description=null) {
  metadata: {
    'name': name,
  } +
  if description != null then { 'description': description } else {}
}