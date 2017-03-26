package ru.egorodov.spark_interactions

import ru.egorodov.domains.SubmissionWrapper
import akka.http.scaladsl.model._
import HttpMethods._
import HttpProtocols._
import MediaTypes._
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import ru.egorodov.utils.JsonSupport
import ru.egorodov.domains.SubmissionResult

object SubmitJob extends JsonSupport with SparkRESTRequestRuner {
  def apply(submission: SubmissionWrapper)(implicit actorSystem: ActorSystem): Option[SubmissionResult] = {
    implicit val materializer = ActorMaterializer()

    val request = HttpRequest(
      POST,
      uri = submission.sparkMasterUi,
      entity = HttpEntity(`application/json`, v1CreateSubmissionparams(submission)),
      protocol = `HTTP/1.1`)

    makeRequest(request)
  }

  private def v1CreateSubmissionparams(subm: SubmissionWrapper): String = {
    s"""
      |{
      |  "action" : "CreateSubmissionRequest",
      |  "appArgs" : [],
      |  "appResource" : "${subm.appResource}",
      |  "clientSparkVersion" : "${subm.clientSparkVersion}",
      |  "environmentVariables" : {
      |    "SPARK_ENV_LOADED" : "1"
      |  },
      |  "mainClass" : "${subm.mainClass}",
      |  "sparkProperties" : {
      |    "spark.jars" : "${subm.sparkJars}",
      |    "spark.driver.supervise" : "true",
      |    "spark.app.name" : "${subm.sparkAppName}",
      |    "spark.eventLog.enabled": "true",
      |    "spark.submit.deployMode" : "cluster",
      |    "spark.master" : "${subm.sparkMaster}"
      |  }
      |}
    """.stripMargin
  }
}
