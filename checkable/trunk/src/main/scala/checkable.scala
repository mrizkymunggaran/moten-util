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
  }

  import PropertiesUtil._

  case class FunctionValue(value: Boolean, properties: Properties)

  trait Function extends Function0[FunctionValue] {

    def and(f: Function) = {
      val v = apply
      val v2 = f()
      FunctionValue(v.value && v2.value, v.properties)
    }

    def or(f: Function) = {
      val v = apply
      val v2 = f()
      FunctionValue(v.value || v2.value, v.properties)
    }

    def not = {
      val v = apply
      FunctionValue(!v.value, v.properties)
    }
  }

  trait NumericExpression extends Function0[BigDecimal] {

    def >(n: NumericExpression): Function0[Boolean] =
      () => apply() > n.apply()
    def <(n: NumericExpression): Function0[Boolean] =
      () => apply() < n.apply()
    def >=(n: NumericExpression): Function0[Boolean] =
      () => apply() >= n.apply()
    def <=(n: NumericExpression): Function0[Boolean] =
      () => apply() <= n.apply()
    def *(n: NumericExpression): Function0[BigDecimal] =
      () => apply() * n.apply()
    def /(n: NumericExpression): Function0[BigDecimal] =
      () => apply() / n.apply()
    def isNull: Function0[Boolean] =
      () => apply() == null
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

  object Util {

    def keyNotFound(key: String) = throw new RuntimeException("key not found=" + key)

    implicit def stringToNumeric(properties: Properties)(key: String) =
      new NumericExpression() {
        def apply =
          properties.get(key) match {
            case None => null
            case x: Option[String] => BigDecimal(x.get)
          }
      }

    implicit def stringToNumeric(x: BigDecimal) =
      new NumericExpression() {
        def apply = x
      }

    implicit def integerToNumericExpression(x: Int) =
      new NumericExpression() {
        def apply = BigDecimal(x)
      }

  }

  object MyPropertiesProvider extends UrlPropertiesProvider(Util.getClass().getResource("/test.properties"))

  import Util._

  object MyPropertiesFunction extends PropertiesFunction {
    def apply(properties: Properties) = {
      implicit def toNumeric = stringToNumeric(properties) _
      val exp: BooleanExpression = ("example.time.ms".isNull) or ("example.time.ms" > 100)
      FunctionValue(exp.apply(), properties)
    }
  }

}