package ru.egorodov.actors

import ru.egorodov.domains.SubmissionWrapper
import ru.egorodov.spark_interactions.{KillJob, SubmitJob}
import ru.egorodov.messages.Messages
import akka.actor.Actor
import ru.egorodov.domains.SubmissionResult

class SparkCommunicationActor(private val submissionParams: SubmissionWrapper) extends Actor {
  implicit val actorSystem = context.system

  private var submission: SubmissionResult = _

  def receive: Receive = {
    case Messages.Spark.SubmissionCreate =>
      val resultOfSubmittion =
        SubmitJob(submissionParams) match {
          case Some(result: SubmissionResult) =>
            setResult(result)
            context become jobSubmitted
          case None => println("no result")
        }
  }

  def jobSubmitted: Receive = {
    case Messages.GetSubmissionId => sender ! submission.submissionId
    case Messages.Kill => KillJob(submissionParams.sparkMasterUi, submission.submissionId)
  }

  private def setResult(result: SubmissionResult): Unit = {
    submission = result
  }
}
