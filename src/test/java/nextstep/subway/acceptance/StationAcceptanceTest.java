package nextstep.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.DataLoader;
import nextstep.subway.applicaion.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.List;

import static nextstep.DataLoader.ADMIN_EMAIL;
import static nextstep.DataLoader.ADMIN_PASSWORD;
import static nextstep.subway.acceptance.MemberAcceptanceTest.PASSWORD;
import static nextstep.subway.acceptance.MemberSteps.로그인_되어_있음;
import static nextstep.subway.acceptance.StationSteps.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    @Autowired
    private DataLoader dataLoader;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        dataLoader.loadData();
    }

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        String accessToken = 로그인_되어_있음(ADMIN_EMAIL, ADMIN_PASSWORD);

        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청("강남역", accessToken);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> stationNames =
                RestAssured.given().log().all()
                        .auth().oauth2(accessToken)
                        .when().get("/stations")
                        .then().log().all()
                        .extract().jsonPath().getList("name", String.class);
        assertThat(stationNames).containsAnyOf("강남역");
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // given
        String accessToken = 로그인_되어_있음(ADMIN_EMAIL, ADMIN_PASSWORD);

        지하철역_생성_요청("강남역", accessToken);
        지하철역_생성_요청("역삼역", accessToken);

        // when
        ExtractableResponse<Response> stationResponse = RestAssured.given().log().all()
                .auth().oauth2(accessToken)
                .when().get("/stations")
                .then().log().all()
                .extract();

        // then
        List<StationResponse> stations = stationResponse.jsonPath().getList(".", StationResponse.class);
        assertThat(stations).hasSize(2);
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        String accessToken = 로그인_되어_있음(ADMIN_EMAIL, ADMIN_PASSWORD);
        ExtractableResponse<Response> createResponse = 지하철역_생성_요청("강남역", accessToken);

        // when
        String location = createResponse.header("location");
        RestAssured.given().log().all()
                .auth().oauth2(accessToken)
                .when()
                .delete(location)
                .then().log().all()
                .extract();

        // then
        List<String> stationNames =
                RestAssured.given().log().all()
                        .auth().oauth2(accessToken)
                        .when().get("/stations")
                        .then().log().all()
                        .extract().jsonPath().getList("name", String.class);
        assertThat(stationNames).doesNotContain("강남역");
    }
}