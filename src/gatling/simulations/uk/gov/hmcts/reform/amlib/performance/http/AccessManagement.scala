package gov.hmcts.reform.amlib.performance.http

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

object AccessManagement {

  private def postRequest(url: String, body: String): HttpRequestBuilder =
    http(url)
      .post(url)
      .body(StringBody(body)).asJSON
      .check(status.is(200))

  def helloWorld: HttpRequestBuilder =
    http("GET Hello World")
      .get("/")
      .check(status.is(200))

  def createResourceAccess(body: String): HttpRequestBuilder =
    postRequest("/create-resource-access", body)

  def filterResource(body: String): HttpRequestBuilder =
    postRequest("/filter-resource", body)

  def getAccessorsList(body: String): HttpRequestBuilder =
    postRequest("/get-accessors-list", body)
}
