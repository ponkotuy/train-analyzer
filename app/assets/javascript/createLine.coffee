
$(document).ready ->
  $('#postLine').click ->
    name = $('#lineName').val()
    period = $('#timeTablePeriod').val()
    stNames = $('#stations').val().split(',')
    stations = stNames.map (name, idx) ->
      {name: name, no: idx + 1}
    postJSON('/line', ({name: name, timeTablePeriod: period, stations: stations, patterns: []}))
