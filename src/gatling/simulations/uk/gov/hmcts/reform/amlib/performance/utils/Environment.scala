package uk.gov.hmcts.reform.cmc.performance.utils

object Environment {
  val baseUrl : String = scala.util.Properties.envOrElse("URL_TO_TEST","http://localhost:2704")
}