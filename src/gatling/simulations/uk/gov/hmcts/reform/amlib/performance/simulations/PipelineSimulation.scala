package gov.hmcts.reform.amlib.performance.simulations

import gov.hmcts.reform.amlib.performance.http.AccessManagement
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.amlib.performance.utils.Environment

import scala.concurrent.duration._

class PipelineSimulation extends Simulation {

  val httpProtocol = http.baseURL(Environment.baseUrl)

  val scn = scenario("Hello World")
    .forever(
      exec(AccessManagement.helloWorld)
        .pause(1.second)
    )

  setUp(
    scn.inject(rampUsers(10).over(10.seconds))
      .protocols(httpProtocol)
  ).maxDuration(30.seconds)
    .assertions(
      global.failedRequests.count.is(0)
    )
}