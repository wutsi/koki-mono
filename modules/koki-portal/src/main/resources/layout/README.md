This directory contains the files to define the layout of the application.

## Main Layout

The main layout is described in the file ``__main.json``.

This file contains the following information:

- ``tabs``: List of modules in their order of display in the application menu.

__Example__:

```json
{
    "tabs": [
        "account",
        "contact",
        "employee",
        "form",
        "product",
        "tax",
        "invoice",
        "payment"
    ]
}
```

## Module Layout

The layout of each module details page is described in the files ``<MODULE-NAME>.json``.

The module details page can be accessed at the URL ``/<MODULE-NAME-IN-PLURAL>`` (Example: ``/accounts`` for
the ``account`` module)

Each module details page contains:

- Summary information about the module object
- A ``Detail`` tab containing the detailed information about the the module object
- A list of tabs of the associated module. The tabs are organized in 2 sections: main section and side section

Each module file contains the following information

- ``tabs/main``: List of modules to display in the main section of the page
- ``tabs/side``: List of modules to display in the side section of the page

__Example__:

```json
{
    "tabs": {
        "main": [
            "contact",
            "file",
            "tax",
            "invoice"
        ],
        "side": [
            "note",
            "email"
        ]
    }
}
```
