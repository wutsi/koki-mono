INSERT T_MODULE(id, name, title, description, home_url, tab_url, settings_url, js_url, css_url)
    VALUES
        (100, 'MODULE1', 'Module 1', 'This is a module', '/module1', '/module1/tab', '/settings/module1', '/js/module1.js', '/css/module1.css');

INSERT T_PERMISSION(id, module_fk, name, description)
    VALUES
        (101, 100, 'module1:read', 'Read data'),
        (103, 100, 'module1:status', null);
