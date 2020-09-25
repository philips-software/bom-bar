package com.philips.research.collector.core.spdx;

import com.philips.research.collector.core.domain.Package;
import com.philips.research.collector.core.domain.Project;
import com.philips.research.collector.core.domain.Purl;
import pl.tlinkowski.annotation.basic.NullOr;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SpdxParser {
    private static final String UNKNOWN = "unknown/";

    private final Project project;
    private final Map<String, Package> lookup = new HashMap<>();
    private final List<Relationship> relationships = new ArrayList<>();

    private @NullOr SpdxPackage current;
    private List<Package> initialPackages = List.of();

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
                //noinspection ConstantConditions
                ifValue(value, (v) -> current.setLicense(value));
                break;
            case "SPDXID":
                //noinspection ConstantConditions
                ifValue(value, (v) -> current.setSpdxRef(value));
                break;
            case "ExternalRef":
                externalRef(value);
                break;
            case "Relationship":
                relationships.add(new Relationship(value));
                break;
            case "FileName": // Start of other section
                mergeCurrent();
                break;
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
        relationships.forEach(Relationship::apply);
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

        private String spdxRef = "";
        private String reference = "";
        private String version = "";
        private String license = "";

        public SpdxPackage(String name) {
            this.name = name;
        }

        void setSpdxRef(String reference) {
            this.spdxRef = reference;
        }

        void setPurl(Purl purl) {
            reference = purl.getName();
            version = purl.getVersion();
        }

        String getReference() {
            if (!reference.isBlank()) {
                return reference;
            } else if (!spdxRef.isBlank()) {
                return UNKNOWN + spdxRef;
            } else {
                return UNKNOWN + name;
            }
        }

        String getVersion() {
            return version;
        }

        void setLicense(String license) {
            this.license = license;
        }

        void merge() {
            final var pkg = project.getPackage(getReference(), getVersion())
                    .orElseGet(() -> {
                        final var newPkg = new Package(getReference(), getVersion());
                        newPkg.setTitle(name);
                        project.addPackage(newPkg);
                        return newPkg;
                    });
            pkg.setLicense(license);

            if (!spdxRef.isBlank()) {
                lookup.put(spdxRef, pkg);
            }
            initialPackages.remove(pkg);
        }
    }

    private class Relationship {
        private final String specification;

        Relationship(String specification) {
            this.specification = specification;
        }

        void apply() {
            final var parts = specification.split("\\s+");
            final var from = lookup.get(parts[0]);
            final var relation = parts[1];
            final var to = lookup.get(parts[2]);

            //noinspection ConditionCoveredByFurtherCondition
            if (from == null || to == null) {
                return;
            }

            switch (relation) {
                case "DESCENDANT_OF":
                    from.addChild(to, Package.Relation.SOURCE_CODE);
                    break;
                case "STATIC_LINK":
                    from.addChild(to, Package.Relation.STATIC_LINK);
                    break;
                case "DYNAMIC_LINK":
                    from.addChild(to, Package.Relation.DYNAMIC_LINK);
                    break;
                case "DEPENDS_ON":
                    from.addChild(to, Package.Relation.INDEPENDENT);
                    break;
                default:
                    // Ignore relation
            }
        }
    }
}
