{
  "name": "FRM-001",
  "title": "Incident Report",
  "description": "Use this form to report incident",
  "elements": [
    {
      "type": "SECTION",
      "title": "General Information",
      "description": "General information about the incident",
      "accessControl": {
        "viewerRoles": [
          "employee",
          "hr",
          "finance"
        ],
        "editorRoles": [
          "employee"
        ]
      },
      "elements": [
        {
          "name": "case_id",
          "type": "TEXT",
          "title": "ID of the incident",
          "description": "The ID is auto-calculated, it cannot be modified",
          "accessControl": {
            "viewerRoles": [
              "employee",
              "hr",
              "finance"
            ],
            "editorRoles": [
              "xxx"
            ]
          }
        },
        {
          "name": "incident_title",
          "type": "TEXT",
          "title": "Incident Title",
          "description": "Short description of the incident",
          "required": true,
          "maxLength": 100
        },
        {
          "name": "employee_name",
          "type": "TEXT",
          "title": "Employee Name",
          "required": true,
          "maxLength": 50
        },
        {
          "name": "employee_email",
          "type": "EMAIL",
          "title": "Employee Email",
          "required": true,
          "maxLength": 100
        },
        {
          "name": "employee_sex",
          "type": "CHECKBOXES",
          "title": "Employee Sex",
          "required": true,
          "options": [
            {
              "value": "M",
              "text": "Male"
            },
            {
              "value": "F",
              "text": "Female"
            }
          ]
        },
        {
          "name": "incident_date",
          "type": "DATE",
          "title": "Incident Date",
          "description": "Enter the date of the incident"
        },
        {
          "name": "incident_type",
          "type": "DROPDOWN",
          "title": "Incident Type",
          "description": "Select the type of incident",
          "otherOption": {
            "value": "other",
            "text": "Other"
          },
          "options": [
            {
              "value": "unsafe_act",
              "text": "Unsafe Act"
            },
            {
              "value": "minor_injury",
              "text": "Minor Injury"
            },
            {
              "value": "major_injury",
              "text": "Major Injury"
            },
            {
              "value": "fire_incident",
              "text": "Fire Incident"
            },
            {
              "value": "security_incident",
              "text": "Security Incident"
            },
            {
              "value": "harassement",
              "text": "Harassement"
            }
          ]
        },
        {
          "name": "injury_type",
          "type": "MULTIPLE_CHOICE",
          "title": "Type of injury",
          "options": [
            {
              "value": "burns",
              "text": "Burns"
            },
            {
              "value": "fracture",
              "text": "Fractures"
            },
            {
              "value": "sprain",
              "text": "Sprain"
            },
            {
              "value": "bruise",
              "text": "Bruises"
            }
          ]
        },
        {
          "name": "description",
          "type": "PARAGRAPH",
          "title": "Incident Description",
          "description": "Enter all the details about the incident. Please add as many information as possible to help us to process the case quickly",
          "required": true
        },
        {
          "name": "doctor_note",
          "type": "FILE_UPLOAD",
          "title": "Doctor Note",
          "description": "Upload the note from the doctor to prove your absence",
          "required": true
        },
        {
          "name": "prescription",
          "type": "FILE_UPLOAD",
          "title": "Prescription",
          "description": "Upload the prescription to prove your expense",
          "required": false
        }
      ]
    },
    {
      "type": "SECTION",
      "title": "HR Approval",
      "description": "This section is reserved to Human Resource for approving the claim",
      "accessControl": {
        "viewerRoles": [
          "hr",
          "finance"
        ],
        "editorRoles": [
          "hr"
        ]
      },
      "elements": [
        {
          "name": "hr_approval",
          "type": "CHECKBOXES",
          "title": "Approval",
          "required": true,
          "options": [
            {
              "value": "approved",
              "text": "Approved"
            },
            {
              "value": "rejected",
              "text": "Rejected"
            }
          ]
        },
        {
          "name": "hr_comment",
          "type": "PARAGRAPH",
          "title": "Justification",
          "required": true,
          "description": "Justify your decision"
        },
        {
          "name": "reimbursement_amount",
          "type": "NUMBER",
          "title": "Reimbursement amount",
          "required": true,
          "description": "Enter the payment to reimburse to the employee, for 0 if you reject the claim"
        }
      ]
    },
    {
      "type": "SECTION",
      "title": "Payment Information",
      "description": "This section is reserved to Finance team",
      "accessControl": {
        "viewerRoles": [
          "finance"
        ],
        "editorRoles": [
          "finance"
        ]
      },
      "elements": [
        {
          "name": "payment_date",
          "type": "DATE",
          "title": "Payment Date",
          "required": true
        },
        {
          "name": "transaction_number",
          "type": "TEXT",
          "title": "Transaction Number",
          "required": true
        }
      ]
    }
  ]
}
