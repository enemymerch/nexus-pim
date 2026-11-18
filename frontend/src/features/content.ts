import type {Resource} from '../lib/contracts';
export default {
  "id": "content",
  "title": "Content",
  "service": "content",
  "subtitle": "Tell a consistent product story",
  "description": "Maintain localized content, channel overrides and resolved product descriptions.",
  "operations": [
    {
      "id": "find",
      "label": "Find product content",
      "method": "GET",
      "path": "/api/content/products/{productCode}",
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
      "id": "initialize",
      "label": "Initialize content",
      "method": "POST",
      "path": "/api/content/products/{productCode}",
      "fields": [
        {
          "key": "productCode",
          "label": "Product code",
          "location": "path",
          "kind": "text",
          "required": true
        },
        {
          "key": "defaultLocale",
          "label": "Default locale",
          "location": "body",
          "kind": "text",
          "required": true,
          "initial": "en-us"
        },
        {
          "key": "title",
          "label": "Title",
          "location": "body",
          "kind": "text",
          "required": true
        },
        {
          "key": "shortDescription",
          "label": "Short description",
          "location": "body",
          "kind": "text",
          "nullable": true
        },
        {
          "key": "description",
          "label": "Description",
          "location": "body",
          "kind": "text",
          "nullable": true
        },
        {
          "key": "bullets",
          "label": "Bullet points",
          "location": "body",
          "kind": "json",
          "initial": "[]",
          "help": "A JSON array of strings."
        }
      ]
    },
    {
      "id": "locale",
      "label": "Update locale",
      "method": "PUT",
      "path": "/api/content/products/{productCode}/locales/{locale}",
      "fields": [
        {
          "key": "productCode",
          "label": "Product code",
          "location": "path",
          "kind": "text",
          "required": true
        },
        {
          "key": "locale",
          "label": "Locale",
          "location": "path",
          "kind": "text",
          "required": true
        },
        {
          "key": "title",
          "label": "Title",
          "location": "body",
          "kind": "text",
          "required": true
        },
        {
          "key": "shortDescription",
          "label": "Short description",
          "location": "body",
          "kind": "text",
          "nullable": true
        },
        {
          "key": "description",
          "label": "Description",
          "location": "body",
          "kind": "text",
          "nullable": true
        },
        {
          "key": "bullets",
          "label": "Bullet points",
          "location": "body",
          "kind": "json",
          "initial": "[]",
          "help": "A JSON array of strings."
        }
      ]
    },
    {
      "id": "channel",
      "label": "Update channel override",
      "method": "PUT",
      "path": "/api/content/products/{productCode}/locales/{locale}/channels/{channel}",
      "fields": [
        {
          "key": "productCode",
          "label": "Product code",
          "location": "path",
          "kind": "text",
          "required": true
        },
        {
          "key": "locale",
          "label": "Locale",
          "location": "path",
          "kind": "text",
          "required": true
        },
        {
          "key": "channel",
          "label": "Channel",
          "location": "path",
          "kind": "text",
          "required": true
        },
        {
          "key": "title",
          "label": "Title",
          "location": "body",
          "kind": "text",
          "required": true
        },
        {
          "key": "shortDescription",
          "label": "Short description",
          "location": "body",
          "kind": "text",
          "nullable": true
        },
        {
          "key": "description",
          "label": "Description",
          "location": "body",
          "kind": "text",
          "nullable": true
        },
        {
          "key": "bullets",
          "label": "Bullet points",
          "location": "body",
          "kind": "json",
          "initial": "[]",
          "help": "A JSON array of strings."
        }
      ]
    },
    {
      "id": "resolve",
      "label": "Resolve content",
      "method": "GET",
      "path": "/api/content/products/{productCode}/resolve",
      "fields": [
        {
          "key": "productCode",
          "label": "Product code",
          "location": "path",
          "kind": "text",
          "required": true
        },
        {
          "key": "locale",
          "label": "Locale",
          "location": "query",
          "kind": "text",
          "required": true,
          "initial": "en-us"
        },
        {
          "key": "channel",
          "label": "Channel",
          "location": "query",
          "kind": "text"
        }
      ]
    }
  ]
} satisfies Resource;
