package ru.egorodov.spark_interactions

import akka.http.scaladsl.model._
import HttpMethods._
import HttpProtocols._
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import ru.egorodov.domains.SubmissionResult
import ru.egorodov.utils.JsonSupport
import MediaTypes._

import scala.concurrent.ExecutionContext.Implicits.global

object KillJob extends JsonSupport with SparkRESTRequestRuner {
  def apply(url: String, submissionID: String)(implicit actorSystem: ActorSystem): Option[SubmissionResult] = {
    implicit val materializer = ActorMaterializer()

    val httpRequest = HttpRequest(
      POST,
      uri = url + s"/v1/submissions/kill/${submissionID}",
      entity = HttpEntity.empty(`application/json`),
      protocol = `HTTP/1.1`
    )

    makeRequest(httpRequest)
  }
}
