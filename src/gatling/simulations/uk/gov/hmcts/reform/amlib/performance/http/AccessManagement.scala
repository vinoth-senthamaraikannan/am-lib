package gov.hmcts.reform.amlib.performance.http

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

object AccessManagement {

  private def postRequestWithStringBody(url: String, body: String): HttpRequestBuilder =
    http(url)
      .post(url)
      .body(StringBody(body)).asJSON
      .check(status.is(200))

  private def postRequestWithBody(url: String, body: String): HttpRequestBuilder =
    http(url)
      .post(url)
      .body(ElFileBody(body)).asJSON
      .check(status.is(200))

  def helloWorld: HttpRequestBuilder =
    http("GET Hello World")
      .get("/")
      .check(status.is(200))

  def createResourceAccess(body: String): HttpRequestBuilder =
    postRequestWithStringBody("/create-resource-access", body)

  def createResourceAssess: HttpRequestBuilder =
    postRequestWithBody("/create-resource-access","createResourceAccess.json")

  def filterResource(body: String): HttpRequestBuilder =
    postRequestWithStringBody("/filter-resource", body)

  def getAccessorsList(body: String): HttpRequestBuilder =
    postRequestWithStringBody("/get-accessors-list", body)
}
