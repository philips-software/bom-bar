# Collector

## Overview
Collector is an experimental web service for collecting the software 
bill-of-materials ("SBOM") for projects. It is intended to evaluate
license compatibility of the reported packages.

## TODO / Limitations
(A marked checkbox means the topic is in progress.)

- [x] Import of SBOM in SPDX format.
- [x] License compatibility evaluation.
- [x] Rest API to expose evaluation results.

Future ideas:
- [ ] Track security vulnerabilities based on CVE/NVD database.
