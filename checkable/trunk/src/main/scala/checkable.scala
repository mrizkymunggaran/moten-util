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
    type Function = Function0[FunctionValue]
  }

  import PropertiesUtil._

  case class FunctionValue(value: Boolean, properties: Properties)

  case class NumericExpression(f: Function0[BigDecimal]) extends Function0[BigDecimal] {
    def apply = f.apply

    def >(n: NumericExpression): BooleanExpression =
      BooleanExpression(() => apply() > n.apply())
    def <(n: NumericExpression): BooleanExpression =
      BooleanExpression(() => apply() < n.apply())
    def >=(n: NumericExpression): BooleanExpression =
      BooleanExpression(() => apply() >= n.apply())
    def <=(n: NumericExpression): BooleanExpression =
      BooleanExpression(() => apply() <= n.apply())
    def *(n: NumericExpression): Function0[BigDecimal] =
      () => apply() * n.apply()
    def /(n: NumericExpression): Function0[BigDecimal] =
      () => apply() / n.apply()
    def empty: BooleanExpression =
      BooleanExpression(() => apply() == null)
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

  trait Policy {
    val name: String
    val description: String
  }

  class Checkable(
    name: String,
    description: String,
    check: Function,
    level: Level,
    policy: Policy)

  trait PropertiesFunction {
    def apply(properties: Properties): FunctionValue

    def getStringMandatory(properties: Properties, key: String) =
      properties.getOrElse(key,
        throw new RuntimeException("value not found for key=" + key))

    def getIntegerMandatory(properties: Properties, key: String) =
      getStringMandatory(properties, key).toInt

    def getLongMandatory(properties: Properties, key: String) =
      getStringMandatory(properties, key).toLong

    def getBigDecimalMandatory(properties: Properties, key: String) =
      BigDecimal(getStringMandatory(properties, key))

    implicit def stringToNumeric(properties: Properties)(key: String) =
      NumericExpression(() => properties.get(key) match {
        case None => null
        case x: Option[String] => BigDecimal(x.get)
      })

    implicit def stringToNumeric(x: BigDecimal) =
      NumericExpression(() => x)

    implicit def integerToNumericExpression(x: Int) =
      NumericExpression(BigDecimal(x))
  }

  trait PropertiesProvider extends Function0[Properties]

  class UrlPropertiesFunction(
    provider: PropertiesProvider,
    function: PropertiesFunction) extends Function {
    def apply() = function(provider())
  }

  class UrlPropertiesProvider(url: URL) extends PropertiesProvider {
    def apply(): Properties = PropertiesUtil.propertiesToMap(url)
  }

  object MyPropertiesProvider extends UrlPropertiesProvider(PropertiesUtil.getClass().getResource("/test.properties"))

  object MyPropertiesFunction extends PropertiesFunction {
    def apply(properties: Properties) = {
      implicit def toNumeric = stringToNumeric(properties)_
      val exp: BooleanExpression = ("example.time.ms" empty) or ("example.time.ms" > 100)
      FunctionValue(exp.apply(), properties)
    }
  }

}