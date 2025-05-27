function(name='unknown', summary=null, description=null) {
  metadata: {
    'name': name,
  }
  + (if description != null then { 'description': description } else {})
  + if summary != null then { 'summary': summary } else {}
}