package ru.egorodov.domains

final case class SubmissionWrapper(sparkMasterUi: String, appResource: String,
                                   clientSparkVersion: String, mainClass: String,
                                   sparkJars: String, sparkAppName: String,
                                   sparkMaster: String
)
