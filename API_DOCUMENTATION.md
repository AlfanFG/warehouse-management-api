# Warehouse Management API Documentation

Base URL: `http://localhost:8080/api/v1/inventory`

---

## 1. Response Standards

### 1.1 Success Response Wrapper (`ApiResponse<T>`)
```json
{
  "code": 200,
  "status": "success",
  "message": "Pesan status",
  "data": { ... }
}
```

### 1.2 Error Response (RFC 7807 Problem Detail)
```json
{
  "type": "about:blank",
  "title": "Resource Not Found | Out of Stock Error",
  "status": 404,
  "detail": "Item not found",
  "instance": "/api/v1/inventory/items/..."
}
```

---

## 2. Item Endpoints

### 2.1 Create Item (with optional variants)
Membuat item baru beserta variannya (opsional).

- **Method:** `POST`
- **URL:** `http://localhost:8080/api/v1/inventory/items`
- **Headers:** `Content-Type: application/json`

#### cURL Example:
```bash
curl -X POST http://localhost:8080/api/v1/inventory/items \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Wireless Mechanical Keyboard",
    "description": "RGB Backlit, Hot-swappable",
    "variants": [
      {
        "sku": "KB-RED-001",
        "variantName": "Red Switch",
        "price": 750000.00,
        "stockQuantity": 25
      },
      {
        "sku": "",
        "variantName": "Blue Switch",
        "price": 750000.00,
        "stockQuantity": 15
      }
    ]
  }'
```

#### Request Body (JSON):
```json
{
  "name": "Wireless Mechanical Keyboard",
  "description": "RGB Backlit, Hot-swappable",
  "variants": [
    {
      "sku": "KB-RED-001",
      "variantName": "Red Switch",
      "price": 750000.00,
      "stockQuantity": 25
    },
    {
      "sku": "",
      "variantName": "Blue Switch",
      "price": 750000.00,
      "stockQuantity": 15
    }
  ]
}
```
> **Catatan SKU:** Jika `sku` dibiarkan kosong `""` atau `null`, sistem otomatis men-generate format: `[ITEM]-[VARIANT]-[RANDOM_HASH]`.

#### Response (`200 OK`):
```json
{
  "code": 200,
  "status": "success",
  "message": "Item created successfully",
  "data": {
    "id": "7b09df65-8b21-4fa3-9e12-4fcf138e4a90",
    "name": "Wireless Mechanical Keyboard",
    "description": "RGB Backlit, Hot-swappable",
    "variants": [
      {
        "id": "5e17da98-5c4d-4dfb-90f7-ebbe7a01211e",
        "sku": "KB-RED-001",
        "variantName": "Red Switch",
        "price": 750000.00,
        "stockQuantity": 25
      },
      {
        "id": "3c7f9911-37d4-4a46-8809-5a507cb67d5e",
        "sku": "WIRELESS-MECHANICAL-KEYBOARD-BLUE-SWITCH-A1B2",
        "variantName": "Blue Switch",
        "price": 750000.00,
        "stockQuantity": 15
      }
    ]
  }
}
```

---

### 2.2 Get All Items (Paginated)
Mengambil list seluruh item dengan paginasi.

- **Method:** `GET`
- **URL:** `http://localhost:8080/api/v1/inventory/items?page=0&size=10&sort=name,asc`

#### cURL Example:
```bash
curl -X GET "http://localhost:8080/api/v1/inventory/items?page=0&size=10&sort=name,asc"
```

#### Response (`200 OK`):
```json
{
  "code": 200,
  "status": "success",
  "message": "Items retrieved successfully",
  "data": {
    "content": [
      {
        "id": "7b09df65-8b21-4fa3-9e12-4fcf138e4a90",
        "name": "Wireless Mechanical Keyboard",
        "description": "RGB Backlit, Hot-swappable",
        "variants": [
          {
            "id": "5e17da98-5c4d-4dfb-90f7-ebbe7a01211e",
            "sku": "KB-RED-001",
            "variantName": "Red Switch",
            "price": 750000.00,
            "stockQuantity": 25
          }
        ]
      }
    ],
    "page": {
      "size": 10,
      "number": 0,
      "totalElements": 1,
      "totalPages": 1
    }
  }
}
```

