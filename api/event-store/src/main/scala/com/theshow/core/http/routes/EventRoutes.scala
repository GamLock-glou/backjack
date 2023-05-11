package com.theshow.core.http.routes

import cats.data.Kleisli
import cats.effect.kernel.Async
import com.theshow.core.domain.Event
import com.theshow.core.kafka.KafkaProducerAlgebra
import org.http4s.{HttpRoutes, Request, Response}
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.http4s.circe.{jsonEncoder, jsonOf}
import cats.implicits._
import com.theshow.core.http.routes.Protocol.{TokenAndUser, User}
import com.theshow.core.utils.Token
import io.circe.Json
import io.circe.parser._
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec._

object Protocol {
  final case class User(name: String, age: Int)
  final case class TokenAndUser(token: String, user: User)
}

case class EventRoutes[F[_]: Async]()
    extends Http4sDsl[F] {
//  private val twinPeaksRawJson: String =
//    """
//      |{
//      |  "show": "Twin Peaks",
//      |  "ratings": [
//      |    { "season": 1, "metaScore": 96 },
//      |    { "season": 2, "metaScore": 95 },
//      |    { "season": 3, "metaScore": 74 }
//      |  ]
//      |}""".stripMargin
//
//  private val twinPeaksParsed: Json =
//    parse(twinPeaksRawJson).getOrElse(Json.Null)
//  private[routes] val prefix = "/api/v1/event"
//  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
//    case req @ POST -> Root => {
//        implicit val entityDecoder = jsonOf[F, Json]
//        req
//          .attemptAs[Json]
//          .foldF(
//            _ => BadRequest("And error"),
//            event => Created("hi")
//          )
//      }
//  }

  private val helloRoutes = HttpRoutes.of[F] {

    // curl "localhost:9001/hello/world"
    case GET -> Root / "hello" / name =>
      Ok(User(name = "Eugene", age = 21))

    // curl -XPOST "localhost:9001/hello" -d "world"
    case req@POST -> Root / "hello" => {
      req.as[User].flatMap { user =>
        val newUser = User(name = user.name, age = user.age + 1)
        Token.checkToken(req) match {
          case Some(v) => Ok(TokenAndUser(v, newUser))
          case None => BadRequest("Token not found")
        }
      }
    }
  }

  private[http] val httpApp = Seq(helloRoutes).reduce(_ <+> _).orNotFound

  def GetHttpApp(): Kleisli[F, Request[F], Response[F]] = httpApp
}
