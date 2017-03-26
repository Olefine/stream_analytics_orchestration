package ru.egorodov.domains

final case class SubmissionResult(action: String, message: String, serverSparkVersion: String, submissionId: String, success: Boolean)