---

### 2.3 Get Item By ID
Mengambil detail satu item berdasarkan UUID.

- **Method:** `GET`
- **URL:** `http://localhost:8080/api/v1/inventory/items/{id}`

#### cURL Example:
```bash
curl -X GET http://localhost:8080/api/v1/inventory/items/7b09df65-8b21-4fa3-9e12-4fcf138e4a90
```

#### Response (`200 OK`):
```json
{
  "code": 200,
  "status": "success",
  "message": "Item fetched successfully",
  "data": {
    "id": "7b09df65-8b21-4fa3-9e12-4fcf138e4a90",
    "name": "Wireless Mechanical Keyboard",
    "description": "RGB Backlit, Hot-swappable",
    "variants": [ ... ]
  }
}
```

---

### 2.4 Update Item
Memperbarui nama dan deskripsi item.

- **Method:** `PUT`
- **URL:** `http://localhost:8080/api/v1/inventory/items/{id}`
- **Headers:** `Content-Type: application/json`

#### cURL Example:
```bash
curl -X PUT http://localhost:8080/api/v1/inventory/items/7b09df65-8b21-4fa3-9e12-4fcf138e4a90 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Wireless Mechanical Keyboard Pro",
    "description": "Upgraded with Bluetooth 5.3 and 4000mAh Battery"
  }'
```

#### Request Body (JSON):
```json
{
  "name": "Wireless Mechanical Keyboard Pro",
  "description": "Upgraded with Bluetooth 5.3 and 4000mAh Battery"
}
```

#### Response (`200 OK`):
```json
{
  "code": 200,
  "status": "success",
  "message": "Item updated successfully",
  "data": {
    "id": "7b09df65-8b21-4fa3-9e12-4fcf138e4a90",
    "name": "Wireless Mechanical Keyboard Pro",
    "description": "Upgraded with Bluetooth 5.3 and 4000mAh Battery",
    "variants": [ ... ]
  }
}
```

---

### 2.5 Delete Item (Soft Delete)
Menghapus item secara logika (`is_deleted = true`).

- **Method:** `DELETE`
- **URL:** `http://localhost:8080/api/v1/inventory/items/{id}`

#### cURL Example:
```bash
curl -X DELETE http://localhost:8080/api/v1/inventory/items/7b09df65-8b21-4fa3-9e12-4fcf138e4a90
```

#### Response (`200 OK`):
```json
{
  "code": 200,
  "status": "success",
  "message": "Item deleted successfully",
  "data": null
}
```

---

## 3. Variant Endpoints

### 3.1 Add Variant to Item
Menambahkan varian baru ke item yang sudah ada.

- **Method:** `POST`
- **URL:** `http://localhost:8080/api/v1/inventory/items/{itemId}/variants`
- **Headers:** `Content-Type: application/json`

#### cURL Example:
```bash
curl -X POST http://localhost:8080/api/v1/inventory/items/7b09df65-8b21-4fa3-9e12-4fcf138e4a90/variants \
  -H "Content-Type: application/json" \
  -d '{
    "sku": "KB-BROWN-001",
    "variantName": "Brown Switch Tactile",
    "price": 780000.00,
    "stockQuantity": 30
  }'
```

#### Request Body (JSON):
```json
{
  "sku": "KB-BROWN-001",
  "variantName": "Brown Switch Tactile",
  "price": 780000.00,
  "stockQuantity": 30
}
```

#### Response (`200 OK`):
```json
{
  "code": 200,
  "status": "success",
  "message": "Variant added successfully",
  "data": {
    "id": "993a4087-3cf1-4fe6-802c-fcfe6ff27bb2",
    "sku": "KB-BROWN-001",
    "variantName": "Brown Switch Tactile",
    "price": 780000.00,
    "stockQuantity": 30
  }
}
```

---

