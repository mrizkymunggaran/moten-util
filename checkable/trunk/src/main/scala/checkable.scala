package checkable {

  import scala.collection.JavaConversions._
  import java.net.URL
  import scala.collection.immutable.Map

  object Util {

    def propertiesToMap(url: String) = {
      val p = new java.util.Properties();
      val is = new URL(url).openStream();
      p.load(is);
      is.close
      p.entrySet()
        .map(x => (x.getKey.toString, x.getValue.toString))
        .toMap
    }

    type Properties = Map[String, String]
  }

  import Util.Properties

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
  }

  trait BooleanExpression extends Function0[Boolean]

  trait Level

  case class Ok extends Level
  case class Warning extends Level
  case class Failure extends Level

  trait Policy {
    val name: String
    val description: String
  }

  trait Checkable {
    val name: String
    val description: String
    val function: Function
    val level: Level
    val policy: Policy
  }

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

  class UrlPropertiesProvider(url: String) extends PropertiesProvider {
    def apply(): Properties = Util.propertiesToMap(url)
  }

  class ExampleFunction extends PropertiesFunction {
    def apply(p: Properties) = {
      p.get("example.interval.ms") match {
        case None => FunctionValue(false, p)
        case x => FunctionValue(x == "100", p)
      }
    }
  }

}