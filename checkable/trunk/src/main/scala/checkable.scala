package checkable {

  import scala.collection.JavaConversions._
  import java.net.URL
  import scala.collection.immutable.Map

  object PropertiesUtil {

    def propertiesToMap(url: URL) = {
      val p = new java.util.Properties();
      val is = url.openStream();
      p.load(is);
      is.close
      p.entrySet()
        .map(x => (x.getKey.toString, x.getValue.toString))
        .toMap
    }

    type Properties = Map[String, String]
    type BooleanExpression = Function0[Boolean]
    type PropertiesProvider = Function0[Properties]
    type ReturnsResult = Function0[Result]
  }

  import PropertiesUtil._

  object NumericExpression {
    def now = NumericExpression(() => System.currentTimeMillis())
  }

  case class NumericExpression(f: () => BigDecimal) extends Function0[BigDecimal] {

    def apply = f()

    def >(n: NumericExpression): BooleanExpression =
      BooleanExpression(() => apply > n.apply)
    def <(n: NumericExpression): BooleanExpression =
      BooleanExpression(() => apply < n.apply)
    def >=(n: NumericExpression): BooleanExpression =
      BooleanExpression(() => apply >= n.apply)
    def <=(n: NumericExpression): BooleanExpression =
      BooleanExpression(() => apply <= n.apply)
    def *(n: NumericExpression): NumericExpression =
      NumericExpression(() => apply * n.apply)
    def +(n: NumericExpression): NumericExpression =
      NumericExpression(() => apply + n.apply)
    def -(n: NumericExpression): NumericExpression =
      NumericExpression(() => apply - n.apply)
    def /(n: NumericExpression): NumericExpression =
      NumericExpression(() => apply / n.apply)
    def equals(n: NumericExpression, precision: BigDecimal) =
      BooleanExpression(() => (apply - n.apply).abs <= precision)
    def ==(n: NumericExpression, precision: BigDecimal) = equals(n, precision)
    def empty: BooleanExpression =
      BooleanExpression(() => apply() == null)
    def notEmpty: BooleanExpression =
      BooleanExpression(() => apply() != null)
    def milliseconds = this
    def seconds = NumericExpression(() => 1000 * this())
    def minutes = NumericExpression(() => 60 * seconds())
    def hours = NumericExpression(() => 60 * minutes())
    def days = NumericExpression(() => 24 * hours())
    def weeks = NumericExpression(() => 7 * days())
  }
  object MyPropertiesProvider extends UrlPropertiesProvider(PropertiesUtil.getClass().getResource("/test.properties"))
  case class BooleanExpression(f: Function0[Boolean]) extends Function0[Boolean] {

    def apply = f.apply()
    def or(e: BooleanExpression) =
      BooleanExpression(() => f.apply || e.apply)
    def and(e: BooleanExpression) =
      BooleanExpression(() => f.apply || e.apply)
    def not() =
      BooleanExpression(() => !f.apply)
    def && = and _
    def || = or _
  }

  object BooleanExpression {
    def urlAvailable(url: String) = BooleanExpression(
      () =>
        {
          val u = new URL(url)
          val is = u.openStream();
          is.close();
          true
        })
  }

  trait Level

  trait Result

  case class Passed extends Result
  case class Failed extends Result
  case class Unknown extends Result
  case class ExceptionOccurred(throwable: Throwable) extends Unknown

  trait Policy

  trait Checkable extends ReturnsResult {
    val name: String
    val description: String
    val level: Level
    val policies: Set[Policy]
  }

  class UrlPropertiesProvider(url: URL) extends PropertiesProvider {
    def apply(): Properties = PropertiesUtil.propertiesToMap(url)
  }

  trait PropertiesFunction extends ReturnsResult {

    val properties: () => Properties

    implicit def toNumeric(key: String) =
      NumericExpression(() => properties().get(key) match {
        case None => null
        case x: Option[String] => BigDecimal(x.get)
      })

    implicit def bigDecimalToNumeric(x: BigDecimal) =
      NumericExpression(() => x)

    implicit def doubleToNumeric(x: Double) =
      NumericExpression(() => BigDecimal(x))

    implicit def integerToNumericExpression(x: Int) =
      NumericExpression(() => BigDecimal(x))

    def apply = {
      try {
        if (expression()) Passed()
        else Failed()
      } catch {
        case e: Throwable => ExceptionOccurred(e)
      }
    }

    def expression: BooleanExpression
  }

  trait WebAppPropertiesFunction extends PropertiesFunction {
    val webapp: String
    val webappBase: String
    private val url = new URL(webappBase + "/" + webapp)
    val properties = new UrlPropertiesProvider(url)
  }

}

package monitoring {

  import scala.actors.Actor
  import scala.actors.Actor._

  class MonitoringProperties {
    import MonitoringPropertiesActor._

    private val actor = new MonitoringPropertiesActor
    private def unexpected = throw new RuntimeException("unexpected")
    def put(key: String, value: String) = actor ! Put(key, value)
    def get(key: String) = actor !? Get(key) match {
      case x: String => x
      case _ => unexpected
    }
    def put(key: String, value: java.util.Date): Unit = put(key, value.getTime())
    def put(key: String, value: Any): Unit = put(key, value.toString)
    def getNumber(key: String) = BigDecimal(get(key))
  }

  object MonitoringPropertiesActor {
    case class Put(key: String, value: String)
    case class Get(key: String)
  }

  class MonitoringPropertiesActor extends Actor {
    //use an Actor to handle concurrent access to the internal properties object safely
    import MonitoringPropertiesActor._

    def act =
      {
        var properties = Map[String, String]()
        loop {
          react {
            case x: Put => properties += x.key -> x.value
            case x: Get => reply(properties.get(x.key))
          }
        }
      }
  }

}

package amsa {

  import checkable._

  object AmsaCheckable {
    val lastProcessDuration = "last.process.duration.ms"
    val lastRunTime = "last.run.time";
    val applicationStartedTime = "application.started.time"
  }

  trait AmsaCheckable extends Checkable {
    val wikiBase = "http://wiki.amsa.gov.au/index.php?title="
    val wikiTitle: String
    def infoUrl: String = wikiBase + wikiTitle
    val webappBase = "http://sardevc.amsa.gov.au:8080"
  }

  trait AmsaWebAppCheckable extends WebAppPropertiesFunction
    with AmsaCheckable {
    def date(s: String): NumericExpression = {
      (s + ".epoch.ms")
    }
  }

  case class Ok extends Level
  case class Warning extends Level
  case class Failure extends Level

  case class FixNextWorkingDay extends Policy
  case class FixImmediate extends Policy
  case class NotifyOncall extends Policy
  case class NotifyOperators extends Policy
  case class NotifyAdministrator extends Policy

  import NumericExpression._
  import AmsaCheckable._

  object MyPropertiesProvider extends UrlPropertiesProvider(PropertiesUtil.getClass().getResource("/test.properties"))

  class SampleWebAppCheckable extends AmsaWebAppCheckable {
    val wikiTitle = "Sample_Web_App"
    val webapp = "sample"
    val name = "last processing duration time"
    val description = "processing duration time is acceptable"
    val level: Level = Warning()
    val policies: Set[Policy] = Set(FixNextWorkingDay(),
      NotifyAdministrator(), NotifyOncall())
    val expression =
      ((lastProcessDuration empty)
        or ((lastProcessDuration hours) < 0.5)) and
        (((date(lastRunTime) empty)
          and
          (date(applicationStartedTime) < (now - (1 hours))))
          or
          (date(lastRunTime) > (now - (2 days))))
  }

}