### 3.2 Update Variant
Memperbarui nama, harga, stok, atau SKU varian.

- **Method:** `PUT`
- **URL:** `http://localhost:8080/api/v1/inventory/variants/{variantId}`
- **Headers:** `Content-Type: application/json`

#### cURL Example:
```bash
curl -X PUT http://localhost:8080/api/v1/inventory/variants/993a4087-3cf1-4fe6-802c-fcfe6ff27bb2 \
  -H "Content-Type: application/json" \
  -d '{
    "sku": "KB-BROWN-MOD",
    "variantName": "Brown Switch Lubed",
    "price": 820000.00,
    "stockQuantity": 20
  }'
```

#### Request Body (JSON):
```json
{
  "sku": "KB-BROWN-MOD",
  "variantName": "Brown Switch Lubed",
  "price": 820000.00,
  "stockQuantity": 20
}
```

#### Response (`200 OK`):
```json
{
  "code": 200,
  "status": "success",
  "message": "Variant updated successfully",
  "data": {
    "id": "993a4087-3cf1-4fe6-802c-fcfe6ff27bb2",
    "sku": "KB-BROWN-MOD",
    "variantName": "Brown Switch Lubed",
    "price": 820000.00,
    "stockQuantity": 20
  }
}
```

---

### 3.3 Delete Variant (Soft Delete)
Menghapus varian secara logika (`is_deleted = true`).

- **Method:** `DELETE`
- **URL:** `http://localhost:8080/api/v1/inventory/variants/{variantId}`

#### cURL Example:
```bash
curl -X DELETE http://localhost:8080/api/v1/inventory/variants/993a4087-3cf1-4fe6-802c-fcfe6ff27bb2
```

#### Response (`200 OK`):
```json
{
  "code": 200,
  "status": "success",
  "message": "Variant deleted successfully",
  "data": null
}
```

---

## 4. Sales / Transaction Endpoint

### 4.1 Process Sale (Idempotent & Concurrent-Safe)
Memproses penjualan stok varian.
- Menggunakan **Pessimistic Locking** (`PESSIMISTIC_WRITE`) untuk mencegah race condition.
- Menggunakan **Idempotency-Key** untuk mencegah double debit stok bila request dikirim ulang.

- **Method:** `POST`
- **URL:** `http://localhost:8080/api/v1/inventory/sell`
- **Headers:**
  - `Content-Type: application/json`
  - `Idempotency-Key: <unique_string_or_uuid>` *(Wajib)*

#### cURL Example:
```bash
curl -X POST http://localhost:8080/api/v1/inventory/sell \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: TXN-ORDER-20261110-001" \
  -d '{
    "variantId": "993a4087-3cf1-4fe6-802c-fcfe6ff27bb2",
    "quantity": 2
  }'
```

#### Request Body (JSON):
```json
{
  "variantId": "993a4087-3cf1-4fe6-802c-fcfe6ff27bb2",
  "quantity": 2
}
```

#### Response Sukses Pertama Kali (`200 OK`):
```json
{
  "code": 200,
  "status": "success",
  "message": "Transaction completed",
  "data": {
    "transactionStatus": "SUCCESS",
    "message": "Sale processed successfully",
    "variantId": "993a4087-3cf1-4fe6-802c-fcfe6ff27bb2",
    "remainingStock": 18
  }
}
```

#### Response Jika Idempotency Key Terduplikasi (`200 OK`):
```json
{
  "code": 200,
  "status": "success",
  "message": "Transaction completed",
  "data": {
    "transactionStatus": "IDEMPOTENT_SUCCESS",
    "message": "Transaction already processed",
    "variantId": "993a4087-3cf1-4fe6-802c-fcfe6ff27bb2",
    "remainingStock": null
  }
}
```

#### Response Jika Stok Kurang (`400 Bad Request`):
```json
{
  "type": "about:blank",
  "title": "Out of Stock Error",
  "status": 400,
  "detail": "Insufficient stock for SKU: KB-BROWN-MOD. Requested: 50, Available: 18"
}
```
