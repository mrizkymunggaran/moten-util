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

  case class NumericExpression(f: Function0[BigDecimal]) extends Function0[BigDecimal] {
    def apply = f.apply

    def >(n: NumericExpression): BooleanExpression =
      BooleanExpression(() => apply > n.apply)
    def <(n: NumericExpression): BooleanExpression =
      BooleanExpression(() => apply < n.apply)
    def >=(n: NumericExpression): BooleanExpression =
      BooleanExpression(() => apply >= n.apply)
    def <=(n: NumericExpression): BooleanExpression =
      BooleanExpression(() => apply <= n.apply)
    def *(n: NumericExpression): Function0[BigDecimal] =
      () => apply * n.apply
    def /(n: NumericExpression): Function0[BigDecimal] =
      () => apply / n.apply
    def equals(n: NumericExpression, precision: BigDecimal) =
      BooleanExpression(() => (apply - n.apply).abs <= precision)
    def ==(n: NumericExpression, precision: BigDecimal) = equals(n, precision)
    def empty: BooleanExpression =
      BooleanExpression(() => apply() == null)
    def milliseconds = this
    def seconds = NumericExpression(() => 1000 * this())
    def minutes = NumericExpression(() => 60 * seconds())
    def hours = NumericExpression(() => 60 * minutes())
    def days = NumericExpression(() => 24 * hours())
    def weeks = NumericExpression(() => 7 * days())
  }

  case class BooleanExpression(f: Function0[Boolean]) extends Function0[Boolean] {

    def apply = f.apply()
    def or(e: BooleanExpression) =
      BooleanExpression(() => f.apply || e.apply)
    def and(e: BooleanExpression) =
      BooleanExpression(() => f.apply || e.apply)
    def not() =
      BooleanExpression(() => !f.apply)
  }

  trait Level

  case class Ok extends Level
  case class Warning extends Level
  case class Failure extends Level

  trait Result

  case class Passed extends Result
  case class Failed extends Result
  case class Unknown extends Result
  case class ExceptionOccurred(throwable: Throwable) extends Unknown

  trait Policy

  trait Checkable extends Function0[Result] {
    val name: String
    val description: String
    val level: Level
    val policies: Set[Policy]
  }

  object PropertiesFunction {

    def notFound(key: String) =
      throw new RuntimeException("value not found for key=" + key)

    def getStringMandatory(properties: Properties, key: String) =
      properties.getOrElse(key, notFound(key))

    def getIntegerMandatory(properties: Properties, key: String) =
      getStringMandatory(properties, key).toInt

    def getLongMandatory(properties: Properties, key: String) =
      getStringMandatory(properties, key).toLong

    def getBigDecimalMandatory(properties: Properties, key: String) =
      BigDecimal(getStringMandatory(properties, key))

  }

  class UrlPropertiesProvider(url: URL) extends PropertiesProvider {
    def apply(): Properties = PropertiesUtil.propertiesToMap(url)
  }

  object MyPropertiesProvider extends UrlPropertiesProvider(PropertiesUtil.getClass().getResource("/test.properties"))

  case class PropsFunction()

  import PropertiesFunction._

  trait PropertiesFunction extends ReturnsResult {

    val properties: () => Properties

    implicit def toNumeric(key: String) =
      NumericExpression(() => properties().get(key) match {
        case None => null
        case x: Option[String] => BigDecimal(x.get)
      })

    implicit def bigDecimalToNumeric(x: BigDecimal) =
      NumericExpression(() => x)

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
    val webApp: String
    val webAppBase: String
    private val url = new URL(webAppBase + "/" + webApp)
    val properties = new UrlPropertiesProvider(url)
  }

}

package amsa {

  import checkable._

  trait AmsaWebAppCheckable extends WebAppPropertiesFunction with Checkable {
    val infoUrl: String
    val webAppBase = "http://sardevc.amsa.gov.au:8080"
  }

  case class FixNextWorkingDay extends Policy
  case class FixImmediate extends Policy

  object SampleWebAppCheckable extends AmsaWebAppCheckable {
    val infoUrl = "http://wiki.amsa.gov.au/sample"
    val webApp = "sample"
    val name = "last processing duration time"
    val description = "processing duration time is acceptable"
    val level: Level = Warning()
    val policies: Set[Policy] = Set(FixNextWorkingDay())
    val expression = ("example.time.hours" empty) or (("example.time.hours" hours) > 100)
  }
}