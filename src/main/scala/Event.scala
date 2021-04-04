import spray.json.DefaultJsonProtocol._
import spray.json._

import scala.language.{implicitConversions, postfixOps}

final case class Event(eventType: String, data: String, timestamp: Int)

object EventFormat extends JsonFormat[Event] {
  override def write(obj: Event): JsValue = {
    JsObject(
      ("event_type", JsString(obj.eventType)),
      ("data", JsString(obj.data)),
      ("timestamp", JsNumber(obj.timestamp)),
    )
  }

  def read(json: JsValue): Event = json match {
    case JsObject(fields)
      if fields.isDefinedAt("event_type") & fields.isDefinedAt("data") & fields.isDefinedAt("timestamp") =>
      Event(fields("event_type").convertTo[String],
        fields("data").convertTo[String],
        fields("timestamp").convertTo[Int]
      )
    case _ => deserializationError("Not an Event")
  }
}