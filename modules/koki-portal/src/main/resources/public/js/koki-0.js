/**
 * Load more.html data
 * @param containerId ID of the container
 */
function koki_load_more(containerId) {
    const container = document.querySelector('#' + containerId);
    const button = container.querySelector('a');
    const url = button.getAttribute('data-url');

    container.innerHTML = '<div class="text-center"><i class="fas fa-spinner fa-spin"></div>';
    fetch(url).then(function (response) {
        response.text().then(function (html) {
            console.log('Replacing #' + containerId + ' with ', html);
            $('#' + containerId).replaceWith(html);
        });
    });
}

/**
 * Filter rows from a table
 * @param inputId - ID of the search input
 * @param tableId - ID of the table
 * @param columnIndex - ID of the colum where to search
 */
function koki_table_filter(inputId, tableId, columnIndex) {
    // Declare variables
    const filter = document.getElementById(inputId).value.toUpperCase();
    const table = document.getElementById(tableId);
    const tr = table.getElementsByTagName("tr");

    for (var i = 0; i < tr.length; i++) {
        var td = tr[i].getElementsByTagName("td")[columnIndex];
        if (td) {
            var txtValue = td.textContent || td.innerText;
            if (txtValue.toUpperCase().indexOf(filter) > -1) {
                tr[i].style.display = "";
            } else {
                tr[i].style.display = "none";
            }
        }
    }
}

