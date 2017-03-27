package ru.egorodov

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.util.{ByteString, Timeout}
import ch.megard.akka.http.cors.CorsDirectives._
import ch.megard.akka.http.cors.CorsSettings
import ru.egorodov.domains.{CheckRatio, SubmissionWrapper}
import ru.egorodov.messages.Messages

import scala.concurrent.duration._
import ru.egorodov.utils.JsonSupport
import akka.pattern.ask

import scala.concurrent.{Await, Future}


object EntryPoint extends App with JsonSupport {
  implicit val system = ActorSystem("stream-analytics-orchestration")
  implicit val materializer = ActorMaterializer()
  implicit val ex = system.dispatcher
  implicit val timeout = Timeout(10.seconds)

  private val applications = collection.mutable.Map.empty[String, ActorRef]

  val corsSettins = CorsSettings.defaultSettings
  val routes = cors(corsSettins) {
    post {
      path("submissions") {
        entity(as[SubmissionWrapper]) { submission =>
          val sparkInteractionActor = system.actorOf(Props(new actors.SparkCommunicationActor(submission)), submission.sparkAppName)
          applications(submission.sparkAppName) = sparkInteractionActor
          sparkInteractionActor ! messages.Messages.Spark.SubmissionCreate
          complete(s"$submission received, send data to create")
        }
      } ~
      pathPrefix("submissions" / Segment / "kill") { sparkApplicationName =>
        val applicationActor = Await.result(system.actorSelection(s"/user/$sparkApplicationName").resolveOne(), 10.seconds)

        val killRequest = applicationActor ? Messages.Kill

        onSuccess(killRequest) {
          case Some(_) => complete(StatusCodes.OK, "Job successfully killed.")
          case None => complete(StatusCodes.NotFound, s"Proplem with killing application $sparkApplicationName")
        }
      } ~
      pathPrefix("submissions" / "check_ratio") {
        entity(as[CheckRatio]) { ratioWrapper =>
          val applicationActor = applications(ratioWrapper.applicationName)

          val killRequest = applicationActor ! Messages.StreamingResult.CheckRatio(ratioWrapper.ratio)

          complete(StatusCodes.OK, "Job was scheduled to check ratio, job will be killed if ratio too big")
        }
      }
    }
  }

  Http().bindAndHandle(routes, "0.0.0.0", 3000)
}
