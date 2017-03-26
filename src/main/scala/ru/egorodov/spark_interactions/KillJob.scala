package ru.egorodov.spark_interactions

import akka.http.scaladsl.model._
import HttpMethods._
import HttpProtocols._
import MediaTypes._
import StatusCodes._
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import ru.egorodov.utils.JsonSupport
import ru.egorodov.domains.SubmissionResult

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

object KillJob extends JsonSupport with SparkRESTRequestRuner {
  def apply(url: String, submissionID: String)(implicit actorSystem: ActorSystem) = {
    implicit val materializer = ActorMaterializer()

    val httpRequest = HttpRequest(
      POST,
      uri = submissionID,
      protocol = `HTTP/1.1`
    )

    makeRequest(httpRequest)
  }
}
