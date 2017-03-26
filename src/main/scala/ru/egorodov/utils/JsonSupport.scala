package ru.egorodov.utils

import spray.json._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import ru.egorodov.domains.{SubmissionWrapper, SubmissionResult}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val itemFormat = jsonFormat7(SubmissionWrapper)
  implicit val resultFormat = jsonFormat5(SubmissionResult)
}
