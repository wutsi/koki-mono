# Tenant Module

This module manages information about customers environment in multi-tenant environment.

## Class Structure

<img src="https://www.plantuml.com/plantuml/png/dLB1Rjim3BthAuGS1-G7ZAAeqsY3CKSf7AVRLPMvJe0i1HBT8XlsxwCSmr22j2YQGqo_nqS-ahwBg08ElMKCtD3p5fnok4E5G_pBED_BxtBxGr9KthVLew1Wjzyz2BaMQ_Pl4kqZX0zAE6z4MpMPk4GCvcb0Q8y7uES5Ml6pfKUzbLCaDcLLfvGc_SlzPYKQ2jRYljgKTOhALYHXjKcFrNPRYp9L-bhL2Q2iRoArrbR5EFS-zJLTmMlldYbqgeU2xuWTNwrw0fkzTn1rC0SqtcMe_ksDXaX5JCIR4tnBED9WnVcSAJk0GkXABFXQOKAKHlD2cIllBIWtsnptUs4GUcLoHmVAU_MXoqsQUB3gACz7YUETYkncLxtqlZE_J0RDPavVoFlxwZpPSnUTzH02E7tC83Gz_F4EgjotzLhPAvQdpc-Pld9L-VXmUTWNPOUizYSc7u_1ULRsurkbCbZxP-FUAjvu2--izkhKz3k9-N9vonTV5cSBcBY4ZxH_TH0Mez5PTxDCz6GXPr8yKacQ4yadoIvr6JlB9fgntDNCpY1ZT-2weRV_0G00">

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


