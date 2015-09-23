$(document).ready ->
  $('#createTrain').click ->
    patternId = parseInt($(@).attr('data-pattern-id'))
    tclass = $('#trainClass').val()
    name = $('#trainName').val()
    arrives = []
    $('input.arrive-time').each ->
      sno = parseInt($(@).attr('data-station-no'))
      arrives[sno] = parseInt($(@).val())
    departs = []
    $('input.depart-time').each ->
      sno = parseInt($(@).attr('data-station-no'))
      departs[sno] = parseInt($(@).val())
    arriveTable = arrives.map (x, n) -> {minutes: x, isArrive: true, stationNo: n}
      .filter((obj) -> obj.minutes? and not Number.isNaN(obj.minutes))
    departTable = departs
      .map (x, n) -> {minutes: x, isArrive: false, stationNo: n}
    .filter((obj) -> obj.minutes? and not Number.isNaN(obj.minutes))
    data =
      trainClass: tclass
      name: name
      timeTable: arriveTable.concat(departTable)
    postJSON("/pattern/#{patternId}/train", data)

  $('button.delete-train').each ->
    $(@).click ->
      trainId = parseInt($(@).attr('data-train-id'))
      if window.confirm("Train (id =#{trainId}) を削除します。よろしいですか？")
        ajaxDelete("/train/#{trainId}")
