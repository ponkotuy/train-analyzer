$(document).ready ->
  $('#postPattern').click ->
    name = $('#patternName').val()
    period = $('#timeTablePeriod').val()
    lineId = parseInt($(@).attr('data-line-id'))
    postJSON("/line/#{lineId}/pattern", {name: name, timeTablePeriod: period, trains: []})
