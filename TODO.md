# Features

## Account

- [] Add AccountUser/AccountUserRole to manage relation ship between Account and User per module. Ex: Accountant,
  Technician
- [] Settings: Import Accounts from CSV
- [x] Add account required fields: email, postal-address, billing-same-as-shipping-address
- [x] List account invoices
- [x] Add additional information to accounts: address
- [x] The account manager must have the permission 'account:manage'
- [x] Add account filters
- [x] Configure validation on DTO
- [x] Apply RBAC security on UI
- [x] Settings: Import Attributes from CSV
- [x] Settings: Import Account Types from CSV
- [x] Send email to account
- [x] Link with taxes
- [x] Attributes API**
- [x] Account API
- [x] Account UI
- [x] BUG: We should not be able to delete Account with Contacts
- [x] Link files to account
- [x] Add note to account

## Business

- [] Record TaxIDs
- [x] Business entity
- [x] Settings: Setup business information

## Contact

- [] Add additional information: address, SSN, Date of birth
- [x] Add contact filters
- [x] Configure validation on DTO
- [x] Apply RBAC security on UI
- [x] Settings: Import Contact Types from CSV
- [x] Settings: Import Contact from CSV
- [x] Send email to contact
- [x] Contact API
- [x] Contact UI
- [x] Link files to contacts
- [x] Add address to Contact

## Email

- [] Auto-translate email
- [x] Add native SMTP configuration
- [x] Configure validation on DTO
- [x] Show email summary
- [x] Apply RBAC security on UI
- [x] Attach 1 file to email
- [x] Settings: Configure email layout
- [x] Settings: Configure SMTP
- [x] Send email API
- [x] Send email UI

## Employees

- [x] Employee API
- [x] Link Employee with User
- [x] Employee UI

## Files

- [] Add Metadata to files: id, tenant-id, owner-type, owner-id
- [x] Make S3 bucket non-public
- [x] Integrate AI to describe the file
- [x] Validate S3 configuration
- [x] Settings: Configure storage type: S3, Local
- [x] Apply RBAC security on UI
- [x] Upload file from popup
- [x] Show file details
- [x] Delete files
- [x] Download files
- [x] Upload files
- [x] Show file icons

## Form

- [x] Form API
- [x] Form UI

## Invoice

- [] Due Invoices Widget
- [x] Change URL of checkout page to "hide" the invoice ID
- [x] Add locale to invoice
- [x] Update amount due on successful payment
- [x] Download invoices
- [x] Send invoice manually via email
- [x] Send invoice by email when approved
- [x] Send invoice by email when paid
- [x] Setting: Configure payment terms
- [x] Setting: Select invoice format
- [x] Before sending email notification, make sure that event.status=invoice.status
- [x] Setting: Configure email content
- [x] Setting: Configure initial invoice number
- [x] Generate Invoice PDF
- [x] Invoice API
- [x] Invoice UI

## Module

- [x] Module API
- [x] List of modules driven by backend in home page
- [x] List of modules driven by backend in configuration
- [x] link CSS and JS with modules

## Notes

- [x] Configure validation on DTO
- [x] Apply RBAC security on UI
- [x] Add type of notes
- [x] Tack time
- [x] Note API
- [x] Note UI

## Platform

- [x] Add healthcheck for S3, RabbitMQ, Email

## Payment

- [] Capture online payments Paypal
- [] Settings: Validate Paypal configuration
- [] Capture online payments Flutterwave
- [] Settings: Validate Flutterwave configuration
- [x] Settings: Validate Stripe configuration
- [x] Record the check date
- [x] Send email notification on payment
- [x] Capture online payments Stripe
- [x] Settings: Configure the supported payment method
    - [x] Cash
    - [x] Check
    - [x] Interact
    - [x] Paypal
    - [x] Stripe
    - [x] Flutterwave
- [x] Payment API
- [x] Capture payments manually:
    - [x] Cash
    - [x] Check
    - [x] Interact

## Product

- [] Integrate Offer API
- [] Add attributes for Digital service
- [] Add attributes for Physical service
- [] Add Images to product
- [x] Set price on creation
- [x] Product Categories for services
- [x] Link with categories
- [x] Add attributes for Services
- [x] Product UI
- [x] Price UI
- [x] Product, Price API

## RefData

- [x] Add endpoint to import all ref data
- [x] Unit API
- [x] Location API
- [x] Juridiction API
- [x] Category API
- [x] Sales Tax API

## Taxes

- [] Widget: Tax Monthly stats
- [] Compile taxes monthly stats
- [] Add Kanban view
- [x] Add metric permission
- [x] Taxes Metrics
    - [x] Labor cost
    - [x] Labor hours
    - [x] Revenu
- [x] Send the form to customer when status=GATHERING_DOCUMENTS
- [x] Settings: Setup email notification
    - [x] When task assigned
    - [x] When starting document collection
    - [x] When done
- [x] Create Invoices
- [x] Link taxes with products
- [x] Filter Accountant and Technician based on the permission 'tax:manage'
- [x] Add Calendar view
- [x] Apply RBAC security on UI
- [x] Settings: Import Taxes Types from CSV
- [x] Send email to Taxes
- [x] Home: Add my assignment widget
- [x] Personal tax UI
- [x] Personal tax API
- [x] Link files to taxes
- [x] Add a note
- [x] List taxes invoices

## Tenant

- [] Add tenant healthcheck for S3, Email
- [x] Init a tenant

## Users

- [] Settings: Import users from CSV
- [] Settings: Update my password
- [x] Settings: Change password of user
- [x] Settings: Filter user by role, status
- [x] Set roles on create/update users
- [x] Set permission on create/update roles
- [x] Apply RBAC security on UI
- [x] Manage users
- [x] Manage roles

## Web

- [x] After user logs-out, he can access any page without login
- [x] Sort the tabs of the menu
- [] serve all static resource from external site
