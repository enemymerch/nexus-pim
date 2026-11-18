import type {Resource} from '../lib/contracts';
export default {
  "id": "search",
  "title": "Search",
  "service": "search",
  "subtitle": "Find the right products",
  "description": "Search the Elasticsearch projection. Existing catalog products appear after indexing; an empty result does not mean the catalog is empty.",
  "operations": [
    {
      "id": "search",
      "label": "Search products",
      "method": "GET",
      "path": "/api/search/products",
      "fields": [
        {
          "key": "q",
          "label": "Search terms",
          "location": "query",
          "kind": "text"
        },
        {
          "key": "categoryCode",
          "label": "Category code",
          "location": "query",
          "kind": "text"
        },
        {
          "key": "status",
          "label": "Status",
          "location": "query",
          "kind": "text",
          "choices": [
            "",
            "DRAFT",
            "IN_REVIEW",
            "APPROVED",
            "PUBLISHED",
            "ARCHIVED"
          ]
        },
        {
          "key": "limit",
          "label": "Result limit",
          "location": "query",
          "kind": "number",
          "initial": "20"
        }
      ]
    },
    {
      "id": "find",
      "label": "Find indexed product",
      "method": "GET",
      "path": "/api/search/products/{productCode}",
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
      "id": "reindex",
      "label": "Reindex product",
      "method": "POST",
      "path": "/api/search/products/{productCode}/reindex",
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
  ],
  "auto": "search"
} satisfies Resource;
