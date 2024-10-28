# Tenant Module

This module manages information about customers environment in multi-tenant environment.

## Class Structure

<img src="https://www.plantuml.com/plantuml/png/bL7TpjCm3BtlKqIxHDeBL9y-VTqQK4L_K7-0sz2Q4IbDfYJTD13lZjDkD2lRD7QndFhusCVscx5Csw4NGG1ow4aDaabRMMO7yoiWfCa_viMN756qhPFF547LL9ze7jCu-7qMDGRqan92IbedfIUEhDNyss2XFks1N1UewTSQdxJ8puXcKPAwbDBzvqsseIM2c6wJB4eTYchgX4dcdarHf3HobTudgGjWrWVgMhU26JFt7lloBYIfaZk4alKGaWhPyRFzeNWB1cbkxCiKViMuHSlXzGGkMmEpq4Ks9342ZB3MyWDcRfGImEHim6rkqHfwneNNUuzvHwKxBzHninVib5-RDEC5GkyQDxtqgkFV-HoQpKotVxo9Jg4zkUZS3bg3R4zUoF8UVYe9YUzRg9Q9MvQdpdTDBnpUlTg1YS4l___3Z_SWn2_xz6AnZ4tLZijxnKibuF5s_nqSVvEOhDUlPFLkjTp1XKPgp5172NerUlqhVLbxIfznUAISr2USLPGjTJvxaPrff_IDpGcprU0DP3VquWy0">

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

`TenantUser` grant a user access to a tenant's admin portal.
A tenant user's status can be `ACTIVE`, `SUSPENDED` or `RETIRED`.

- `ACTIVE` indicates that the user can access the tenant's admin portal.
- `SUSPENDED` indicates that the user has been suspended from the tenant's admin portal.
- `RETIRED` indicates that the user is no longer associated with the tenant. Ex: when employee leave a company

The `TenantRole` indicates its level of privilege within the tenant.

## Standard Attributes

### Tenant Attributes

| Attribute             | Description                          |
|-----------------------|--------------------------------------|
| tenant.logo_url       | URL of the tenant logo               |
| tenant.website_url    | URL of the tenant website            |
| tenant.aws.secret_key | AWS Secret key                       |
| tenant.aws.access_key | AWS Access key                       |
| tenant.smtp.host      | SMTP host address, for sending email |
| tenant.smtp.username  | SMTP username                        |
| tenant.smtp.password  | SMTP password                        |
| tenant.smtp.port      | SMTP port                            |
| tenant.smtp.from      | Value of the SMTP header 'From'      |


