<div align="center">

# BOM-Bar

[![Release](https://img.shields.io/github/release/philips-software/bom-bar.svg)](https://github.com/philips-software/bom-bar/releases)

**Description**:  BOM-Bar is an _experimental_ bill-of-materials aggregator
and (license compatibility) policy validation service.

</div>

By uploading the latest bill-of-materials from every CI build, the BOM-Bar
service can keep an inventory of all packages in use within an organization and
provide feedback to the development team on potential (license) policy
violations.

License compatibility detection takes individual package licenses, the target
distribution and package relations into account. Violations can be manually
overridden after investigation for packages that provide
a [package URL](https://github.com/package-url/purl-spec). (The Package URL is
used as identifier to track such exemptions across bill-of-materials uploads.)

Package URLs also provide insights into the use of versions of packages, and the
re-use of packages across projects.

Bill-of-materials files are uploaded as SPDX tag-value files, where
relationships from the document are used to build the typed hierarchy of
packages. This information is used to separate deliverable from development
packages, and identify any originating project for a package version.

(See the [architecture document](docs/architecture.md) for a detailed technical
description.)

## Dependencies

The service requires at least Java 11.

## Installation

The Flutter web user interface is built by the `install_ui` script from
the `/ui` directory. This script builds the web application and installs it into
the `/src/main/resources/static` directory of the backend.

The Java backend software is built by the standard Maven `mvn clean install`
command.

The server is started as a standard Java executable
using `java -jar <application-name>.jar`.

## Configuration

(Empty)

## Usage

### Web interface

After starting up, the service exposes on port 8080 (=default):

* A user interface to browse projects for detected policy violations and
  packages and grant exemptions.
* An API to upload SPDX software bill-of-materials documents in tag-value format
  by a POST request to `/projects/<project_uuid>/upload`.
* A (basic) and insecure database management tool on url `/h2`. with default
  credentials "user" and "password".

### Solving database migration issues

If migration of the database fails, a stand-alone database server can be started
from the command line on Linux or Mac using:

    java -jar ~/.m2/repository/com/h2database/h2/<version>/h2-<version>.jar

(Failed migrations can be manually corrected or removed in the
"flyway_schema_history" table.)

### Docker

After building the project, you can also run the application with Docker.

Build docker image:

```bash
docker build -f docker/Dockerfile -t bom-bar .
```

Run application:

```
docker run -p 8080:8080 bom-bar
```

### Image from docker hub

Run application:

```
docker run -p 8080:8080 philipssoftware/bom-bar:latest
```

## How to test the software

Java unit tests are executed by the Maven `mvn clean test` command.

Flutter unit tests are executed by the Flutter `flutter test` command from
the `/ui` directory.

## Known issues

The software is not suited for production use.

These are the most important topics that need to be addressed:
(A marked checkbox means the topic is in progress.)

- [x] License compatibility evaluation.
- [ ] List applicable packages per license obligation
- [ ] Import license obligations from
  e.g. [OSADL](https://www.osadl.org/Access-to-raw-data.oss-compliance-raw-data-access.0.html)

Future ideas:

- [ ] Derive key technologies of projects from packages used
- [ ] Track security vulnerabilities.

## Disclaimer

BOM-Bar is an _experimental_ tool and not a substitute for human review of each
dependency for licensing or any other issues. It is not the goal of BOM-Bar to
provide legal advice about licensing or any other issues. If you have any
questions regarding licensing compliance for your code or any other legal issues
relating to it, it’s up to you to do further research or consult with a
professional.

## Contact / Getting help

Submit an issue in the issue tracker of this project.

## License

See [LICENSE.md](LICENSE.md).

## Credits and references

- [The Software Package Data Exchange (SPDX®) Specification Version 2.2](https://spdx.github.io/spdx-spec/)
