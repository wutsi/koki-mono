INSERT INTO T_MODULE(id, object_type, name, title, home_url, tab_url, settings_url, js_url)
    VALUES (150, 5, 'email', 'Emails', null, '/emails/tab', '/settings/email', '/js/emails.js');

INSERT INTO T_PERMISSION(id, module_fk, name, description)
    VALUES (1500, 150, 'email:admin',  'Configure email module');
