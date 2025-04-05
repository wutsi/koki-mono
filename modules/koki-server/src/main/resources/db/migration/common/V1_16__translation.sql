INSERT INTO T_MODULE(id, object_type, name, title, home_url, tab_url, settings_url, js_url)
    VALUES (230, 0, 'translation', 'Translation', null, null, '/settings/translations', null);

INSERT INTO T_PERMISSION(id, module_fk, name, description)
    VALUES (2300, 230, 'translation:admin',  'Configure translation');
