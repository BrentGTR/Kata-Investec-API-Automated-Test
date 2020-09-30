
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class FormulaOneCircuitsTests {

    @DataProvider(name="seasonsAndNumberOfRaces")
    public Object[][] createTestDataRecords() {
        return new Object[][] {
                {"2020",14},
                {"2017",20},
                {"2016",21},
                {"1966",9}
        };
    }

    @Test(dataProvider="seasonsAndNumberOfRaces")
    public void test_NumberOfCircuits_ShouldBe_DataDriven(String season, int numberOfRaces) {

        given().
                pathParam("raceSeason",season).
                when().
                get("http://ergast.com/api/f1/{raceSeason}/circuits.json").
                then().
                assertThat().
                body("MRData.CircuitTable.Circuits.circuitId",hasSize(numberOfRaces));
    }

    @Test
    public void test_GetLastCircuitFor2020SeasonAndGetCountry_ShouldBeUae() {
        String year = "2020";

        String total =
                given()
                    .pathParam("raceSeason",year)
                    .when()
                        .get("http://ergast.com/api/f1/{raceSeason}/circuits.json")
                    .then().extract().path("MRData.total");

        //Account for zero-based array
        int totalRaces = Integer.parseInt(total);
        totalRaces--;

        String circuitId =
            given()
                    .pathParam("year",year)
            .when()
                    .get("http://ergast.com/api/f1/{year}/circuits.json")
            .then().extract().path("MRData.CircuitTable.Circuits.circuitId["+totalRaces+"]");

            given()
                .pathParam("circuitId",circuitId)
            .when()
                .get("http://ergast.com/api/f1/circuits/{circuitId}.json")
            .then()
                .assertThat()
                    .body("MRData.CircuitTable.Circuits.Location[0].country",equalTo("UAE"));
    }
}