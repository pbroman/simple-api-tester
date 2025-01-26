function(metadata=null, requests=[], sub_suites=[], constants=null, auth=null, default_timeout=null, skip_condition=null)
  (if metadata != null then metadata else { 'metadata': { name: 'unknown' } }) +
  { 'requests': requests } +
  { subSuites: sub_suites } +
  (if constants != null then constants else { constants: {} }) +
  (if auth != null then auth else {}) +
  (if default_timeout != null then default_timeout else {}) +
  if skip_condition != null then skip_condition else {}
