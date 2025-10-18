

INSERT INTO T_MODULE(id, object_type, name, title, home_url, tab_url, settings_url, js_url, css_url)
    VALUES (290, 17, 'agent', 'Agents', '/agents', null, null, null, '/css/agents.css');

INSERT INTO T_PERMISSION(id, module_fk, name, description)
    VALUES (2900, 290, 'agent',             'View Agents');
