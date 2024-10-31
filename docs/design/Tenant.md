# Tenant Module

This module manages information about customers environment in multi-tenant environment.

## Class Structure

<img src="https://www.plantuml.com/plantuml/png/bLBBRjim4BppAuWS0_W7X228RBE5K5akz6XxPQXDIe0Y1JvI64N_lLmfJYZ12EABE9hbS6QvV72UMny6HK27WRQWkVQDvpwuluJIhlfM7NzM4UMxjlZ18cYwvZkhzcnF_f5Pr3cmdvD4NBEsg1DnxhsLZy53Ung1BWzesQysBkMnUaNia1SbRgdnMtM7BQiZsBDTSSXBH7dBK5WSSDaUZoNByQGlHOb4tFMLeRLGtBatxzbNzXajZNwEKFC1Cjh4wlJhOxHicJIoN7Zv4lcjCGguZepYZw0INGzEM3bwQNJ2Yjz62d3HJ3f_DzFto5lW7lhSPtJFFJeCffTFCg7UakELhpB3m6LgFiPzVupjqppIZOgVgcLtRXfTbepnoZ3pe_du2diJu-cLimZMWXQdX596S7KftMnoELyQPcVqatmEbkEjh-VwmbLO1BYYozeekDZZn-DUFvDFkn8qf3UtDtIpkQUl_HCohyXXRTdkkNYtmTeqK4BmE_tUdZLuC24BvTdd_P6J1z1z6DH_">

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


