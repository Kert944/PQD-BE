package com.pqd.application.domain.release;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode(callSuper = false)
public class ReleaseInfoSonarqube {

    private final Long id;

    private final Double securityRating;

    private final Double reliabilityRating;

    private final Double maintainabilityRating;

    private final Double securityVulnerabilities;

    private final Double reliabilityBugs;

    private final Double maintainabilityDebt;

    private final Double maintainabilitySmells;
}
