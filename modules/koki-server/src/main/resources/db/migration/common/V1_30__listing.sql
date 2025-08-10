
INSERT INTO T_MODULE(id, object_type, name, title, home_url, tab_url, settings_url, js_url, css_url)
    VALUES (270, 15, 'listing', 'Listings', '/listings', null, null, '/js/listings.js', '/css/listings.css');


INSERT INTO T_PERMISSION(id, module_fk, name, description)
    VALUES (2700, 270, 'listing',             'View Listings'),
           (2701, 270, 'listing:manage',      'Add/Edit Listing'),
           (2702, 270, 'listing:full_access', 'Full access on all Listings');
