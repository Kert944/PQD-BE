package com.pqd.adapters.sonarqube;

import com.pqd.application.domain.connection.ConnectionResult;
import com.pqd.application.domain.release.ReleaseInfoSonarqube;
import com.pqd.application.domain.sonarqube.SonarqubeInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SonarqubeRestClientTest {

    private RestTemplate restTemplate;
    private SonarqubeRestClient restClient;

    @BeforeEach
    void setup() {
        restTemplate = mock(RestTemplate.class);
        restClient = new SonarqubeRestClient(restTemplate);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void GIVEN_all_good_WHEN_sonarqube_measures_requested_THEN_sonarqube_release_info_returned() {
        SonarqubeMeasureResponse generatedSqResponse = TestDataGenerator.generateSonarqubeMeasureResponse();
        ReleaseInfoSonarqube generatedReleaseInfoSonarqube = TestDataGenerator.generateReleaseInfoSonarqube();
        SonarqubeInfo sonarqubeInfo = TestDataGenerator.generateSonarqubeInfo();
        ResponseEntity<SonarqubeMeasureResponse> responseEntity =
                new ResponseEntity<>(generatedSqResponse, HttpStatus.OK);
        when(restTemplate.exchange(ArgumentMatchers.anyString(),
                                   any(HttpMethod.class),
                                   any(),
                                   ArgumentMatchers.<Class<SonarqubeMeasureResponse>>any()))
                .thenReturn(responseEntity);

        ReleaseInfoSonarqube result = restClient.getSonarqubeReleaseInfo(sonarqubeInfo);

        assertThat(result).isEqualTo(generatedReleaseInfoSonarqube);
    }

    @Test
    void GIVEN_request_error_WHEN_sonarqube_measures_requested_THEN_sonarqube_rest_client_exception_thrown() {
        SonarqubeInfo sonarqubeInfo = TestDataGenerator.generateSonarqubeInfo();
        when(restTemplate.exchange(ArgumentMatchers.anyString(),
                                   any(HttpMethod.class),
                                   any(),
                                   ArgumentMatchers.<Class<SonarqubeMeasureResponse>>any()))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "request error"));

        Exception exception =
                assertThrows(Exception.class, () -> restClient.getSonarqubeReleaseInfo(sonarqubeInfo));
        assertThat(exception).hasStackTraceContaining("SonarqubeRestClientException");
    }

    @Test
    void GIVEN_metric_not_found_WHEN_sonarqube_measures_requested_THEN_sonarqube_measure_response_exception_thrown() {
        SonarqubeInfo sonarqubeInfo = TestDataGenerator.generateSonarqubeInfo();
        SonarqubeMeasureResponse generatedSqResponse = TestDataGenerator.generateSonarqubeMeasureResponse_withInvalidMeasures();
        ResponseEntity<SonarqubeMeasureResponse> responseEntity =
                new ResponseEntity<>(generatedSqResponse, HttpStatus.OK);
        when(restTemplate.exchange(ArgumentMatchers.anyString(),
                                   any(HttpMethod.class),
                                   any(),
                                   ArgumentMatchers.<Class<SonarqubeMeasureResponse>>any()))
                .thenReturn(responseEntity);

        Exception exception =
                assertThrows(Exception.class, () -> restClient.getSonarqubeReleaseInfo(sonarqubeInfo));
        assertThat(exception).hasStackTraceContaining("SonarqubeMeasureResponseException");
    }

    @Test
    void GIVEN_all_good_WHEN_sonarqube_connection_tested_THEN_sonarqube_connection_result_returned() {
        SonarqubeInfo sonarqubeInfo = TestDataGenerator.generateSonarqubeInfo();
        ConnectionResult connectionResult = TestDataGenerator.generateSonarqubeConnectionResult_success();
        SonarqubeMeasureResponse generatedSqResponse = TestDataGenerator.generateSonarqubeMeasureResponse();
        ResponseEntity<SonarqubeMeasureResponse> responseEntity =
                new ResponseEntity<>(generatedSqResponse, HttpStatus.OK);
        when(restTemplate.exchange(ArgumentMatchers.anyString(),
                                   any(HttpMethod.class),
                                   any(),
                                   ArgumentMatchers.<Class<SonarqubeMeasureResponse>>any()))
                .thenReturn(responseEntity);

        ConnectionResult actual = restClient.testSonarqubeConnection(sonarqubeInfo);

        assertThat(actual).isEqualTo(connectionResult);
    }

    @Test
    void GIVEN_invalid_baseurl_WHEN_sonarqube_connection_tested_THEN_corresponding_result_returned() {
        SonarqubeInfo sonarqubeInfo = TestDataGenerator.generateSonarqubeInfo();
        ConnectionResult connectionResult = TestDataGenerator.generateSonarqubeConnectionResult_wrongBaseUrl();
        when(restTemplate.exchange(ArgumentMatchers.anyString(),
                                   any(HttpMethod.class),
                                   any(),
                                   ArgumentMatchers.<Class<SonarqubeMeasureResponse>>any()))
                .thenThrow(new ResourceAccessException("", new IOException()));

        ConnectionResult actual = restClient.testSonarqubeConnection(sonarqubeInfo);

        assertThat(actual).isEqualTo(connectionResult);
    }

    @Test
    void GIVEN_invalid_component_WHEN_sonarqube_connection_tested_THEN_corresponding_result_returned() {
        SonarqubeInfo sonarqubeInfo = TestDataGenerator.generateSonarqubeInfo();
        ConnectionResult connectionResult = TestDataGenerator.generateSonarqubeConnectionResult_wrongComponent();
        when(restTemplate.exchange(ArgumentMatchers.anyString(),
                                   any(HttpMethod.class),
                                   any(),
                                   ArgumentMatchers.<Class<SonarqubeMeasureResponse>>any()))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        ConnectionResult actual = restClient.testSonarqubeConnection(sonarqubeInfo);

        assertThat(actual).isEqualTo(connectionResult);
    }

    @Test
    void GIVEN_invalid_token_WHEN_sonarqube_connection_tested_THEN_corresponding_result_returned() {
        SonarqubeInfo sonarqubeInfo = TestDataGenerator.generateSonarqubeInfo();
        ConnectionResult connectionResult = TestDataGenerator.generateSonarqubeConnectionResult_wrongToken();
        when(restTemplate.exchange(ArgumentMatchers.anyString(),
                                   any(HttpMethod.class),
                                   any(),
                                   ArgumentMatchers.<Class<SonarqubeMeasureResponse>>any()))
                .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));

        ConnectionResult actual = restClient.testSonarqubeConnection(sonarqubeInfo);

        assertThat(actual).isEqualTo(connectionResult);
    }

}
