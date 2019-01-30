package gov.hmcts.reform.amlib.performance.scenarios

import gov.hmcts.reform.amlib.performance.feed.CreateResourceAccess
import gov.hmcts.reform.amlib.performance.http.AccessManagement
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder

object BasicScenarios {
  val helloWorld: ScenarioBuilder = scenario("Hello World")
    .forever(
      exec(AccessManagement.helloWorld)
        .pause(1)
    )

  val createResourceAccess: ScenarioBuilder = scenario("Create Resource Access")
    .forever(
      feed(CreateResourceAccess.feed)
        .exec(AccessManagement.createResourceAssess)
        .pause(1)
    )

  val filterResource: ScenarioBuilder = scenario("Filter Resource")
    .forever(
      feed(CreateResourceAccess.feed)
        .exec(AccessManagement.createResourceAssess)
        .exec(AccessManagement.filterResource)
        .pause(1)
    )

  val getAccessorsList: ScenarioBuilder = scenario("Filter Resource")
    .forever(
      feed(CreateResourceAccess.feed)
        .exec(AccessManagement.createResourceAssess)
        .exec(AccessManagement.getAccessorsList)
        .pause(1)
    )
}
