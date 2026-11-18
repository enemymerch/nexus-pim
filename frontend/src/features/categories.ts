import type {Resource} from '../lib/contracts';
export default {
  "id": "categories",
  "title": "Categories",
  "service": "taxonomy",
  "subtitle": "Organize your catalog",
  "description": "Find categories by code, maintain hierarchy and assign attribute sets. The current API does not offer category listing or deletion.",
  "operations": [
    {
      "id": "find",
      "label": "Find category",
      "method": "GET",
      "path": "/api/taxonomy/categories/{categoryCode}",
      "fields": [
        {
          "key": "categoryCode",
          "label": "Category code",
          "location": "path",
          "kind": "text",
          "required": true
        }
      ]
    },
    {
      "id": "create",
      "label": "Create category",
      "method": "POST",
      "path": "/api/taxonomy/categories",
      "fields": [
        {
          "key": "code",
          "label": "Category code",
          "location": "body",
          "kind": "text",
          "required": true
        },
        {
          "key": "name",
          "label": "Category name",
          "location": "body",
          "kind": "text",
          "required": true
        },
        {
          "key": "parentCode",
          "label": "Parent code",
          "location": "body",
          "kind": "text",
          "nullable": true,
          "help": "Leave blank for a root category."
        }
      ]
    },
    {
      "id": "update",
      "label": "Update category",
      "method": "PUT",
      "path": "/api/taxonomy/categories/{categoryCode}",
      "fields": [
        {
          "key": "categoryCode",
          "label": "Category code",
          "location": "path",
          "kind": "text",
          "required": true
        },
        {
          "key": "name",
          "label": "Category name",
          "location": "body",
          "kind": "text",
          "required": true
        },
        {
          "key": "parentCode",
          "label": "Parent code",
          "location": "body",
          "kind": "text",
          "nullable": true
        },
        {
          "key": "updateParent",
          "label": "Update parent",
          "location": "body",
          "kind": "boolean",
          "initial": "true"
        }
      ]
    },
    {
      "id": "sets",
      "label": "Assign attribute sets",
      "method": "PUT",
      "path": "/api/taxonomy/categories/{categoryCode}/attribute-sets",
      "fields": [
        {
          "key": "categoryCode",
          "label": "Category code",
          "location": "path",
          "kind": "text",
          "required": true
        },
        {
          "key": "attributeSetCodes",
          "label": "Attribute set codes",
          "location": "body",
          "kind": "array",
          "initial": "",
          "help": "Comma-separated codes. Leave blank to clear assignments."
        }
      ]
    },
    {
      "id": "effective",
      "label": "Inherited attribute sets",
      "method": "GET",
      "path": "/api/taxonomy/categories/{categoryCode}/effective-attribute-sets",
      "fields": [
        {
          "key": "categoryCode",
          "label": "Category code",
          "location": "path",
          "kind": "text",
          "required": true
        }
      ]
    },
    {
      "id": "metadata",
      "label": "Category metadata",
      "method": "GET",
      "path": "/api/taxonomy/categories/{categoryCode}/metadata",
      "fields": [
        {
          "key": "categoryCode",
          "label": "Category code",
          "location": "path",
          "kind": "text",
          "required": true
        }
      ]
    }
  ]
} satisfies Resource;
