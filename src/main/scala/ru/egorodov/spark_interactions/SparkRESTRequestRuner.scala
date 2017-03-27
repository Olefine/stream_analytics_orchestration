package ru.egorodov.spark_interactions

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import ru.egorodov.domains.SubmissionResult
import ru.egorodov.utils.JsonSupport

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

trait SparkRESTRequestRuner extends JsonSupport {
  def makeRequest(request: HttpRequest)(implicit actorSystem: ActorSystem) = {
    implicit val materializer = ActorMaterializer()
    val response: Future[HttpResponse] = Http().singleRequest(request)

    Await.result(response, 60.seconds) match {
      case HttpResponse(OK, _, en, _) => Some(
        Await.result(Unmarshal(en).to[SubmissionResult], 10.seconds)
      )
      case HttpResponse(UnprocessableEntity, _, _, _) => None
    }
  }
}
