# Tenant Module

This module manages information about customers environment in multi-tenant environment.

## Class Structure

<img src="https://www.plantuml.com/plantuml/png/bL9DRzim3BthLmWz5iWVC8gYJgmLnXndS9njLrLcEm6o5EYZGp3ilu-Csq0EZ6AzMFGZ7z-Zg8SGfOzfD0ni6daFLjguZpAcy8TnVcY_jhiVBKRbfg-_2mpsX_qtqLQYOd_PH3e4y4iKAglRnsKovvtewovhKyRezLEAq9-EmEUjUl6pnwFPjM-Hs9PrGoKTVTl3TYqw32gngRSbGLNP2oBMMphMkrqZIkhqfMu8mAf7GTBAo10ksfEk7WhUEFk2ePKZ57oFsVDlH6l5t2cYKaNzYlZQEGFI8cBa4vYCDq1GNX-ZTZP3rI-d5GGKqo7UJV0zuHvaXA6C1QymG6HqWtxM6NHnJfT_vHb6gNFn8zRzTdx8_UXmDFBKpgSBvoKMsK8_QMPwFXzVuU3GdhrILibxiEgKGSOfQPRSJIBB_d8p6sUVzKloacxzUg-lqgIi-QUcx9o1vHa_NlVrC_bVLKQ2_ERsXgzMz_njViQcap3ApSTzJzwjA7TUA6FqFV_UldEeCMCpvaNdyiZP0zWXZUOV">

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


