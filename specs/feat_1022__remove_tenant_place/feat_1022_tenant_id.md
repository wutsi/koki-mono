Place domain has been associated with tenant.
We no longer want Place to be tenant-specific, as places can be shared across tenants.

## Changes Required

1. in the database:
    - Remove tenant column from the places table.
    - Remove the index on tenant column in the places table.
2. in the API:
    - Update the place endpoints so that they no longer need the tenant header
    - Update the services layer to remove any tenant-specific logic for places
    - Update the tests to reflect these changes (including in the SQL scripts used for testing)
3. In Portal and Admin:
    - Update the SDK configuration so that the place SDK used the rest instance without tenant context
    - Update the place fixtures
