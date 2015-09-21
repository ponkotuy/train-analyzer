
@postJSON = (url, data) ->
  $.ajax({
    type: 'POST',
    url: url,
    data: JSON.stringify(data)
    contentType: 'application/json'
    dataType: 'JSON',
  }).done -> location.reload(true)
