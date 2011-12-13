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
    val appInfoSchema = "http://moten.david.org/util/xsd/simplified/appinfo"
  }

  case class Sequence(group: ExplicitGroupable)
  case class Choice(group: ExplicitGroupable)

  //every element is either a sequence, choice or simpleType
  // simpleTypes may be based on string, decimal, boolean, date, datetime
  // and may be restricted to a regex pattern, have min, max ranges
  // or be an enumeration. all elements may have  minOccurs and maxOccurs
  //attributes.

  trait Visitor {
    def startSequence(e: Element, sequence: Sequence)
    def endSequence(e: Element, sequence: Sequence)
    def startChoice(e: Element, choice: Choice)
    def startChoiceItem(e: Element, p: ParticleOption)
    def endChoiceItem(e: Element, p: ParticleOption)
    def endChoice(e: Element, choice: Choice)
    def simpleType(e: Element, typ: SimpleType)
    def baseType(e: Element, typ: BaseType)
  }

  class HtmlVisitor extends Visitor {
    import XsdUtil._
    import Util._
    private val t = new StringBuilder
    private val script = new StringBuilder

    private var number = 0

    private def println(x: Any) {
      t.append(x.toString)
      t.append("\n")
    }

    private def addScript(s: String) {
      script.append(s)
      script.append("\n")
    }

    def text =
      header +
        t.toString() + footer

    private def header = {
      val s = new StringBuilder
      s.append(
        """
<html>
<head>
<link rel="stylesheet" href="style.css" type="text/css"/>
<link type="text/css" href="css/smoothness/jquery-ui-1.8.16.custom.css" rel="stylesheet" />	
<link type="text/css" href="css/timepicker.css" rel="stylesheet" />	
<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/jquery-ui-timepicker-addon.js"></script>
<script type="text/javascript">
    $(function() {
      $('input').filter('.datepickerclass').datepicker();
      $('input').filter('.datepickerclass').datepicker( "option", "dateFormat","dd/mm/yy");
      $('input').filter('.datetimepickerclass').datetimepicker();
      $('input').filter('.timepickerclass').timepicker({});
""" + script + """
    });
</script>
</head>
<body>
<div class="form">
""")
      s.toString
    }

    private def footer = "</form>\n</body>\n</html>"

    def startSequence(e: Element, sequence: Sequence) {
      val label = getAnnotation(e, "label").mkString
      println(
        """<div class="sequence">
<div class="sequence-label">""" + label + """</div>
<div class="sequence-content">""")
    }

    def endSequence(e: Element, sequence: Sequence) {
      println("</div>")
      maxOccurs(e, true)
      println("</div>")
    }

    var choiceNumber: String = "?????"

    def startChoice(e: Element, choice: Choice) {
      val choiceNumber = nextNumber
      println("<div class=\"choice\">")
    }

    def startChoiceItem(e: Element, p: ParticleOption) {
      val number = nextNumber
      //println("<input name=\"choice-" + choiceNumber + "\" type=\"radio\" value=\"number\">")
    }

    def endChoiceItem(e: Element, p: ParticleOption) {
      println("</input>")
    }

    def endChoice(e: Element, choice: Choice) {
      println("</div>")
    }

    private case class MyRestriction(qName: QName)
      extends Restriction(None, SimpleRestrictionModelSequence(), None, Some(qName), Map())

    def simpleType(e: Element, typ: SimpleType) {
      val number = nextNumber
      println("<div class=\"item-number\">" + number + "</div>")
      println("<div class=\"item-label\">" + getLabel(e) + "</div>")
      println("<div class=\"item-input\">")
      val en =
        getEnumeration(typ)
      if (!en.isEmpty) {
        enumeration(en, number)
        maxOccurs(e, false)
      } else {
        typ.arg1.value match {
          case x: Restriction => simpleType(e, x, number)
          case _ =>
        }
      }
      println("</div>")
    }

    private def getEnumeration(typ: SimpleType) =
      typ.arg1.value match {
        case x: Restriction =>
          x.arg1.arg2.seq.map(
            _.value match {
              case y: NoFixedFacet => { Some(y.value) }
              case _ => None
            }).flatten
        case _ => unexpected
      }

    private def enumeration(en: Seq[String], number: String) {
      println("<select id=\"select-" + number + "\" class=\"select\">")
      en.foreach { x => println("<option value=\"" + x + "\">" + x + "</option>") }
      println("</select>")
    }

    private def getTextType(e: Element) =
      getAnnotation(e, "text")

    private def nextNumber: String = {
      number += 1
      number + ""
    }

    def baseType(e: Element, typ: BaseType) {
      val number = nextNumber
      println("<div class=\"item-number\">" + number + "</div>")
      println("<div class=\"item-label\">" + getLabel(e) + "</div>")
      println("<div class=\"item-input\">")
      simpleType(e, MyRestriction(typ.qName), number + "")
      println("</div>")
    }

    case class QN(namespace: String, localPart: String)

    implicit def toQN(qName: QName) = QN(qName.getNamespaceURI(), qName.getLocalPart())

    def simpleType(e: Element, r: Restriction, number: String) {
      val qn = toQN(r.base.get)
      val extraClasses =
        qn match {
          case QN(xs, "date") => "datepickerclass "
          case QN(xs, "datetime") => "datetimepickerclass "
          case QN(xs, "time") => "timepickerclass "
          case _ => ""
        }

      val inputType =
        qn match {
          case QN(xs, "boolean") => "checkbox"
          case _ => "text"
        }

      val itemId = "item-" + number

      getTextType(e) match {
        case Some("textarea") =>
          println("<textarea id=\"" + itemId + "\" name=\"item-input-textarea-" + number + "\" class=\"" + extraClasses + "item-input-textarea\"></textarea>")
        case _ =>
          println("<input id=\"" + itemId + "\" name=\"item-input-text-" + number + "\" class=\"" + extraClasses + "item-input-text\" type=\"" + inputType + "\"></input>")
      }

      maxOccurs(e, false)

      getAnnotation(e, "description") match {
        case Some(x) => println("<div class=\"item-description\">" + x + "</div>")
        case None =>
      }

      val itemErrorId = "item-error" + number
      println("<div id=\"" + itemErrorId + "\" class=\"item-error\">" + getAnnotation(e, "validation").getOrElse("Invalid") + "</div>")

      getAnnotation(e, "help") match {
        case Some(x) => println("<div class=\"item-help\">" + x + "</div>")
        case None =>
      }

      //TODO do a logical OR across the patterns
      val patterns = r.arg1.arg2.seq.flatMap(f => {
        f match {
          case DataRecord(xs, Some("pattern"), x: Pattern) => Some(x.value)
          case _ => None
        }
      })

      val lengthTestScriptlet = r.arg1.arg2.seq.flatMap(f => {
        val start = "\n|  //length test\n|  if (v.val().length "
        val finish = ")\n|    ok = false;"
        f match {
          case DataRecord(xs, Some("minLength"), x: NumFacet) => Some(start + "<" + x.value + finish)
          case DataRecord(xs, Some("maxLength"), x: NumFacet) => Some(start + ">" + x.value + finish)
          case DataRecord(xs, Some("length"), x: NumFacet) => Some(start + "==" + x.value + finish)
          case _ => None
        }
      }).mkString("\n")

      val basePattern = qn match {
        case QN(xs, "decimal") => Some("\\d+(\\.\\d*)?")
        case QN(xs, "integer") => Some("\\d+")
        case _ => None
      }

      val restrictions = r.arg1.arg2.seq.flatMap(f => {
        f.value match {
          case x: Pattern => Some(x.value)
          case _ => None
        }
      })

      val facetTestScriptlet = r.arg1.arg2.seq.flatMap(f => {
        val start = "\n|  //facet test\n|  if ((+(v.val())) "
        val finish = ")\n|    ok = false;"

        f match {
          case DataRecord(xs, Some("minInclusive"), x: Facet) =>
            Some(start + "< " + x.value + finish)
          case DataRecord(xs, Some("maxInclusive"), x: Facet) =>
            Some(start + "> " + x.value + finish)
          case DataRecord(xs, Some("minExclusive"), x: Facet) =>
            Some(start + "<=" + x.value + finish)
          case DataRecord(xs, Some("maxExclusive"), x: Facet) =>
            Some(start + ">= " + x.value + finish)
          case _ => None
        }
      }).mkString("\n")

      val declarationScriptlet = """
|$("#""" + itemId + """").blur(function() {
|  // element.name = """ + e.name.get + """
|  var ok = true;
|  var v = $("#""" + itemId + """");
|  var error= $("#""" + itemErrorId + """");"""

      val mandatoryTestScriptlet = if (e.minOccurs.intValue() == 1)
        """
|  // mandatory test
|  if ((v.val() == null) || (v.val().length==0))
|    ok=false;"""
      else ""

      val patternsTestScriptlet = if (patterns.size > 0)
        """
|  // pattern test
|  var regex = /^""" + patterns.first + """$/ ;
|  if (!(regex.test(v.val()))) 
|    ok = false;"""
      else ""

      val basePatternTestScriptlet = if (basePattern.size > 0)
        """    	  
|  // base pattern test
|  var regex = /^""" + basePattern.first + """$/ ;
|  if (!(regex.test(v.val()))) 
|    ok = false;"""
      else ""

      val closingScriptlet =
        """
|  if (!(ok)) 
|    error.show();
|  else 
|    error.hide();
|  // v.clone(true).removeAttr("id").attr("id", "del" + i).insertAfter("#""" + itemId + """");
|})
"""
      val margin = "          "
      val statements = List(
        declarationScriptlet,
        mandatoryTestScriptlet,
        patternsTestScriptlet,
        basePatternTestScriptlet,
        facetTestScriptlet,
        lengthTestScriptlet,
        closingScriptlet)

      statements
        .map(_.stripMargin.replaceAll("\n", "\n" + margin))
        .foreach(addScript(_))

    }

    def maxOccurs(e: Element, isGroup: Boolean) {

      if (e.maxOccurs == "unbounded" || e.maxOccurs.toInt > 1) {
        val label = getAnnotation(e, "addLabel") match {
          case Some(x) => x
          case None => "+"
        }

        println("<div class=\""
          + (if (isGroup) "group-add" else "item-add")
          + " white small\">" + label + "</div>")
      }
    }

    def getAnnotation(e: Annotatedable, key: String): Option[String] =
      e.annotation match {
        case Some(x) => {
          //          println(x.attributes)
          x.attributes.get("@{" + appInfoSchema + "}" + key) match {
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
        val mandatory = e.minOccurs.intValue() > 0
        if (mandatory) label + "<em>*</em>"
        else label
      }
  }

  class Simple(s: Schema, rootElement: String, visitor: Visitor) {
    import Util._
    import XsdUtil._

    private val topLevelElements =
      s.schemasequence1.flatMap(_.arg1.value match {
        case y: TopLevelElement => Some(y)
        case _ => None
      })

    private val topLevelComplexTypes = s.schemasequence1.flatMap(_.arg1.value match {
      case y: TopLevelComplexType => Some(y)
      case _ => None
    })

    private val topLevelSimpleTypes = s.schemasequence1.flatMap(_.arg1.value match {
      case y: TopLevelSimpleType => Some(y)
      case _ => None
    })

    private val targetNs = s.targetNamespace.getOrElse(
      unexpected("schema must have targetNamespace attribute")).toString

    private val schemaTypes =
      (topLevelComplexTypes.map(x => (qn(targetNs, x.name.get), x))
        ++ (topLevelSimpleTypes.map(x => (qn(targetNs, x.name.get), x)))).toMap;

    private val baseTypes =
      Set("decimal", "string", "integer", "date", "dateTime", "time", "boolean")
        .map(new QName(xs, _))

    private def getType(q: QName): AnyRef = {
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

    private case class MyType(typeValue: AnyRef)

    /**
     * Visits the element definition tree.
     */
    def process {

      //      println(s.toString.replaceAllLiterally("(", "(\n"))
      val element = topLevelElements.find(
        _.name match {
          case Some(y) => y equals rootElement
          case None => false
        }).getOrElse(unexpected("did not find element " + rootElement))

      process(element)

    }

    private def process(e: Element) {
      def exception = unexpected("type of element " + e + " is missing")
      e.typeValue match {
        case Some(x: QName) => process(e, MyType(getType(x)))
        case _ => exception
      }
    }

    private def process(e: Element, typeValue: MyType) {
      typeValue.typeValue match {
        case x: TopLevelSimpleType => process(e, x)
        case x: TopLevelComplexType => process(e, x)
        case x: BaseType => process(e, x)
      }
    }

    private def process(e: Element, x: SimpleType) {
      visitor.simpleType(e, x)
    }

    private def process(e: Element, x: ComplexType) {
      x.arg1.value match {
        case x: ComplexContent =>
          unexpected
        case x: SimpleContent =>
          unexpected
        case x: ComplexTypeModelSequence1 =>
          x.arg1.getOrElse(unexpected).value match {
            case y: GroupRef =>
              unexpected
            case y: ExplicitGroupable =>
              if (matches(x.arg1.get, qn("sequence")))
                process(e, Sequence(y))
              else if (matches(x.arg1.get, qn("choice")))
                process(e, Choice(y))
              else unexpected
            case _ => unexpected
          }
      }
    }

    private def process(e: Element, x: BaseType) {
      visitor.baseType(e, x)
    }

    private def process(e: Element, x: Sequence) {
      visitor.startSequence(e, x)
      x.group.arg1.foreach(y => process(e, toQName(y), y.value))
      visitor.endSequence(e, x)
    }

    private def process(e: Element, q: QName, x: ParticleOption) {
      if (q == qn("element")) {
        x match {
          case y: LocalElementable => process(y)
          case _ => unexpected
        }
      } else if (q == qn("choice")) {
        x match {
          case y: ExplicitGroupable => process(e, Choice(y))
          case _ => unexpected
        }
      } else unexpected(q + x.toString)
    }

    private def process(e: Element, x: Choice) {
      visitor.startChoice(e, x)
      x.group.arg1.foreach(y => {
        visitor.startChoiceItem(e, y.value)
        process(e, toQName(y), y.value)
        visitor.endChoiceItem(e, y.value)
      })
      visitor.endChoice(e, x)
    }
  }
}
