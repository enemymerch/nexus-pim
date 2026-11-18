import type {Resource} from '../lib/contracts';
export default {
  "id": "products",
  "title": "Products",
  "service": "catalog",
  "subtitle": "Your product source of truth",
  "description": "Create products and variants, or find a product by its exact code. Listing, editing and deleting products are not exposed by the current catalog API.",
  "operations": [
    {
      "id": "find",
      "label": "Find product",
      "method": "GET",
      "path": "/api/catalog/products/{productCode}",
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
      "id": "create",
      "label": "Create product",
      "method": "POST",
      "path": "/api/catalog/products",
      "fields": [
        {
          "key": "code",
          "label": "Product code",
          "location": "body",
          "kind": "text",
          "required": true
        },
        {
          "key": "name",
          "label": "Product name",
          "location": "body",
          "kind": "text",
          "required": true
        },
        {
          "key": "categoryCode",
          "label": "Category code",
          "location": "body",
          "kind": "text",
          "required": true
        },
        {
          "key": "variantAttributeCodes",
          "label": "Variant attribute codes",
          "location": "body",
          "kind": "array",
          "required": true,
          "help": "Comma-separated codes, for example color, size"
        }
      ]
    },
    {
      "id": "variant",
      "label": "Add variant",
      "method": "POST",
      "path": "/api/catalog/products/{productCode}/variants",
      "fields": [
        {
          "key": "productCode",
          "label": "Product code",
          "location": "path",
          "kind": "text",
          "required": true
        },
        {
          "key": "code",
          "label": "Variant code",
          "location": "body",
          "kind": "text",
          "required": true
        },
        {
          "key": "active",
          "label": "Active",
          "location": "body",
          "kind": "boolean",
          "initial": "true"
        },
        {
          "key": "barcode",
          "label": "Barcode",
          "location": "body",
          "kind": "text",
          "nullable": true
        },
        {
          "key": "options",
          "label": "Variant options",
          "location": "body",
          "kind": "json",
          "required": true,
          "initial": "[{\"attributeCode\":\"color\",\"attributeValueCode\":\"red\"}]",
          "help": "Each option contains attributeCode and attributeValueCode."
        }
      ]
    }
  ]
} satisfies Resource;
