package com.philips.research.bombar.core.domain.licenses;

import com.philips.research.bombar.core.domain.Dependency;
import com.philips.research.bombar.core.domain.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ObligationAnalyzerTest {
    private static final String OTHER_OBLIGATION = "Other";
    private static final String OTHER_OBLIGATION_DESC = "Other desc";
    private static final String KEY = "Key";
    private static final String TITLE = "Title";
    private static final String LICENSE = "License";
    private static final String OBLIGATION = "Obligation";
    private static final String OBLIGATION_DESC = "Obligation desc";

    private final LicenseRegistry registry = new LicenseRegistry();
    private final Project project = new Project(UUID.randomUUID());
    private final ObligationAnalyzer analyzer = new ObligationAnalyzer(registry, project);

    @BeforeEach
    void setUp() {
        registry.term(OBLIGATION, OBLIGATION_DESC);
        registry.term(OTHER_OBLIGATION, OTHER_OBLIGATION_DESC);
    }

    @Test
    void noObligationForEmptyProject() {
        final var obligations = analyzer.findObligations();

        assertThat(obligations).isEmpty();
    }

    @Test
    void noObligationForUnknownLicense() {
        final var dependency = new Dependency(KEY, TITLE).setLicense("unknown");
        project.addDependency(dependency);

        final var obligations = analyzer.findObligations();

        assertThat(obligations).isEqualTo(Map.of("Unknown Obligations", Set.of(dependency)));
    }

    @Test
    void obligationForLicense() {
        registry.license(LICENSE).requires(OBLIGATION);
        final var dependency = new Dependency(KEY, TITLE).setLicense(LICENSE);
        project.addDependency(dependency);

        final var obligations = analyzer.findObligations();

        assertThat(obligations).isEqualTo(Map.of(OBLIGATION_DESC, Set.of(dependency)));
    }

    @Test
    void obligationForMultiLicenses() {
        registry.license(LICENSE).requires(OBLIGATION);
        final var dependency = new Dependency(KEY, TITLE).setLicense(LICENSE + " AND " + LICENSE);
        project.addDependency(dependency);

        final var obligations = analyzer.findObligations();

        assertThat(obligations).isEqualTo(Map.of(OBLIGATION_DESC, Set.of(dependency)));
    }

    @Test
    void multipleObligationForLicense() {
        registry.license(LICENSE).requires(OBLIGATION).requires(OTHER_OBLIGATION);
        final var dependency = new Dependency(KEY, TITLE).setLicense(LICENSE);
        project.addDependency(dependency);

        final var obligations = analyzer.findObligations();

        assertThat(obligations).isEqualTo(Map.of(OBLIGATION_DESC, Set.of(dependency), OTHER_OBLIGATION_DESC, Set.of(dependency)));
    }

    @Test
    void conditionalObligationForLicense() {
        registry.license(LICENSE).requires(OBLIGATION, Project.Distribution.PROPRIETARY);
        final var dependency = new Dependency(KEY, TITLE).setLicense(LICENSE);
        project.addDependency(dependency);
        project.setDistribution(Project.Distribution.OPEN_SOURCE);

        final var obligations = analyzer.findObligations();

        assertThat(obligations).isEmpty();
    }

    @Test
    void sameObligationMultipleDependendcies() {
        registry.license(LICENSE).requires(OBLIGATION);
        final var dependency1 = new Dependency("DEP1", TITLE).setLicense(LICENSE);
        project.addDependency(dependency1);
        final var dependency2 = new Dependency("DEP2", TITLE).setLicense(LICENSE);
        project.addDependency(dependency2);

        final var obligations = analyzer.findObligations();

        assertThat(obligations).isEqualTo(Map.of(OBLIGATION_DESC, Set.of(dependency1, dependency2)));
    }
}