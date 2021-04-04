import akka.actor.ActorSystem
import akka.stream.scaladsl.{Sink, Source}
import org.scalatest.wordspec.AnyWordSpec
import spray.json.{JsNumber, JsObject}

import scala.concurrent.Await
import scala.concurrent.duration._

class PipeSpec extends  AnyWordSpec {

  implicit val system = ActorSystem("test")

  "eventTypeCountFlow" should {
    "count all words grouped by element type" in {

      val events = List(
        Event("type1", "qwerty", 1617560234),
        Event("type2", "qwerty", 1617562342),
        Event("type1", "qwerty", 1617560235),
        Event("type3", "qwerty", 1617560834),
      )

      val future = Source(events).via(Pipe.eventTypeCountFlow)runWith Sink.seq

      val result = Await.result(future, 3.seconds)
      assert(result.last == new JsObject(Map(
        "type1" -> JsNumber(2), "type2" -> JsNumber(1), "type3" -> JsNumber(1),
      )))
    }
  }

  "jsonParser" should {
    "produce Event object only if json has valid format" in {
      val inputStrings = List(
        "{ \"event_type\": \"baz\", \"data\": \"amet\", \"timestamp\": 1617560854 }",
        "{ \"event_type\": 34t%Y#%^Y!#$TR@%T@$%Y",
        "{",
        "{ \"event_type\": \"bar\", \"data\": \"ewwe\", \"timestamp\": 1617560854 }",
        "{ \"event_type\": \"foo\", \"data\": \"durt\"",
        "{ \"event_type\": \"foo\", \"data\": \"durt\", \"timestamp\": 1617560854 }",
        "34j3l4t54t3#%^735"
      )

      val future = Source(inputStrings).via(Pipe.jsonParser)runWith Sink.seq

      val result = Await.result(future, 3.seconds)
      assert(result.size == 3)
      assert(result(0) == Event("baz", "amet", 1617560854))
      assert(result(1) == Event("bar", "ewwe", 1617560854))
      assert(result(2) == Event("foo", "durt", 1617560854))
    }
  }
}
