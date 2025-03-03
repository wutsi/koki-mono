# Features

## Account

- [] List account invoices
- [] Add AccountUser/AccountUserRole to manage relation ship between Account and User per module. Ex: Accountant,
  Technician
- [] Settings: Import Accounts from CSV
- [] Integrate [int-tel](https://intl-tel-input.com/) for phone number input
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

- [] Integrate [int-tel](https://intl-tel-input.com/) for phone number input
- [x] Business entity
- [x] Settings: Setup business information

## Contact

- [] Add additional information: address, SSN, Date of birth
- [] Integrate [int-tel](https://intl-tel-input.com/) for phone number input
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
- [] Attach multiple files to email
- [x] Configure validation on DTO
- [x] Show email summary
- [x] Apply RBAC security on UI
- [x] Attach 1 file to email
- [x] Settings: Configure email layout
- [x] Settings: Configure SMTP
- [x] Send email API
- [x] Send email UI

## Employees

- [] Send email to employee
- [] Integrate [int-tel](https://intl-tel-input.com/) for phone number input
- [x] Employee API
- [x] Link Employee with User
- [x] Employee UI

## Files

- [] Integrate AI to describe the file
- [] Tag the files
- [x] Settings: Configure storage type: S3, Local
- [x] Apply RBAC security on UI
- [x] Upload file from popup
- [x] Show file details
- [x] Delete files
- [x] Download files
- [x] Upload files
- [x] Show file icons

## Invoicing

- [+] Setting: Configure payment terms
- [+] Setting: Select invoice format
- [] Send invoice by email when approved
- [] Send invoice by email when paid
- [] Setting: Configure email content
- [] Setting: Configure initial invoice number
- [] Payment API
- [] Capture payments manually: Cash, Check etc...
- [] Capture online payments:
    - [] Paypal
    - [] Stripe
    - [] Flutterwave
- [x] Invoice API
- [x] Invoice UI
- [x] Generate Invoice PDF

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

## Product

- [] Integrate Offer API
- [] Add attributes for Digital service
- [] Add attributes for Physical service
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

- [] Settings: Setup email notification
- [] Taxes KPI
    - [] Time spent by Tax report
    - [] Time spent on Tax report by user
    - [] Labor cost by Tax report
    - [] Total time spent on Tax Report per user
- [] Add Kanban view
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

## Tenant

- [] Tenant should have a list of currencies
- [] Init a tenant

## Users

- [] Settings: Import users from CSV
- [] Settings: Update my password
- [] Settings: Change password of user
- [x] Settings: Filter user by role, status
- [x] Set roles on create/update users
- [x] Set permission on create/update roles
- [x] Apply RBAC security on UI
- [x] Manage users
- [x] Manage roles

## Web

- [] serve all static resource from external site
