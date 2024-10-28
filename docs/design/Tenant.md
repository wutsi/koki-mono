# Tenant Design

The tenant module is managing information about customers environment in multi-tenant environment.

## Class Struture
<img src="https://www.plantuml.com/plantuml/png/VLDTYzim47pthn1vBEGFc5BEQTHYS7B5Tev2wOCgxrq5iXIizPMqzBztPUTZO_Ty3wVDQ6PsLjay15GTzgrD4d1zAsfmoc652llm9n4Y-rZdJvAAwb1zaVkjt2P_pzH3W6wHA4GfwxmS9Tgg40PDP9ic5OLtBrH2gunDHOMT6JuVYVVBTms3j7UeDEvL2mmDG--KvOZK3XH2as4gjbJSEbUdWD3E-iyLGX0V2_zY77Evs8plCx5mzy4QJoEvILhTTnquVM8GcXP-UmSvdzLwhUp_nXmxBmtA4sP8-XyzWfXaVLMsv_RpyLlVc6V3eCiNVlMiJyVHjvPVQphsXzr6bbJ8NPONT1xA-7-RrJAIybqyDe-FXSpsL7tAYmZak-oplELcYPTV5uBkp16DTtVei-ejFasd--cDXd11ldqd32bzUZ_CuZimI6aqhoJUU6z1kTbU3Eibn7t-vJrTtZfkNbBFvQUPxmJzZMAnUhSIw_K7iJgJDgSL-qrSMCI8pEaINLtUhzdr84nkzRK7luYfIJRLlNsR99FcLriMvm5SqxVs7m00">

### Attribute
`Attribute` describe a configuration parameter.

### Tenant
`Tenant` contains the information of a customer using the platform.
Tenant configuration is manages be `TenantAttribute`, that holds the value for the various configuration attributes

### User
`User` contains information for log into the application using email and password.
The user's status can be `ACTIVE`, `SUSPENDED`.
- `ACTIVE` indicates that the user can login a access the platform
- `SUSPENDED` indicates that the user has been suspended from platform

### TenantUser
`TenantUser` grant a user access to a tenant.
A tenant user's status can be `ACTIVE`, `SUSPENDED` or `RETIRED`.
- `ACTIVE` indicates that the user can access the tenant
- `SUSPENDED` indicates that the user has been suspended from the tenant
- `RETIRED` indicates that the user is no longer associated with the tenant. Ex: when employee leave a company

The `TenantRole` indicates its level of privilege within the tenant.

## Standard Attributes

### Tenant Attributes
| Attribute              | Description                          |
|------------------------|--------------------------------------|
| tenant.logo_url        | URL of the tenant logo               |
| tenant.website_url     | URL of the tenant website            |
| tenant.smtp.host       | SMTP host address, for sending email |
| tenant.smtp.username   | SMTP username                        |
| tenant.smtp.password   | SMTP password                        |
| tenant.smtp.port       | SMTP port                            |
| tenant.smtp.from       | Value of the SMTP header 'From'      |


