package ru.egorodov.messages

case object Messages {
  case object Spark {
    case object SubmissionCreate
  }

  case object GetSubmissionId
  case object Kill

  case object StreamingResult {
    case class CheckRatio(ratio: Double)
  }
}
