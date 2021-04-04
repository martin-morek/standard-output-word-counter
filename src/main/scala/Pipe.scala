import akka.NotUsed
import akka.stream.scaladsl.Flow
import spray.json.DefaultJsonProtocol._
import spray.json._

import scala.language.{implicitConversions, postfixOps}
import scala.util.{Success, Try}

object Pipe {
  implicit val eventFormat = EventFormat

  type Count = Int
  type EventTypeCount = Map[String, Count]

  val zeroCount : EventTypeCount = Map.empty[String, Count] withDefaultValue 0

  val appendEventTypeToCount : (EventTypeCount, Event) => EventTypeCount =
    (acc, event) => acc + (event.eventType -> (acc(event.eventType) + 1))


  val eventTypeCountFlow: Flow[Event, JsValue, NotUsed] =
    Flow[Event].scan(zeroCount)(appendEventTypeToCount).map(_.toJson)

  val jsonParser: Flow[String, Event, NotUsed] =
    Flow[String]
      .map(s => {
        Try(s.parseJson.convertTo[Event])
      }).collect{case Success(e) => e}
}
