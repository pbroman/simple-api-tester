function(metadata=null, request_definition=null, response_handling=null, flow_control=null, skip_condition=null)
  (if metadata != null then metadata else { 'metadata': {} }) +
  (if request_definition != null then request_definition else { requestDefinition: {} }) +
  (if response_handling != null then response_handling else { responseHandling: { assertions: [] }}) +
  (if flow_control != null then flow_control else {}) +
  if skip_condition != null then skip_condition else {}
