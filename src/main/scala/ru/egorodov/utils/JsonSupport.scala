package ru.egorodov.utils

import spray.json._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import ru.egorodov.domains.{CheckRatio, SubmissionResult, SubmissionWrapper}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val itemFormat = jsonFormat7(SubmissionWrapper)
  implicit val resultFormat = jsonFormat5(SubmissionResult)
  implicit val ratioFormat = jsonFormat2(CheckRatio)
}
