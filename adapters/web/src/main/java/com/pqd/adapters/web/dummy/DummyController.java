package com.pqd.adapters.web.dummy;

import com.pqd.application.usecase.dummy.GetDummyResult;
import com.pqd.application.usecase.sonarqube.RetrieveSonarqubeData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/dummy")
@RequiredArgsConstructor
public class DummyController {

    private final GetDummyResult getDummyResult;

    private final RetrieveSonarqubeData retrieveSonarqubeData;

    @GetMapping("/response")
    public ResponseEntity<DummyResponseResultJson> getDummyResponse() {
        GetDummyResult.Response response = getDummyResult.execute();
        DummyResponsePresenter presenter = new DummyResponsePresenter();

        presenter.present(response);

        return presenter.getViewModel();
    }

    @GetMapping("/trigger")
    public String trigger() {
        retrieveSonarqubeData.execute();

        return "triggered";
    }
}
