# Tenant Module

This module manages information about customers environment in multi-tenant environment.

## Class Structure

<img src="https://www.plantuml.com/plantuml/png/bLDTYzim47pthn1vBEGFcECu9r6BgUCKnsdxgfFtKe4i1NtS4Kh_UtVjOEIGEovvYCOpcjtHodxoGRWGUvs1YJrhmGWJza64wFzcZ1tgx_NkLuseMBVbJuvWVzZ_uFM6Rx9_sMWwU72Vj33MyBPi4dCHWbFFCK1xFW6R5sZvxnQNQbTV4DyMPKLR6lgl3ziLRn1i-BhS5XMXekLaBBUqh7QxYXTKwMjP4O6xld5gBRNmVkezzbLTpYfhZWYDw25dUrI7nu3HydbIP8KCwXNvbRKQX456YsVGYQy3Bvqw1MLDmie_Ladms4pvy33IZyGx406w8kHiWm2PtdRgHINKb9n6VfKPUg7IvYVSzsPTb-PH_gJ5kPwVpWzNboVNU6UOyQNvU8ITnNZcghECpe6HvuJILWfzAztOv7Q-DCpQcXTrZ4xGrA_lzLNec1Q__vJ3u1bBotvwWbWcLFQep7l56wlXmp7UE3R-HZDRBX_PuijYFe6BICAWspS3RZ4adOmFIv8l6LA5y22HDHKeADgkVOawsIvobirYJVBqKMLFOBhOw_y0">

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


