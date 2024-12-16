# Activity Types

## User

A User Activity is executed by a person, with the assistance of a BPMS.
Examples of User Activities are the following:

- A customer fulfils an online registration form
- A help-desk employee marks an open issue as resolved
- An editor approves an article for publishing

In Koki, User Activity are used by Users to input information using a Forms. After the Form is submitted, the Activity
is completed.

### Parameters for User Activity

| Name | Required | Description                                    |
|------|----------|------------------------------------------------|
| form | Y        | Name of the form                               |
| role | Y        | Role of the user who will perform the activity |

## Manual

A Manual Activity requires human involvement to complete. But in contrast to a User Activity, it is expected to be
performed without the aid the BPMS.
Examples of Manual Activities are as follows:

- A telephone technician installs a telephone at a customer location
- A help desk issue is resolved via telephone call
- A software engineer implements an open issue

-----

## Send

A Send Activity sends messages to participants. Once the Message has been sent, the Activity is completed.
Examples of a Send Activities are as follows:

- Send a email to a collaborator to inform that the process is finished
- When the purchase is about to be delivered, send a SMS to the customer
- When the article is published, send a slack message to inform the author

In Koki, Send Activities use Email, SMS or Messaging app (Slack, WhatsApp etc.) for delivering the messages.

### Parameters for Send Activity

| Name    | Required | Description                          |
|---------|----------|--------------------------------------|
| role    | Y        | Role of the recipient of the message |
| message | Y        | Name of the message to send          |

-----

## Service

As opposed to the User Activity and Manual Activity, a Service Activity does not require any human interaction.
It is completed automatically, by using some sort of an external service , which could be a Web service or
an automated application.
Examples of a Service Task are as follows:

- A payment processed by PayPal services
- Storing an image in an online archive
- Converting a price into a specific currency using an online currency converter.

Koki will provide the ability to send/receive message to external web services (JSON/XML).

### Parameters for Service

| Name                | Required | Description                           |
|---------------------|----------|---------------------------------------|
| service             | Y        | Name of the service to call           |
| service.contentType | Y        | `application/xml` or `text/xml`       |
| service.request     |          | Request to send in JSON or XML format |

-----

## Script

Another type of automated Activity is the Script Activity.
In contrast to the Service Task, a Script Activity is executed by a business process engine.
Technically, a Script Activity represents code, which can be executed on a process engine.
The modeler or implementer defines the Task’s script in a language that the engine can interpret.
When the Activity is ready to start, the engine will execute the script. When the script is
completed, the Activity will also be completed.

Examples of a Script Task are as follows:

- Receive a new open help-desk issue and send to the operator,
- Calculate the total cost by summing product cost and shipping cost,
- Add a unique ID to a help-desk ticket

Koki will support the following language: `Javascript`, `Phython`

### Parameters for Script

| Name   | Required | Description                                                                    |
|--------|----------|--------------------------------------------------------------------------------|
| script | Y        | Name of the script to execute                                                  |
| input  |          | Mapping of the input data to send as input parameter when executing the script |
| output |          | Mapping of script execution output with the workflow instance state            |

-----

## Receive

The Receive Activity is the opposite of the Send Task.
It represents a simple Activity that is designed to wait for an event to arrive from an external Participant.
Once the Message has been received, the Task is completed.
Examples of a Receive Task are as follows:
• Wait for author’s approval before continuing with the publishing process
• Wait for customer information to resolve an open issue
• Begin with the delivery process as soon the shipping address is approved.

### Parameters for Receive

| Name              | Required | Description                                                           |
|-------------------|----------|-----------------------------------------------------------------------|
| event             | Y        | Name of the event to receive                                          |
| correlation_field | Y        | Correlation field in the event, that matches the activity instance ID |
| input             |          | Mapping between event and state                                       |

-----

## Business Rule

A Business Rule Activity provides a mechanism for a Process to provide input to a Business Rules Engine,
and to get the output of calculations that the Business Rules Engine might provide.
Examples of a Business Rule Task are as follows:

- Define the priority of an open help-desk issue according to the user type,
- Calculate the insurance cost according to the user’s profile,
- Send the appropriate product offer according to the user’s behaviour.
