$(document).ready ->
  $('#postPattern').click ->
    name = $('#patternName').val()
    lineId = parseInt($(@).attr('data-line-id'))
    postJSON("/line/#{lineId}/pattern", {name: name, trains: []})
