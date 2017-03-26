package ru.egorodov

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.util.Timeout
import ch.megard.akka.http.cors.CorsDirectives._
import ch.megard.akka.http.cors.CorsSettings
import ru.egorodov.domains.SubmissionWrapper

import scala.concurrent.duration._
import ru.egorodov.utils.JsonSupport


object EntryPoint extends App with JsonSupport {
  implicit val system = ActorSystem("stream-analytics-orchestration")
  implicit val materializer = ActorMaterializer()
  implicit val ex = system.dispatcher
  implicit val timeout = Timeout(10.seconds)

  val corsSettins = CorsSettings.defaultSettings
  val routes = cors(corsSettins) {
    post {
      path("submissions") {
        entity(as[SubmissionWrapper]) { submission =>
          val sparkInteractionActor = system.actorOf(Props(new actors.SparkCommunicationActor(submission)), name = submission.sparkAppName)
          sparkInteractionActor ! messages.Messages.Spark.SubmissionCreate
          complete(s"$submission received, send data to create")
        }
      }
    }
  }

  Http().bindAndHandle(routes, "0.0.0.0", 3000)
}
