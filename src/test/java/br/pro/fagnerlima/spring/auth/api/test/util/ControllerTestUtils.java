package br.pro.fagnerlima.spring.auth.api.test.util;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import io.restassured.response.Response;

public class ControllerTestUtils {

    public static void assertPage(Response response, int pageSize, int pageNumber, int numberOfElements, int totalPages,
            int totalElements) {
        response.then()
                .body("totalElements", equalTo(totalElements))
                .body("totalPages", equalTo(totalPages))
                .body("number", equalTo(pageNumber))
                .body("numberOfElements", equalTo(numberOfElements))
                .body("size", equalTo(pageSize))
                .body("empty", equalTo(0 == numberOfElements))
                .body("content", hasSize(numberOfElements));
    }

}
