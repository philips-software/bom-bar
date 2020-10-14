# BOM-bar, a service to evaluate the Bill-of-Materials for software projects

**Description**:  A service to evaluate the Bill-of-Materials for software projects.

This is an _experimental_ tool for evaluating various aspects of the Software 
Bill-of-Materials (SBOM) for projects, including:

- License compatibility of packages in the product
- Security vulnerabilities (TODO)

The latest SBOM of a project can be uploaded automatically during every CI build, so
the service can provide feedback about violations by the packages in the product. The
license compatibility checker takes the target distribution of the project, and the 
relations between packages into account. 

Relations between packages of a project are expressed as "dependencies". Each dependency 
is identified by a [package URL](https://github.com/package-url/purl-spec). This 
identifier is split into a reference (covering the type/namespace/name and subpath parts) 
and the version. The reference links a dependency to a common package definitions that is
shared between projects. This allows packages attributes (like identifiers that 
reference security vulnerabilities, or common exemption information) to be managed 
independent of projects.

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

## Installation

The software is built by the Maven `mvn clean install` command.

The server is started as a standard Java executable using `java -jar <application-name>.jar`.

## Configuration

(No configuration supported.)

## Usage

The service exposes its REST API on port 8081.

## How to test the software

Unit tests are executed by the Maven `mvn clean test` command.

## Known issues
The software is not suited for production use.

These are the most important topics that need to be addressed:
(A marked checkbox means the topic is in progress.)

- [x] Import of SBOM in SPDX format.
- [x] License compatibility evaluation.
- [ ] Rest API to expose evaluation results.
- [ ] Persist data into database.

Future ideas:
- [ ] Track security vulnerabilities based on CVE/NVD database.

## Contact / Getting help

Submit an issue in the issue tracker of this project.

## License

See [LICENSE.md](LICENSE.md).

## Credits and references

(Empty)


