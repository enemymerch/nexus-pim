import type {Resource} from '../lib/contracts';
export default {
  "id": "attributes",
  "title": "Attributes",
  "service": "attribute",
  "subtitle": "Define what makes a product",
  "description": "Browse attribute definitions and create the fields your products need. Attribute changes and deletion are not available in the backend yet.",
  "operations": [
    {
      "id": "list",
      "label": "Browse attributes",
      "method": "GET",
      "path": "/api/attribute/attributes",
      "fields": [
        {
          "key": "scope",
          "label": "Scope",
          "location": "query",
          "kind": "text",
          "choices": [
            "",
            "PRODUCT",
            "VARIANT"
          ]
        }
      ]
    },
    {
      "id": "find",
      "label": "Find attribute",
      "method": "GET",
      "path": "/api/attribute/attributes/{code}",
      "fields": [
        {
          "key": "code",
          "label": "Attribute code",
          "location": "path",
          "kind": "text",
          "required": true
        }
      ]
    },
    {
      "id": "create",
      "label": "Create attribute",
      "method": "POST",
      "path": "/api/attribute/attributes",
      "fields": [
        {
          "key": "code",
          "label": "Attribute code",
          "location": "body",
          "kind": "text",
          "required": true
        },
        {
          "key": "dataType",
          "label": "Data type",
          "location": "body",
          "kind": "text",
          "required": true,
          "choices": [
            "STRING",
            "NUMBER",
            "BOOLEAN",
            "DATE",
            "ENUM",
            "JSON"
          ],
          "initial": "STRING"
        },
        {
          "key": "scope",
          "label": "Scope",
          "location": "body",
          "kind": "text",
          "required": true,
          "choices": [
            "PRODUCT",
            "VARIANT"
          ],
          "initial": "PRODUCT"
        },
        {
          "key": "localizable",
          "label": "Localizable",
          "location": "body",
          "kind": "boolean",
          "initial": "false"
        },
        {
          "key": "channelOverridable",
          "label": "Channel overrides",
          "location": "body",
          "kind": "boolean",
          "initial": "false"
        },
        {
          "key": "multiValue",
          "label": "Multiple values",
          "location": "body",
          "kind": "boolean",
          "initial": "false"
        }
      ]
    }
  ],
  "auto": "list"
} satisfies Resource;
