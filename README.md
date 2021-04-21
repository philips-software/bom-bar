# BOM-bar, a service to evaluate the Bill-of-Materials for software projects

**Description**:  A service to evaluate the Bill-of-Materials for software
projects.

(See the [architecture document](docs/architecture.md) for a detailed technical
description.)

This is an _experimental_ tool for evaluating various aspects of the Software
Bill-of-Materials (SBOM) for projects, including:

- License compatibility of packages in the product
- Security vulnerabilities (TODO)

The latest SBOM of a project can be uploaded automatically during every CI
build, so the service can provide feedback about violations by the packages in
the product. The license compatibility checker takes the target distribution of
the project, and the relations between packages into account.

Relations between packages of a project are expressed as "dependencies". Each
dependency is identified by
a [package URL](https://github.com/package-url/purl-spec). This identifier is
split into a reference (covering the type/namespace/name and subpath parts)
and the version. The reference links a dependency to a common package
definitions that is shared between projects. This allows packages attributes (
like identifiers that reference security vulnerabilities, or common exemption
information) to be managed independent of projects.

The mapping of SPDX relationships to package dependencies is as follows:

SPDX relationship | Package relation | Remark
------------------|------------------|--------
DESCENDANT_OF     | Modified code | Interpreted as contributions to or changes in the package itself.
STATIC_LINK       | Statically linked | Package is tightly coupled.
DYNAMIC_LINK      | Dynamically linked | Package is slightly coupled. (E.g. relevant for LGPL licenses)
DEPENDS_ON        | Independent | Separate work, but still related. (E.g. relevant for AGPL licenses)
(All others)      | Unrelated | Truly unrelated package.

## Dependencies

The service requires Java 11.

The Web user interface is developed in the separate
[BOM-bar UI](https://github.com/philips-software/bom-bar-ui) project.

## Installation

The software is built by the Maven `mvn clean install` command.

The server is started as a standard Java executable
using `java -jar <application-name>.jar`.

## Configuration

(No configuration supported.)

## Usage

### Web interface

After starting up, the service exposes on port 8080 (=default):

* A user interface to browse projects for detected policy violations and
  packages and grant exemptions.
  (See the separate [bom_bar_ui](https://github.com/philips-software/bom_bar_ui)
  user interface project.)
* An API to upload SPDX SBOM documents in tag-value format by a POST request
  to `/projects/<project_uuid>/upload`.
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

## How to test the software

Unit tests are executed by the Maven `mvn clean test` command.

## Known issues

The software is not suited for production use.

These are the most important topics that need to be addressed:
(A marked checkbox means the topic is in progress.)

- [x] License compatibility evaluation.
- [ ] Handle dependencies according to type (e.g. skip dev dependencies)
- [ ] List applicable packages per license obligation
- [ ] Derive key technologies of projects from packages used
- [ ] Import license obligations from
  e.g. [OSADL](https://www.osadl.org/Access-to-raw-data.oss-compliance-raw-data-access.0.html)

Future ideas:

- [ ] Track security vulnerabilities.

## Disclaimer

BOM-bar is an _experimental_ tool and not a substitute for human review of each
dependency for licensing or any other issues. It is not the goal of BOM-bar to
provide legal advice about licensing or any other issues. If you have any
questions regarding licensing compliance for your code or any other legal issues
relating to it, it’s up to you to do further research or consult with a
professional.

## Contact / Getting help

Submit an issue in the issue tracker of this project.

## License

See [LICENSE.md](LICENSE.md).

## Credits and references

(Empty)


