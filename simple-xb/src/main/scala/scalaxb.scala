package simple {

  import xsd._
  import xsd.ComplexTypeModelSequence1
  import javax.xml.namespace.QName
  import scalaxb._

  case class BaseType(qName: QName)

  object Util {
    def unexpected(s: String) = throw new RuntimeException(s)
    def unexpected() = throw new RuntimeException()
  }

  object XsdUtil {
    def qn(namespaceUri: String, localPart: String) = new QName(namespaceUri, localPart)
    def qn(localPart: String): QName = new QName(xs, localPart)
    val xs = "http://www.w3.org/2001/XMLSchema"
  }

  case class Sequence(group: ExplicitGroupable)
  case class Choice(group: ExplicitGroupable)

  //every element is either a sequence, choice or simpleType
  // simpleTypes may be based on string, decimal, boolean, date, datetime
  // and may be restricted to a regex pattern, have min, max ranges
  // or be an enumeration. all elements may have  minOccurs and maxOccurs
  //attributes.

  trait Visitor {
    def startSequence(sequence: Sequence)
    def endSequence(sequence: Sequence)
    def startChoice(choice: Choice)
    def endChoice(choice: Choice)
    def simpleType(e: Element, typ: SimpleType)
    def baseType(e: Element, typ: BaseType)
  }

  class HtmlVisitor extends Visitor {
    import XsdUtil._
    import Util._
    private val t = new StringBuilder

    private var number = 0

    private def println(x: Any) = {
      t.append(x.toString)
      t.append("\n")
    }

    def text =
      header +
        t.toString() + footer

    private def header = {
      val s = new StringBuilder
      s.append(
        """<html>
<head>
<link rel="stylesheet" href="style.css" type="text/css"/>
<link type="text/css" href="css/smoothness/jquery-ui-1.8.16.custom.css" rel="stylesheet" />	
<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript">
	$(function() {
		$('input').filter('.datepickerclass').datepicker()
	});
</script>
</head>
<body>
<div class="form">
""")
      s.toString
    }

    private def footer = "</form>\n</body>\n</html>"

    def startSequence(sequence: Sequence) {
      println(
        """<div class="sequence">
<div class="sequence-label">Group</div>
<div class="sequence-content">""")
    }

    def endSequence(sequence: Sequence) {
      println("</div>")
      println("</div>")
    }
    def startChoice(choice: Choice) {
      println("<div class=\"choice\">")
    }
    def endChoice(choice: Choice) {
      println("</div>")
    }

    def simpleType(e: Element, typ: SimpleType) {
      number += 1
      println("<div class=\"item-number\">" + number + "</div>")
      println("<div class=\"item-label\">" + getLabel(e) + "</div>")
      println("<div class=\"item-input\">")
      val enumeration =
        typ.arg1.value match {
          case x: Restriction =>
            x.arg1.arg2.seq.map(
              _.value match {
                case y: NoFixedFacet => { Some(y.value) }
                case _ => None
              }).flatten
          case _ => unexpected
        }
      if (!enumeration.isEmpty) {
        println("<select class=\"select\">")
        enumeration.foreach { x => println("<option value=\"" + x + "\">" + x + "</option>") }
        println("</select>")
      } else
        getTextType(e) match {
          case Some("textarea") =>
            println("<textarea name=\"item-input-textarea\" class=\"item-input-textarea\"></textarea>")
          case _ =>
            println("<input name=\"item-input-n\" class=\"item-input-text\" type=\"text\"></input>")
        }
      println("</div>")
    }

    private def getTextType(e: Element) =
      getAnnotation(e, "text")

    def baseType(e: Element, typ: BaseType) {
      number += 1
      println("<div class=\"item-number\">" + number + "</div>")
      println("<div class=\"item-label\">" + getLabel(e) + "</div>")
      println("<div class=\"item-input\">")
      val extraClasses = if (typ.qName == qn("date")) "datepickerclass " else ""
      val inputType = if (typ.qName == qn("boolean")) "checkbox" else "text"
      getTextType(e) match {
        case Some("textarea") =>
          println("<textarea name=\"item-input-textarea-" + number + "\" class=\"item-input-textarea\"></textarea>")
        case _ =>
          println("<input name=\"item-input-text-" + number + "\" class=\"item-input-text\" type=\"text\"></input>")
      }
      //println("<input name=\"item-input-n\" class=\"" + extraClasses + "item-input-text\" type=\"" + inputType + "\"></input>")
      println("</div>")
    }

    def getAnnotation(e: Element, key: String): Option[String] =
      e.annotation match {

        case Some(x) => {
          //          println(x.attributes)
          x.attributes.get("@{http://moten.david.org/util/xsd/simplified/appinfo}" + key) match {
            case Some(y) => Some(y.value.toString)
            case None => None
          }
        }
        case None => None
      }

    def getLabel(e: Element) =
      {
        val name = e.name.get
          .replaceAll("-", " ")
          .replaceAll("_", " ")
          .split(" ")
          .map(s => Character.toUpperCase(s.charAt(0)) + s.substring(1, s.length))
          .mkString(" ")
        val label = getAnnotation(e, "label") match {
          case Some(x) => x
          case _ => name
        }
        val mandatory = e.minOccurs.intValue()>0
        if (mandatory) label + "<em>*</em>"
        else label
      }
  }

  class Simple(s: Schema, rootElement: String, visitor: Visitor) {
    import Util._
    import XsdUtil._

    val topLevelElements =
      s.schemasequence1.flatMap(_.arg1.value match {
        case y: TopLevelElement => Some(y)
        case _ => None
      })

    val topLevelComplexTypes = s.schemasequence1.flatMap(_.arg1.value match {
      case y: TopLevelComplexType => Some(y)
      case _ => None
    })

    val topLevelSimpleTypes = s.schemasequence1.flatMap(_.arg1.value match {
      case y: TopLevelSimpleType => Some(y)
      case _ => None
    })

    val targetNs = s.targetNamespace.getOrElse(
      unexpected("schema must have targetNamespace attribute")).toString

    val schemaTypes =
      (topLevelComplexTypes.map(x => (qn(targetNs, x.name.get), x))
        ++ (topLevelSimpleTypes.map(x => (qn(targetNs, x.name.get), x)))).toMap;

    val baseTypes =
      Set("decimal", "string", "integer", "date", "dateTime", "boolean")
        .map(new QName(xs, _))

    def getType(q: QName): AnyRef = {
      schemaTypes.get(q) match {
        case Some(x: Annotatedable) => return x
        case _ =>
          if (baseTypes contains q) return BaseType(q)
          else unexpected("unrecognized type: " + q)
      }
    }

    private def toQName[T](d: DataRecord[T]) =
      new QName(d.namespace.getOrElse(null), d.key.getOrElse(null))

    private def matches[T](d: DataRecord[T], q: QName) =
      toQName(d).equals(q)

    def process(x: Sequence) {
      visitor.startSequence(x)
      x.group.arg1.foreach(y => process(toQName(y), y.value))
      visitor.endSequence(x)
    }

    case class MyType(typeValue: AnyRef)

    def process(e: Element) {
      def exception = unexpected("type of element " + e + " is missing")
      e.typeValue match {
        case Some(x: QName) => process(e, MyType(getType(x)))
        case _ => exception
      }
    }

    def process(e: Element, typeValue: MyType) {
      typeValue.typeValue match {
        case x: TopLevelSimpleType => process(e, x)
        case x: TopLevelComplexType => process(e, x)
        case x: BaseType => process(e, x)
      }
    }

    def process(q: QName, x: ParticleOption) {
      if (q == qn("element")) {
        x match {
          case y: LocalElementable => process(y)
          case _ => unexpected
        }
      } else if (q == qn("choice")) {
        x match {
          case y: ExplicitGroupable => process(Choice(y))
          case _ => unexpected
        }
      } else unexpected(q + x.toString)
    }

    def process(x: Choice) {
      visitor.startChoice(x)
      x.group.arg1.foreach(y => process(toQName(y), y.value))
      visitor.endChoice(x)
    }

    def process(e: Element, x: ComplexType) {
      x.arg1.value match {
        case x: ComplexContent =>
          unexpected
        case x: SimpleContent =>
          unexpected
        //          x.simplecontentoption.value match {
        //            case y: SimpleRestrictionType =>
        //              unexpected
        //            case y: SimpleExtensionType =>
        //              unexpected
        //            case _ => unexpected
        //          }
        case x: ComplexTypeModelSequence1 =>
          x.arg1.getOrElse(unexpected).value match {
            case y: GroupRef =>
              unexpected
            case y: ExplicitGroupable =>
              if (matches(x.arg1.get, qn("sequence")))
                process(Sequence(y))
              else if (matches(x.arg1.get, qn("choice")))
                process(Choice(y))
              else unexpected
            case _ => unexpected
          }
      }
    }

    def process(e: Element, x: SimpleType) {
      visitor.simpleType(e, x)
    }
    def process(e: Element, x: BaseType) {
      visitor.baseType(e, x)
      //      val name = e.name.get
      //      x.qName.getLocalPart() match {
      //        case "string" => println(name + ": [TextBox]")
      //        case "date" => println(name + ": [DatePicker]")
      //        case "dateTime" => println(name + ": [DateTimePicker]")
      //        case "boolean" => println(name + ": [CheckBox]")
      //        case "integer" => println(name + ": [TextBox]")
      //        case "decimal" => println(name + ": [TextBox]")
      //        case _ => unexpected(name + ":" + x)
      //      }
    }

    def process {

      //      println(s)
      //      println
      //
      //      println("\ntopLevelComplexTypes:")
      //      println(topLevelComplexTypes)
      //      println("\ntopLevelSimpleTypes:")
      //      println(topLevelSimpleTypes)
      //
      //      println("\ntopLevelElements:")
      //      println(topLevelElements)
      //      println
      //
      //      println(schemaTypes)

      val element = topLevelElements.find(
        _.name match {
          case Some(y) => y equals rootElement
          case None => false
        }).getOrElse(unexpected("did not find element " + rootElement))

      //      println(element)

      process(element)

    }
  }
}