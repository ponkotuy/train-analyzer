
@postJSON = (url, data) ->
  $.ajax(
    type: 'POST'
    url: url
    data: JSON.stringify(data)
    contentType: 'application/json'
    dataType: 'JSON'
    complete: -> location.reload(true)
  )

@ajaxDelete = (url) ->
  $.ajax(
    type: 'DELETE'
    url: url
    complete: -> location.reload(true)
  )
