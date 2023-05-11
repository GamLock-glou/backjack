package com.theshow.core.service
import cats.effect.kernel.Async
import fs2.Stream
import org.elasticsearch.action.index.IndexResponse

trait IndexService[F[_]] {
  def persist: Stream[F, IndexResponse]
}