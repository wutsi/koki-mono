INSERT INTO T_MODULE(id, object_type, name, title, home_url, tab_url, settings_url, js_url)
    VALUES (210, 0, 'ai', 'AI', null, null, '/settings/ai', null);

INSERT INTO T_PERMISSION(id, module_fk, name, description)
    VALUES (2100, 200, 'ai:admin',  'Configure AI module');
