package checkable {

  import java.util.Properties
  import scala.collection.JavaConversions._
  import java.net.URL

  trait Source;

  case class FunctionValue(value: Boolean, properties: Map[String, String])

  trait Function {
    def apply(): FunctionValue

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

  trait NumericExpression extends Function1[Unit, BigDecimal] {
    def >(n: NumericExpression): Function1[Unit, Boolean] =
      (Unit) => apply() > n.apply()
    def <(n: NumericExpression): Function1[Unit, Boolean] =
      (Unit) => apply() < n.apply()
    def >=(n: NumericExpression): Function1[Unit, Boolean] =
      (Unit) => apply() >= n.apply()
    def <=(n: NumericExpression): Function1[Unit, Boolean] =
      (Unit) => apply() <= n.apply()
    def *(n: NumericExpression): Function1[Unit, BigDecimal] =
      (Unit) => apply() * n.apply()
    def /(n: NumericExpression): Function1[Unit, BigDecimal] =
      (Unit) => apply() / n.apply()
  }

  trait BooleanExpression extends Function1[Unit, Boolean]

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
    def apply(properties: Map[String, String]): FunctionValue
  }

  trait PropertiesProvider extends Function1[Unit, Map[String, String]]

  class UrlPropertiesFunction(
    provider: PropertiesProvider,
    function: PropertiesFunction) extends Function {
    def apply() = function(provider())
  }

  class UrlPropertiesProvider(url: String) extends PropertiesProvider {
    def get: Map[String, String] = {
      val p = new Properties();
      val is = new URL(url).openStream();
      p.load(is);
      is.close
      p.entrySet()
        .map(x => (x.getKey.toString, x.getValue.toString))
        .toMap
    }
  }

  case class MySource(name: String) extends Source

  class ExampleFunction extends PropertiesFunction {
    def apply(p: Map[String, String]) = {
      p.get("example.interval.ms") match {
        case None => FunctionValue(false, p)
        case x => FunctionValue(x == "100", p)
      }
    }
  }
}