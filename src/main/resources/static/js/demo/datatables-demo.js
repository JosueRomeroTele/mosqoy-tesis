// Call the dataTables jQuery plugin
$(document).ready(function() {
  $('#dataTable').DataTable({
   
  });
});

$(document).ready(function() {
  $('#dataTableActivity').DataTable({
    "order": [[ 0, 'desc' ]]
  });
});

/*
$(document).ready(function() {
  $('a[data-toggle="tab"]').on( 'shown.bs.tab', function (e) {
    $.fn.dataTable.tables( {visible: true, api: true} ).columns.adjust();
  } );

  $('table.table').DataTable( {
    ajax:           '../ajax/data/arrays.txt',
    scrollY:        200,
    scrollCollapse: true,
    paging:         false
  } );

  // Apply a search to the second table for the demo
  $('#myTable2').DataTable().search( 'New York' ).draw();
} );*/