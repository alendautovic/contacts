# Contacts API

A demo Spring Boot application that can be used to manage contacts using a RESTful API.

This documentation describes how to use the Contacts (https://alendautovic-contacts.herokuapp.com) API.

## Create a Contact

### Request
To create a Contact, you should provide a JSON formatted Contact object.

```http
POST /api/contacts
```
```json
{
  "fullName": "James Miller",
  "dateOfBirth": "05/25/1985",
  "address": {
    "city": "Podgorica",
    "postalCode": "81000"
  }
}
```

The `fullName` is a required field.

### Response

If contact was created successfully, response body should look like this:

```json
{
  "id": 1,
  "fullName": "James Miller",
  "dateOfBirth": "05/25/1985",
  "address": {
    "city": "Podgorica",
    "postalCode": "81000"
  }
}
```

If you specified an ID inside request body of the HTTP request, you should receive following error, with `400` HTTP status code:

```json
{
  "timestamp": "2020-10-18T20:48:42.030+00:00",
  "status": 400,
  "error": "Bad Request",
  "trace": "...",
  "message": "A new contact cannot already have an ID",
  "path": "/api/contacts"
}
```

If the required `fullName` is not provided, response body should look like this:

```json
{
  "timestamp": "2020-10-18T20:49:44.608+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for object='contact'. Error count: 1",
  "errors": [
    {
      "codes": [
        "NotNull.contact.fullName",
        "NotNull.fullName",
        "NotNull.java.lang.String",
        "NotNull"
      ],
      "arguments": [
        {
          "codes": [
            "contact.fullName",
            "fullName"
          ],
          "arguments": null,
          "defaultMessage": "fullName",
          "code": "fullName"
        }
      ],
      "defaultMessage": "must not be null",
      "objectName": "contact",
      "field": "fullName",
      "rejectedValue": null,
      "bindingFailure": false,
      "code": "NotNull"
    }
  ],
  "path": "/api/contacts"
}
```

## Find contacts

### Request
To find all contacts, you should call following endpoint without any parameters:

```http
GET /api/contacts
```
To find contacts by postalCode, you should provide a `postalCode` parameter:

```http
GET /api/contacts?postalCode=81000
```

### Response
Response body should like like this:

```json
[
  {
    "id": 1,
    "fullName": "James Miller",
    "dateOfBirth": "05/25/1985",
    "address": {
      "city": "Podgorica",
      "postalCode": "81000"
    }
  },
  {
    "id": 2,
    "fullName": "George Willis",
    "dateOfBirth": "06/24/1984",
    "address": {
      "city": "Berlin",
      "postalCode": "10115"
    }
  }
]
```

### Parameters:

| Parameter | Type | Description |
| :--- | :--- | :--- |
| `postalCode` | `string` | *Optional*. Postal code to be filtered on |

