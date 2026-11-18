import type {Resource} from '../lib/contracts';
export default {
  "id": "attribute-sets",
  "title": "Attribute sets",
  "service": "attribute",
  "subtitle": "Build reusable field collections",
  "description": "Group attributes into reusable sets and define required fields, ordering and constraints.",
  "operations": [
    {
      "id": "find",
      "label": "Find attribute set",
      "method": "GET",
      "path": "/api/attribute/attribute-sets/{setCode}",
      "fields": [
        {
          "key": "setCode",
          "label": "Attribute set code",
          "location": "path",
          "kind": "text",
          "required": true
        }
      ]
    },
    {
      "id": "create",
      "label": "Create attribute set",
      "method": "POST",
      "path": "/api/attribute/attribute-sets",
      "fields": [
        {
          "key": "code",
          "label": "Set code",
          "location": "body",
          "kind": "text",
          "required": true
        },
        {
          "key": "name",
          "label": "Set name",
          "location": "body",
          "kind": "text",
          "required": true
        }
      ]
    },
    {
      "id": "group",
      "label": "Add group",
      "method": "POST",
      "path": "/api/attribute/attribute-sets/{setCode}/groups",
      "fields": [
        {
          "key": "setCode",
          "label": "Attribute set code",
          "location": "path",
          "kind": "text",
          "required": true
        },
        {
          "key": "code",
          "label": "Group code",
          "location": "body",
          "kind": "text",
          "required": true
        },
        {
          "key": "name",
          "label": "Group name",
          "location": "body",
          "kind": "text",
          "required": true
        },
        {
          "key": "sortOrder",
          "label": "Sort order",
          "location": "body",
          "kind": "number",
          "initial": "0"
        }
      ]
    },
    {
      "id": "attach",
      "label": "Attach attribute",
      "method": "PUT",
      "path": "/api/attribute/attribute-sets/{setCode}/items",
      "fields": [
        {
          "key": "setCode",
          "label": "Attribute set code",
          "location": "path",
          "kind": "text",
          "required": true
        },
        {
          "key": "attributeCode",
          "label": "Attribute code",
          "location": "body",
          "kind": "text",
          "required": true
        },
        {
          "key": "required",
          "label": "Required",
          "location": "body",
          "kind": "boolean",
          "initial": "false"
        },
        {
          "key": "sortOrder",
          "label": "Sort order",
          "location": "body",
          "kind": "number",
          "initial": "0"
        },
        {
          "key": "groupCode",
          "label": "Group code",
          "location": "body",
          "kind": "text",
          "nullable": true
        },
        {
          "key": "constraintsJson",
          "label": "Constraints (JSON string)",
          "location": "body",
          "kind": "text",
          "nullable": true
        }
      ]
    }
  ]
} satisfies Resource;
