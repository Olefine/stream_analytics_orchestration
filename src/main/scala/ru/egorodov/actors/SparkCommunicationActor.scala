package ru.egorodov.actors

import ru.egorodov.domains.SubmissionWrapper
import ru.egorodov.spark_interactions.{KillJob, SubmitJob}
import ru.egorodov.messages.Messages
import akka.actor.{Actor, ActorRef}
import ru.egorodov.domains.SubmissionResult
import ru.egorodov.services.JobRatioChecker

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
    case Messages.Kill =>
      sender ! KillJob(submissionParams.sparkMasterUi, submission.submissionId)
      actorSystem.stop(self)
    case Messages.StreamingResult.CheckRatio(ratio) =>
      val ratioChecker = JobRatioChecker(submissionParams, ratio)
      ratioChecker.passOrKill match {
        case ru.egorodov.domains.Kill =>
          self ! KillJob(submissionParams.sparkMasterUi, submission.submissionId)
        case ru.egorodov.domains.Pass =>
      }
  }

  private def setResult(result: SubmissionResult): Unit = {
    submission = result
  }
}
