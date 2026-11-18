import type {Resource} from '../lib/contracts';
export default {
  "id": "workflow",
  "title": "Workflow",
  "service": "workflow",
  "subtitle": "Move from draft to published",
  "description": "Start a product workflow, perform valid transitions and review the audit history. Available transitions depend on the current state.",
  "operations": [
    {
      "id": "find",
      "label": "Current workflow",
      "method": "GET",
      "path": "/api/workflow/products/{productCode}",
      "fields": [
        {
          "key": "productCode",
          "label": "Product code",
          "location": "path",
          "kind": "text",
          "required": true
        }
      ]
    },
    {
      "id": "history",
      "label": "Transition history",
      "method": "GET",
      "path": "/api/workflow/products/{productCode}/history",
      "fields": [
        {
          "key": "productCode",
          "label": "Product code",
          "location": "path",
          "kind": "text",
          "required": true
        }
      ]
    },
    {
      "id": "start",
      "label": "Start workflow",
      "method": "POST",
      "path": "/api/workflow/products/{productCode}/start",
      "fields": [
        {
          "key": "productCode",
          "label": "Product code",
          "location": "path",
          "kind": "text",
          "required": true
        },
        {
          "key": "actor",
          "label": "Actor",
          "location": "body",
          "kind": "text",
          "required": true
        }
      ]
    },
    {
      "id": "transition",
      "label": "Transition",
      "method": "POST",
      "path": "/api/workflow/products/{productCode}/transitions",
      "fields": [
        {
          "key": "productCode",
          "label": "Product code",
          "location": "path",
          "kind": "text",
          "required": true
        },
        {
          "key": "action",
          "label": "Action",
          "location": "body",
          "kind": "text",
          "required": true,
          "choices": [
            "SUBMIT",
            "APPROVE",
            "REJECT",
            "PUBLISH",
            "ARCHIVE",
            "REOPEN"
          ],
          "initial": "SUBMIT"
        },
        {
          "key": "actor",
          "label": "Actor",
          "location": "body",
          "kind": "text",
          "required": true
        },
        {
          "key": "comment",
          "label": "Comment",
          "location": "body",
          "kind": "text",
          "nullable": true
        }
      ]
    }
  ]
} satisfies Resource;
