package ru.egorodov.domains

trait RatioCheckResult
case object Kill extends RatioCheckResult
case object Pass extends RatioCheckResult
