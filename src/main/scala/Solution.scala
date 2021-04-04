import Pipe._
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives.{complete, get, path}
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.Source
import akka.util.ByteString

import scala.concurrent.duration._
import scala.language.{implicitConversions, postfixOps}
import scala.sys.process._
import scala.util.Try

object Solution {
  implicit val system = ActorSystem("word-counter")
  implicit val executionContext = system.dispatcher

  val sourceDecl = Source.queue[String](bufferSize = 100, OverflowStrategy.backpressure)
  val (sourceMat, source) = sourceDecl.preMaterialize()

  val route =
    path("count") {
      get {
        complete(HttpEntity(
            ContentTypes.`text/plain(UTF-8)`,
            source
              .via(jsonParser)
              .via(eventTypeCountFlow)
              .map(n => ByteString(s"$n\n"))
          ))
      }
    }

  def main(args: Array[String]): Unit = {
    val cmd = "./" + Try(args(0)).toOption.getOrElse("blackbox.macosx")
    val lifeDuration = Duration(Try(args(1).toInt).toOption.getOrElse(30), "seconds")

    Process(cmd).run( new ProcessLogger {
      override def out(s: => String): Unit = sourceMat.offer(s)
      override def err(s: => String): Unit = ()
      override def buffer[T](f: => T): T = f
    })

    val bindingFuture =
      Http().newServerAt("localhost", 8080).bind(route)
        .map(_.addToCoordinatedShutdown(hardTerminationDeadline = 10.seconds))

    system.scheduler.scheduleOnce(lifeDuration) {
      bindingFuture
        .flatMap(_.unbind())
        .onComplete(
          _ => {
            system.terminate()
            println("Server is shut down")
            System.exit(0)
          })
    }

    println("Server online at http://localhost:8080/count")
  }
}