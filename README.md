# BOM-bar, a service to evaluate the Bill-of-Materials for software projects

## Overview
BOM-bar is an _experimental_ web service for evaluating various aspects 
of the software bill-of-materials files for projects, including:

- License compatibility of packages
- Security vulnerabilities (TODO)

## TODO / Limitations
(A marked checkbox means the topic is in progress.)

- [x] Import of SBOM in SPDX format.
- [x] License compatibility evaluation.
- [ ] Rest API to expose evaluation results.
- [ ] Persist data into database.

Future ideas:
- [ ] Track security vulnerabilities based on CVE/NVD database.

## REST API
The web server starts by default on port 8081.

### Create project
POST `/projects`
```json
{
  "title": "<Assign a title here>"
}
```

Returns the assigned project UUID in the location header, and a
body indicating the current project properties. (See Get project)

### Upload project
POST `/projects/{uuid}/upload`

Multipart file upload of an SPDX file in key-value format, using 
`file` as the key for the file.

### Get project
GET `/projects/{uuid}`

Returns the primary project information and hierarchy of contained 
packages, for example:
```json
{
    "id": "d4b5fdc3-2fd4-4ad6-80ae-be7224de33f6",
    "title": "<Project title>",
    "packages": [
        {
            "id": "<namespace>/<name>-<version>",
            "title": "<Name of the package>",
            "relation": "<relation type>",
            "license": "<SPDX license identifier>",
            "children": [
                {
                    "id": "<namespace>/<name>-<version>",
                    "title": "<Name of the package>",
                    "relation": "<relation type>",
                    "license": "<SPDX license identifier>",
                    "children": []
                }   
            ]
        }
    ]
}
```
