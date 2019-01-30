package gov.hmcts.reform.amlib.performance.simulations

import gov.hmcts.reform.amlib.performance.scenarios.BasicScenarios
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.reform.amlib.performance.utils.Environment

import scala.concurrent.duration._

class PipelineSimulation extends Simulation {

  private val httpProtocol = http.baseURL(Environment.baseUrl)

  setUp(
    BasicScenarios.helloWorld.inject(rampUsers(10).over(10.seconds))
      .protocols(httpProtocol)
  ).maxDuration(30.seconds)
    .assertions(
      global.failedRequests.count.is(0)
    )
}