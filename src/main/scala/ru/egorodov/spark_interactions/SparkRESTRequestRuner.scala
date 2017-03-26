package ru.egorodov.spark_interactions

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.unmarshalling.Unmarshal
import ru.egorodov.domains.SubmissionResult
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

trait SparkRESTRequestRuner {
  def makeRequest(request: HttpRequest) = {
    val response: Future[HttpResponse] = Http().singleRequest(request)

    Await.result(response, 60.seconds) match {
      case HttpResponse(OK, _, en, _) => Some(
        Await.result(Unmarshal(en).to[SubmissionResult], 10.seconds)
      )
      case HttpResponse(UnprocessableEntity, _, _, _) => None
    }
  }
}
