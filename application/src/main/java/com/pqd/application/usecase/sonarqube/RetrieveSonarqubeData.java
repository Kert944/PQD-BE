package com.pqd.application.usecase.sonarqube;

import com.pqd.application.domain.release.ReleaseInfoSonarqube;
import com.pqd.application.domain.sonarqube.SonarqubeInfo;
import com.pqd.application.usecase.AbstractResponse;
import com.pqd.application.usecase.UseCase;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@UseCase
@Transactional
public class RetrieveSonarqubeData {

    private final SonarqubeGateway sonarqubeGateway;

    public Response execute(Request request) {
        ReleaseInfoSonarqube releaseInfoSonarqube = sonarqubeGateway.getSonarqubeReleaseInfo(request.getSonarqubeInfo());
        return Response.of(releaseInfoSonarqube);
    }

    @Value(staticConstructor = "of")
    @EqualsAndHashCode(callSuper = false)
    public static class Response extends AbstractResponse {

        ReleaseInfoSonarqube releaseInfo;
    }

    @Value(staticConstructor = "of")
    @EqualsAndHashCode(callSuper = false)
    public static class Request {

        SonarqubeInfo sonarqubeInfo;
    }

}
