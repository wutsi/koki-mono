INSERT INTO T_TENANT(id, status, name, domain_name, locale, number_format, currency, currency_symbol, monetary_format, date_format, time_format, date_time_format, logo_url, icon_url, portal_url)
    VALUES
        (1, 1, 'test', 'localhost', 'fr_FR', '#,###,###',    'CAD', 'CA$',  'CA$ #,###,###.#0', 'yyyy-MM-dd', 'hh:mm a', 'yyyy-MM-dd hh:mm a', 'https://prod-wutsi.s3.amazonaws.com/static/wutsi-blog-web/assets/wutsi/img/logo/name-104x50.png', 'https://prod-wutsi.s3.amazonaws.com/static/wutsi-blog-web/assets/wutsi/img/logo/logo_512x512.png', 'http://localhost:8081');

INSERT INTO T_CONFIGURATION(tenant_fk, name, value)
    VALUES (1, 'email.decorator',   '<table> <tr><td>{{tenant_name}}</td></tr> <tr><td>{{{body}}}</td></tr> </table>');

INSERT INTO T_FILE(id, tenant_fk, created_by_fk, name, content_type, content_length, url)
    VALUES (100, 1, 11, 'image.png', 'image/ong',       1000, 'https://picsum.photos/200/300'),
           (101, 1, 11, 'bar.pdf',   'application/pdf', 1000, 'https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf');

