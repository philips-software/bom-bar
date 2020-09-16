# Collector, a service to evaluate the bill-of-materials for projects

## Overview
Collector is an _experimental_ web service for evaluating various aspects 
of the software bill-of-materials ("SBOM") files for projects, including:

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
