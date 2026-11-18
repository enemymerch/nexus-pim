import type {Resource} from '../lib/contracts';
export default {
  "id": "media",
  "title": "Media",
  "service": "media",
  "subtitle": "Assets, in context",
  "description": "Manage asset records and presentation. Upload initiation returns a development storage URL; binary storage is not configured by this UI.",
  "operations": [
    {
      "id": "list",
      "label": "Product assets",
      "method": "GET",
      "path": "/api/media/assets",
      "fields": [
        {
          "key": "productCode",
          "label": "Product code",
          "location": "query",
          "kind": "text",
          "required": true
        }
      ]
    },
    {
      "id": "find",
      "label": "Find asset",
      "method": "GET",
      "path": "/api/media/assets/{assetId}",
      "fields": [
        {
          "key": "assetId",
          "label": "Asset ID",
          "location": "path",
          "kind": "text",
          "required": true
        }
      ]
    },
    {
      "id": "upload",
      "label": "Initiate upload",
      "method": "POST",
      "path": "/api/media/assets/uploads",
      "fields": [
        {
          "key": "productCode",
          "label": "Product code",
          "location": "body",
          "kind": "text",
          "required": true
        },
        {
          "key": "variantCode",
          "label": "Variant code",
          "location": "body",
          "kind": "text",
          "nullable": true
        },
        {
          "key": "mediaType",
          "label": "Media type",
          "location": "body",
          "kind": "text",
          "required": true,
          "choices": [
            "IMAGE",
            "DOCUMENT",
            "VIDEO",
            "OTHER"
          ],
          "initial": "IMAGE"
        },
        {
          "key": "mimeType",
          "label": "MIME type",
          "location": "body",
          "kind": "text",
          "required": true,
          "initial": "image/jpeg"
        },
        {
          "key": "originalFilename",
          "label": "Original filename",
          "location": "body",
          "kind": "text",
          "required": true
        },
        {
          "key": "sizeBytes",
          "label": "Size in bytes",
          "location": "body",
          "kind": "number",
          "nullable": true
        },
        {
          "key": "role",
          "label": "Role",
          "location": "body",
          "kind": "text",
          "required": true,
          "choices": [
            "MAIN",
            "GALLERY",
            "MANUAL",
            "SWATCH",
            "OTHER"
          ],
          "initial": "MAIN"
        },
        {
          "key": "locale",
          "label": "Locale",
          "location": "body",
          "kind": "text",
          "nullable": true
        },
        {
          "key": "channel",
          "label": "Channel",
          "location": "body",
          "kind": "text",
          "nullable": true
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
      "id": "complete",
      "label": "Complete upload",
      "method": "POST",
      "path": "/api/media/assets/{assetId}/complete",
      "fields": [
        {
          "key": "assetId",
          "label": "Asset ID",
          "location": "path",
          "kind": "text",
          "required": true
        },
        {
          "key": "sizeBytes",
          "label": "Size in bytes",
          "location": "body",
          "kind": "number",
          "nullable": true
        },
        {
          "key": "checksumSha256",
          "label": "SHA-256 checksum",
          "location": "body",
          "kind": "text",
          "nullable": true
        }
      ]
    },
    {
      "id": "presentation",
      "label": "Update presentation",
      "method": "PATCH",
      "path": "/api/media/assets/{assetId}/presentation",
      "fields": [
        {
          "key": "assetId",
          "label": "Asset ID",
          "location": "path",
          "kind": "text",
          "required": true
        },
        {
          "key": "role",
          "label": "Role",
          "location": "body",
          "kind": "text",
          "required": true,
          "choices": [
            "MAIN",
            "GALLERY",
            "MANUAL",
            "SWATCH",
            "OTHER"
          ],
          "initial": "MAIN"
        },
        {
          "key": "locale",
          "label": "Locale",
          "location": "body",
          "kind": "text",
          "nullable": true
        },
        {
          "key": "channel",
          "label": "Channel",
          "location": "body",
          "kind": "text",
          "nullable": true
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
      "id": "delete",
      "label": "Delete asset",
      "method": "DELETE",
      "path": "/api/media/assets/{assetId}",
      "fields": [
        {
          "key": "assetId",
          "label": "Asset ID",
          "location": "path",
          "kind": "text",
          "required": true
        }
      ],
      "destructive": true
    }
  ]
} satisfies Resource;
