package ru.egorodov.services

import ru.egorodov.domains.{SubmissionWrapper, Kill, Pass, RatioCheckResult}
import com.typesafe.config.ConfigFactory

case class JobRatioChecker(submission: SubmissionWrapper, ratio: Double) {
  def passOrKill: RatioCheckResult = {
    if (ratio > getKillRatio) Kill
    else Pass
  }

  private def getKillRatio = {
    //according to config, only Monitor and TsvProcessor apps allowed
    ConfigFactory.load().getNumber(s"application.ratio.${submission.sparkAppName}").doubleValue
  }
}
