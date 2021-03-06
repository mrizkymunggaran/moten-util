package simple {
  import org.junit.Test
  import scalaxb.DataRecord
  import xsd._
  import xsd.ComplexTypeModelSequence1
  import javax.xml.namespace.QName
  import scalaxb._

  case class BaseType(qName: QName)

  object Simple {
    def main(args: Array[String]) {
      val visitor = new HtmlVisitor
      new Simple(visitor).process
      println(visitor.text)
      val fos = new java.io.FileOutputStream("target/test.html");
      fos.write(visitor.text.getBytes)
      fos.close
    }
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
    private val t = new StringBuilder

    private def println(x: Any) = {
      t.append(x.toString)
      t.append("\n")
    }

    def text = 
       header +
        t.toString()  +footer

    private def header = {
      val s = new StringBuilder
      s.append("<html>\n")
      s.append("<head>\n")
      s.append("<link rel=StyleSheet href=\"style.css\" type=\"text/css\"/>\n")
      s.append("<script type=\"text/javascript\" src=\"jquery.js\"></script>\n")
      s.append("</head>\n")
      s.append("<body>\n")
      s.append("<form class=\"form\">\n")
      s.toString
    }
    
    private def footer = "</form>\n</body>\n</html>"

    def startSequence(sequence: Sequence) {
      println("<div class=\"sequence\">")
      println("<div class=\"sequence-label\">Group</div>")
      println("<div class=\"sequence-content\">")
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
      println("<div class=\"item-label\">" + e.name.get + "</div>")
      println("<div class=\"item-input\">")
      println("<input name=\"item-input-n\" class=\"item-input-text\" type=\"text\"></input>")
      println("</div>")
    }
    def baseType(e: Element, typ: BaseType) {
      //println(e.name.get + " " +  typ.qName)
    }
  }

  class Simple(visitor: Visitor) {
    val s = scalaxb.fromXML[Schema](getXml)

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

    val xs = "http://www.w3.org/2001/XMLSchema"

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

    def qn(namespaceUri: String, localPart: String) = new QName(namespaceUri, localPart)
    def qn(localPart: String): QName = new QName(xs, localPart)

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
          //          case y: GroupRef => unexpected
          //          case y: Allable => unexpected
          //          case y: AnyType => unexpected
          //          case y: ExplicitGroupable => unexpected //process(Sequence(y))
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

    def unexpected(s: String) = throw new RuntimeException(s)
    def unexpected() = throw new RuntimeException()

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

      val rootElement = "person"
      val element = topLevelElements.find(
        _.name match {
          case Some(y) => y equals rootElement
          case None => false
        }).getOrElse(unexpected("did not find element " + rootElement))

      //      println(element)

      process(element)
      
    }

    def getXml =
      <xs:schema targetNamespace="http://org.moten.david/example" xmlns="http://org.moten.david/example" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:i="http://moten.david.org/util/xsd/simplified/appinfo">
        <xs:annotation i:numberItems="true"/>
        <xs:element name="person" type="person">
          <xs:annotation i:label="Personal details"/>
        </xs:element>
        <xs:complexType name="person">
          <xs:sequence>
            <xs:element name="name" type="xs:string">
              <xs:annotation i:label="Full name"/>
            </xs:element>
            <xs:element name="email" type="email">
              <xs:annotation i:validation="Invalid format" i:after="If you have concerns about privacy please see our &lt;a href=&quot;http://google.com&quot;&gt;privacy statement&lt;a&gt;"/>
            </xs:element>
            <xs:element name="date-of-birth" type="xs:date">
              <xs:annotation i:before="Date of birth is required for age based statistical analysis but will not be available publically" i:label="Date of birth" i:description="dd/mm/yyyy (dd=day, mm=month, yyyy=year)" i:validation="This field must of the form dd/mm/yyyy. For example, 12 March 2011 is 12/03/2011" i:after="If you have concerns about privacy please see our &lt;a href=&quot;http://google.com&quot;&gt;privacy statement&lt;a&gt;"/>
            </xs:element>
            <xs:element name="passport-no" type="passport-no">
              <xs:annotation i:label="Passport Number" i:description="L followed by digits" i:validation="This field must start with an L and then follow with digits only"/>
            </xs:element>
            <xs:element name="permanent-resident" type="xs:boolean"/>
            <xs:element name="last-submission" type="xs:dateTime" minOccurs="0"/>
            <xs:element name="lived-in" type="country" maxOccurs="unbounded">
              <xs:annotation i:description="Select the current country name for the location you lived in. You may select multiple locations by clicking on the Add button."/>
            </xs:element>
            <xs:element name="number-of-children" type="xs:integer" default="0"/>
            <xs:choice>
              <xs:element name="address" type="xs:string">
                <xs:annotation i:label="Address" i:lines="4" i:cols="40"/>
              </xs:element>
              <xs:element name="phone" type="xs:string" maxOccurs="unbounded"/>
              <xs:element name="other" type="other">
                <xs:annotation i:glass="true"/>
              </xs:element>
            </xs:choice>
            <xs:element name="code" type="code">
              <xs:annotation i:label="Identifier Code" i:validation="This field must be a whole number between 10 and 20 inclusive"/>
            </xs:element>
            <xs:element name="story" type="xs:string" minOccurs="0">
              <xs:annotation i:lines="10" i:cols="50"/>
            </xs:element>
            <xs:element name="height-m" type="xs:decimal">
              <xs:annotation i:label="Height in metres"/>
            </xs:element>
            <xs:element name="termsAndConditionsUnderstood" type="alwaysTrue">
              <xs:annotation i:label="I acknowledge the &lt;a href=''&gt;Terms and Conditions&gt;"/>
            </xs:element>
          </xs:sequence>
        </xs:complexType>
        <xs:complexType name="other">
          <xs:sequence>
            <xs:element name="other1" type="xs:string"/>
            <xs:element name="other2" type="xs:string"/>
          </xs:sequence>
        </xs:complexType>
        <xs:simpleType name="alwaysTrue">
          <xs:restriction base="xs:boolean">
            <xs:enumeration value="true"/>
          </xs:restriction>
        </xs:simpleType>
        <xs:simpleType name="passport-no">
          <xs:restriction base="xs:string">
            <xs:pattern value="L[0-9]+"/>
          </xs:restriction>
        </xs:simpleType>
        <xs:simpleType name="email">
          <xs:restriction base="xsd:string">
            <xs:pattern value="^([0-9a-zA-Z]([-.\w]*[0-9a-zA-Z])*@([0-9a-zA-Z][-\w]*[0-9a-zA-Z]\.)+[a-zA-Z]{2,9})$"/>
          </xs:restriction>
        </xs:simpleType>
        <!-- List source : http://geotags.com/iso3166/countries.html -->
        <xs:simpleType name="country">
          <xs:restriction base="xs:string">
            <xs:enumeration value="Afghanistan"/>
            <xs:enumeration value="Albania"/>
            <xs:enumeration value="Algeria"/>
            <xs:enumeration value="Andorra"/>
            <xs:enumeration value="Angola"/>
            <xs:enumeration value="Antigua &amp; Deps"/>
            <xs:enumeration value="Argentina"/>
            <xs:enumeration value="Armenia"/>
            <xs:enumeration value="Australia">
              <xs:annotation i:label="Van Diemen's Land"/>
            </xs:enumeration>
            <xs:enumeration value="Austria"/>
            <xs:enumeration value="Azerbaijan"/>
            <xs:enumeration value="Bahamas"/>
            <xs:enumeration value="Bahrain"/>
            <xs:enumeration value="Bangladesh"/>
            <xs:enumeration value="Barbados"/>
            <xs:enumeration value="Belarus"/>
            <xs:enumeration value="Belgium"/>
            <xs:enumeration value="Belize"/>
            <xs:enumeration value="Benin"/>
            <xs:enumeration value="Bhutan"/>
            <xs:enumeration value="Bolivia"/>
            <xs:enumeration value="Bosnia Herzegovina"/>
            <xs:enumeration value="Botswana"/>
            <xs:enumeration value="Brazil"/>
            <xs:enumeration value="Brunei"/>
            <xs:enumeration value="Bulgaria"/>
            <xs:enumeration value="Burkina"/>
            <xs:enumeration value="Burundi"/>
            <xs:enumeration value="Cambodia"/>
            <xs:enumeration value="Cameroon"/>
            <xs:enumeration value="Canada"/>
            <xs:enumeration value="Cape Verde"/>
            <xs:enumeration value="Central African Rep"/>
            <xs:enumeration value="Chad"/>
            <xs:enumeration value="Chile"/>
            <xs:enumeration value="China"/>
            <xs:enumeration value="Colombia"/>
            <xs:enumeration value="Comoros"/>
            <xs:enumeration value="Congo"/>
            <xs:enumeration value="Congo {Democratic Rep}"/>
            <xs:enumeration value="Costa Rica"/>
            <xs:enumeration value="Croatia"/>
            <xs:enumeration value="Cuba"/>
            <xs:enumeration value="Cyprus"/>
            <xs:enumeration value="Czech Republic"/>
            <xs:enumeration value="Denmark"/>
            <xs:enumeration value="Djibouti"/>
            <xs:enumeration value="Dominica"/>
            <xs:enumeration value="Dominican Republic"/>
            <xs:enumeration value="East Timor"/>
            <xs:enumeration value="Ecuador"/>
            <xs:enumeration value="Egypt"/>
            <xs:enumeration value="El Salvador"/>
            <xs:enumeration value="Equatorial Guinea"/>
            <xs:enumeration value="Eritrea"/>
            <xs:enumeration value="Estonia"/>
            <xs:enumeration value="Ethiopia"/>
            <xs:enumeration value="Fiji"/>
            <xs:enumeration value="Finland"/>
            <xs:enumeration value="France"/>
            <xs:enumeration value="Gabon"/>
            <xs:enumeration value="Gambia"/>
            <xs:enumeration value="Georgia"/>
            <xs:enumeration value="Germany"/>
            <xs:enumeration value="Ghana"/>
            <xs:enumeration value="Greece"/>
            <xs:enumeration value="Grenada"/>
            <xs:enumeration value="Guatemala"/>
            <xs:enumeration value="Guinea"/>
            <xs:enumeration value="Guinea-Bissau"/>
            <xs:enumeration value="Guyana"/>
            <xs:enumeration value="Haiti"/>
            <xs:enumeration value="Honduras"/>
            <xs:enumeration value="Hungary"/>
            <xs:enumeration value="Iceland"/>
            <xs:enumeration value="India"/>
            <xs:enumeration value="Indonesia"/>
            <xs:enumeration value="Iran"/>
            <xs:enumeration value="Iraq"/>
            <xs:enumeration value="Ireland {Republic}"/>
            <xs:enumeration value="Israel"/>
            <xs:enumeration value="Italy"/>
            <xs:enumeration value="Ivory Coast"/>
            <xs:enumeration value="Jamaica"/>
            <xs:enumeration value="Japan"/>
            <xs:enumeration value="Jordan"/>
            <xs:enumeration value="Kazakhstan"/>
            <xs:enumeration value="Kenya"/>
            <xs:enumeration value="Kiribati"/>
            <xs:enumeration value="Korea North"/>
            <xs:enumeration value="Korea South"/>
            <xs:enumeration value="Kosovo"/>
            <xs:enumeration value="Kuwait"/>
            <xs:enumeration value="Kyrgyzstan"/>
            <xs:enumeration value="Laos"/>
            <xs:enumeration value="Latvia"/>
            <xs:enumeration value="Lebanon"/>
            <xs:enumeration value="Lesotho"/>
            <xs:enumeration value="Liberia"/>
            <xs:enumeration value="Libya"/>
            <xs:enumeration value="Liechtenstein"/>
            <xs:enumeration value="Lithuania"/>
            <xs:enumeration value="Luxembourg"/>
            <xs:enumeration value="Macedonia"/>
            <xs:enumeration value="Madagascar"/>
            <xs:enumeration value="Malawi"/>
            <xs:enumeration value="Malaysia"/>
            <xs:enumeration value="Maldives"/>
            <xs:enumeration value="Mali"/>
            <xs:enumeration value="Malta"/>
            <xs:enumeration value="Marshall Islands"/>
            <xs:enumeration value="Mauritania"/>
            <xs:enumeration value="Mauritius"/>
            <xs:enumeration value="Mexico"/>
            <xs:enumeration value="Micronesia"/>
            <xs:enumeration value="Moldova"/>
            <xs:enumeration value="Monaco"/>
            <xs:enumeration value="Mongolia"/>
            <xs:enumeration value="Montenegro"/>
            <xs:enumeration value="Morocco"/>
            <xs:enumeration value="Mozambique"/>
            <xs:enumeration value="Myanmar, {Burma}"/>
            <xs:enumeration value="Namibia"/>
            <xs:enumeration value="Nauru"/>
            <xs:enumeration value="Nepal"/>
            <xs:enumeration value="Netherlands"/>
            <xs:enumeration value="New Zealand"/>
            <xs:enumeration value="Nicaragua"/>
            <xs:enumeration value="Niger"/>
            <xs:enumeration value="Nigeria"/>
            <xs:enumeration value="Norway"/>
            <xs:enumeration value="Oman"/>
            <xs:enumeration value="Pakistan"/>
            <xs:enumeration value="Palau"/>
            <xs:enumeration value="Panama"/>
            <xs:enumeration value="Papua New Guinea"/>
            <xs:enumeration value="Paraguay"/>
            <xs:enumeration value="Peru"/>
            <xs:enumeration value="Philippines"/>
            <xs:enumeration value="Poland"/>
            <xs:enumeration value="Portugal"/>
            <xs:enumeration value="Qatar"/>
            <xs:enumeration value="Romania"/>
            <xs:enumeration value="Russian Federation"/>
            <xs:enumeration value="Rwanda"/>
            <xs:enumeration value="St Kitts &amp; Nevis"/>
            <xs:enumeration value="St Lucia"/>
            <xs:enumeration value="Saint Vincent &amp; the Grenadines"/>
            <xs:enumeration value="Samoa"/>
            <xs:enumeration value="San Marino"/>
            <xs:enumeration value="Sao Tome &amp; Principe"/>
            <xs:enumeration value="Saudi Arabia"/>
            <xs:enumeration value="Senegal"/>
            <xs:enumeration value="Serbia"/>
            <xs:enumeration value="Seychelles"/>
            <xs:enumeration value="Sierra Leone"/>
            <xs:enumeration value="Singapore"/>
            <xs:enumeration value="Slovakia"/>
            <xs:enumeration value="Slovenia"/>
            <xs:enumeration value="Solomon Islands"/>
            <xs:enumeration value="Somalia"/>
            <xs:enumeration value="South Africa"/>
            <xs:enumeration value="Spain"/>
            <xs:enumeration value="Sri Lanka"/>
            <xs:enumeration value="Sudan"/>
            <xs:enumeration value="Suriname"/>
            <xs:enumeration value="Swaziland"/>
            <xs:enumeration value="Sweden"/>
            <xs:enumeration value="Switzerland"/>
            <xs:enumeration value="Syria"/>
            <xs:enumeration value="Taiwan"/>
            <xs:enumeration value="Tajikistan"/>
            <xs:enumeration value="Tanzania"/>
            <xs:enumeration value="Thailand"/>
            <xs:enumeration value="Togo"/>
            <xs:enumeration value="Tonga"/>
            <xs:enumeration value="Trinidad &amp; Tobago"/>
            <xs:enumeration value="Tunisia"/>
            <xs:enumeration value="Turkey"/>
            <xs:enumeration value="Turkmenistan"/>
            <xs:enumeration value="Tuvalu"/>
            <xs:enumeration value="Uganda"/>
            <xs:enumeration value="Ukraine"/>
            <xs:enumeration value="United Arab Emirates"/>
            <xs:enumeration value="United Kingdom"/>
            <xs:enumeration value="United States"/>
            <xs:enumeration value="Uruguay"/>
            <xs:enumeration value="Uzbekistan"/>
            <xs:enumeration value="Vanuatu"/>
            <xs:enumeration value="Vatican City"/>
            <xs:enumeration value="Venezuela"/>
            <xs:enumeration value="Vietnam"/>
            <xs:enumeration value="Yemen"/>
            <xs:enumeration value="Zambia"/>
            <xs:enumeration value="Zimbabwe"/>
          </xs:restriction>
        </xs:simpleType>
        <xs:simpleType name="code">
          <xs:restriction base="xs:integer">
            <xs:minExclusive value="9"/>
            <xs:maxExclusive value="21"/>
          </xs:restriction>
        </xs:simpleType>
        <xs:simpleType name="code2">
          <xs:restriction base="xs:integer">
            <xs:minInclusive value="10"/>
            <xs:maxInclusive value="20"/>
          </xs:restriction>
        </xs:simpleType>
      </xs:schema>;
  }
}