package com.philips.research.collector.core.spdx;

import com.philips.research.collector.core.domain.Package;
import com.philips.research.collector.core.domain.Project;
import com.philips.research.collector.core.domain.Purl;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.function.Consumer;

public class SpdxParser {
    private final Project project;

    private SpdxPackage current;
    private List<Package> initialPackages;

    public SpdxParser(Project project) {
        this.project = project;
    }

    public void parse(InputStream stream) {
        initialPackages = project.getPackages();
        new TagValueParser(this::tagValue).parse(stream);
        finish();
    }

    private void tagValue(String tag, String value) {
        switch (tag) {
            case "PackageName":
                mergeCurrent();
                current = new SpdxPackage(value);
                break;
            case "PackageLicenseConcluded":
                ifValue(value, (v) -> current.setLicense(value));
                break;
            case "ExternalRef":
                externalRef(value);
            default: // Ignore
        }
    }

    private void ifValue(String value, Consumer<String> consumer) {
        if (current == null || "NOASSERTION".equals(value)) {
            return;
        }
        consumer.accept(value);
    }

    private void finish() {
        mergeCurrent();
        initialPackages.forEach(project::removePackage);
    }

    private void mergeCurrent() {
        if (current != null) {
            current.merge();
            current = null;
        }
    }

    private void externalRef(String value) {
        final var elements = value.split("\\s+");
        if (elements.length != 3) {
            throw new SpdxException("Malformed external reference value: " + value);
        }
        if (current != null && "PACKAGE-MANAGER".equals(elements[0]) && "purl".equals(elements[1])) {
            current.setPurl(new Purl(URI.create(elements[2])));
        }
    }

    private class SpdxPackage {
        private final String name;

        private String library;
        private String version;
        private String license;

        public SpdxPackage(String name) {
            this.name = name;
        }

        void setPurl(Purl purl) {
            library = purl.getName();
            version = purl.getVersion();
        }

        void setLicense(String license) {
            this.license = license;
        }

        void merge() {
            validate();
            final var pkg = project.getPackage(library, version)
                    .orElseGet(() -> {
                        final var newPkg = new Package(library, version);
                        project.addPackage(newPkg);
                        return newPkg;
                    });
            pkg.setLicense(license);
            initialPackages.remove(pkg);
        }

        private void validate() {
            if (library == null) {
                throw new SpdxException("Missing Package URL reference for package '" + name + "'");
            }
        }
    }
}
