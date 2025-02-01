# Features

## Module

- [x] Module API
- [x] List of modules driven by backend in home page
- [x] List of modules driven by backend in configuration
- [x] link CSS and JS with modules

## Account

- [] The account manager must have the permission 'account:manage'
- [] Add additional information to accounts: address
- [] Add account filters
- [] Settings: Import Accounts from CSV
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

## Contact

- [] Add additional information: address, SSN, Date of birth
- [] Add contact filters
- [x] Configure validation on DTO
- [x] Apply RBAC security on UI
- [x] Settings: Import Contact Types from CSV
- [x] Settings: Import Contact from CSV
- [x] Send email to contact
- [x] Contact API
- [x] Contact UI
- [x] Link files to contacts
- [x] Add address to Contact

## Files

- [] Tag the files:
- [] Settings: Configure storage type: S3, Local
- [x] Apply RBAC security on UI
- [x] Upload file from popup
- [x] Show file details
- [x] Delete files
- [x] Download files
- [x] Upload files
- [x] Show file icons

## Taxes

- [] Add Kanban view
- [] Compile time spent by Tax report
- [] Compile time spent on Tax report by user
- [] Compile labor cost by Tax report
- [] Track total time spent on Tax Report per user
- [] Settings: Setup email notification
- [] Filter Accountant and Technician based on the permission 'tax:manage'
- [x] Add Calendar view
- [x] Apply RBAC security on UI
- [x] Settings: Import Taxes Types from CSV
- [x] Send email to Taxes
- [x] Home: Add my assignment widget
- [x] Personal tax UI
- [x] Personal tax API
- [x] Link files to taxes
- [x] Add a note

## Employees

- [x] Employee API
- [x] Link Employee with User
- [x] Employee UI

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

## Notes

- [x] Configure validation on DTO
- [x] Apply RBAC security on UI
- [x] Add type of notes
- [x] Tack time
- [x] Note API
- [x] Note UI

## Users

- [] Apply RBAC security on UI
- [] Settings: Update my password
- [] Settings: Filter user by role, status
- [] Settings: Change password of user
- [] Import users from CSV
- [x] Manage users
- [x] Manage roles

## Product

- [] Product API
- [] Product UI

---------

## Form

- [] Add support for Paypal field
- [] Add support for Stripe field
- [] Add checkbox grid field
- [] Add radio grid field
- [] Embed form
- [] Support customer form style
- [] Support anonymous forms
- [x] Show form in preview with all fields displayed
- [x] Support ReadOnly fields
- [x] Track Form submissions
- [x] Share Forms Link

## Workflow

- [] Approve/Reject activity
- [] Send email notification for approval
- [] Task approval should be only on USER or MANUAL tasks
- [] Cancel workflow Instance
- [] Run scheduled workflow instances
- [] Start Workflow on event received
- [] SEND activity to fire events
- [] Add support for jsonpath mapping
- [] Add support for Business Rule activities
- [x] Send email notification for each USER or MANUAL activity
- [x] add modifiedAt to Activity Instance. Use it as default sort field
- [x] Start Workflow on form submission
- [x] Add "recipient" field for SEND activity
- [x] Add support for Manual activities
- [x] Simplify tenant configuration API as Name/Value pair
- [x] Add message API
- [x] Add support for Send activities with Email
- [x] UI for managing messages
- [x] UI for managing forms
- [x] UI for configuring SMTP
- [x] Use RabbitMQ for workflow orchestration
- [x] Add Workflow Log
- [x] Add support for Script activities
- [x] Add support for Receive activities
- [x] Add support for Service activities
- [x] List workflow instances

## Portal

- [] Configure home page by role
- [x] Align webapp domain name with Tenant
