import sbt._

object Dependencies {
  private val http4sVersion = "0.23.7"

  private val catEffectVersion = "3.4.9"

  private val cirisVersion = "3.1.0"

  private val circeVersion = "0.14.1"
  private val circeGenericExtraVersion = "0.14.3"
  private val f2sKafkaVersion = "3.0.0"
  private val elasticSearchClientVersion = "7.17.9"
  private val doobieVersion = "1.0.0-RC1"
  private val newTypeVersion = "0.4.4"

  private def http4s(branch: String) =
    "org.http4s" %% s"http4s-$branch" % http4sVersion

  val circe = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % circeVersion)

  val http4sEmberClient = http4s("blaze-client");
  val http4sEmberServer = http4s("blaze-server");
  val http4sDsl         = http4s("dsl");
  val http4sCirce       = http4s("circe")
  val catEffect         = "org.typelevel" %% "cats-effect"          % catEffectVersion;
  val ciris             = "is.cir"        %% "ciris"                % cirisVersion;
  val circeGenericExtra = "io.circe"      %% "circe-generic-extras" % circeGenericExtraVersion
  val kafka = "com.github.fd4s"           %% "fs2-kafka"            % f2sKafkaVersion
  val elasticSearchClient = {
    "org.elasticsearch.client" % "elasticsearch-rest-high-level-client" % elasticSearchClientVersion
  }
  val doobiePostgres = "org.tpolecat" %% "doobie-postgres" % doobieVersion
  val doobieCore = "org.tpolecat" %% "doobie-core" % doobieVersion
  val doobieHikari = "org.tpolecat" %% "doobie-hikari" % doobieVersion
  val newType = "io.estatico" %% "newtype" % newTypeVersion
}
