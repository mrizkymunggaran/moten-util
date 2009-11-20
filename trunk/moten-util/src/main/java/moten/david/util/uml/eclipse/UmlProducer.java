package moten.david.util.uml.eclipse;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import moten.david.util.text.StringUtil;
import moten.david.util.xml.TaggedString;

/**
 * Produces uml2 v3.0 in xmi v2.1 format (xml) from a set of classes. This xmi
 * can be converted by Eclipse 3.5 UML2 Tools editor into a class diagram.
 * 
 * @author dave
 * 
 */
public class UmlProducer {

	private static final String DIRECTION_IN = "in";
	private static final String DIRECTION_RETURN = "return";
	private static final String DIRECTION = "direction";
	private static final String OWNED_PARAMETER = "ownedParameter";
	private static final String OWNED_OPERATION = "ownedOperation";
	private static final String USAGE = "uml:Usage";
	private static final String SUPPLIER = "supplier";
	private static final String CLIENT = "client";
	private static final String GENERAL = "general";
	private static final String GENERALIZATION = "generalization";
	private final UmlProducerOptions options;

	public UmlProducer(ClassFilter filter, UmlProducerOptions options) {
		this.filter = filter;
		this.options = options;
	}

	private static final String TYPE = "type";
	private static final String VALUE = "value";
	private static final String UML_LITERAL_UNLIMITED_NATURAL = "uml:LiteralUnlimitedNatural";
	private static final String ASSOCIATION = "association";
	private static final String OWNED_END = "ownedEnd";
	private static final String NAME = "name";
	private static final String UML_ASSOCIATION = "uml:Association";
	private static final String UML_INTERFACE = "uml:Interface";
	private static final String UML_CLASS = "uml:Class";
	private static final String XMI_ID = "xmi:id";
	private static final String XMI_TYPE = "xmi:type";
	private static final String INTERFACE_REALIZATION = "interfaceRealization";
	private static final String PACKAGED_ELEMENT = "packagedElement";
	static int id = 1;
	private final ClassFilter filter;

	public String getUmlXmi(Class... classes) {
		StringBuffer s = new StringBuffer();
		Set<Dependency> defined = new HashSet<Dependency>();
		for (Class cls : classes)
			s.append(getPackageElements(new Dependency(new ClassWrapper(cls,
					filter)), defined));
		try {
			String template = StringUtil.readString(getClass()
					.getResourceAsStream("uml-template.txt"));

			return template.replace("${content}", s.toString());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Object getPackageElements(Class cls) {
		return getPackageElements(cls, new HashSet<Class>());
	}

	private final Map<Object, String> ids = new HashMap<Object, String>();

	private synchronized String getXmiId(Object key) {
		if (ids.get(key) == null)
			ids.put(key, "id" + id++);
		return ids.get(key);
	}

	private String getKey(Class cls) {
		return cls.getName().replace(".", "_");
	}

	private String getXmiId(Class cls) {
		return getXmiId(getKey(cls));
	}

	private String getXmiId(Dependency dependency) {
		return getXmiId(getKey(dependency.getClassWrapper().getWrappedClass()));
	}

	private String getKey(Dependency dependency) {
		return getKey(dependency.getClassWrapper().getWrappedClass());
	}

	private String getInterfaceXmiId(Class cls, Class inter) {
		return getXmiId("Interface " + getKey(cls) + "-" + getKey((inter)));
	}

	private String getGeneralizationXmiId(Class cls, Class cls2) {
		return getXmiId("Generalization " + getKey(cls) + "-" + getKey(cls2));
	}

	private String getAssociationXmiId(Class cls, Class cls2, String uniqueName) {
		return getXmiId("Association " + getKey(cls) + "-" + getKey(cls2) + " "
				+ uniqueName);
	}

	private String getPackageElements(Dependency dep, Set<Dependency> defined) {
		if (defined.contains(dep))
			return "";
		else
			defined.add(dep);
		System.out.println(defined);
		ClassWrapper classWrapper = dep.getClassWrapper();
		Class cls = classWrapper.getWrappedClass();

		StringBuffer s = new StringBuffer();

		boolean hasSuper = classWrapper.getSuperDependency() != null
				&& !classWrapper.getSuperDependency().getClassWrapper()
						.getWrappedClass().equals(Object.class);
		{
			// class definition
			TaggedString t = new TaggedString();
			t.startTag(PACKAGED_ELEMENT);
			if (cls.isInterface())
				t.addAttribute(XMI_TYPE, UML_INTERFACE);
			else
				t.addAttribute(XMI_TYPE, UML_CLASS);
			t.addAttribute(XMI_ID, getXmiId(cls));
			t
					.addAttribute(NAME, getClassName(cls, options
							.useFullClassNames()));
			// only add clientDependency if is class and has interfaces
			if (classWrapper.getInterfaceDependencies().size() > 0
					&& !cls.isInterface()) {
				StringBuffer interfaceIds = new StringBuffer();
				for (Dependency inter : (Set<Dependency>) classWrapper
						.getInterfaceDependencies()) {
					if (interfaceIds.length() > 0)
						interfaceIds.append(" ");
					interfaceIds.append(getInterfaceXmiId(cls, inter
							.getClassWrapper().getWrappedClass()));
				}
				t.addAttribute("clientDependency", interfaceIds.toString());
			}

			// only include methods if filter accepts the class
			if (filter.accept(classWrapper.getWrappedClass())) {
				List<Dependency> deps = new ArrayList<Dependency>();
				// add method elements
				int methodNo = 0;
				for (Method method : classWrapper.getWrappedClass()
						.getDeclaredMethods()) {
					if (Modifier.isPublic(method.getModifiers())
							&& !Modifier.isStatic(method.getModifiers())) {
						// <ownedOperation xmi:id="_g6Qf8NSkEd6IEsbu0VI_Hg"
						// name="getWrappedClass">
						// <ownedParameter xmi:id="_jHSYMNSkEd6IEsbu0VI_Hg"
						// direction="return"/>
						// </ownedOperation>
						methodNo++;
						t.startTag(OWNED_OPERATION);
						t.addAttribute(XMI_ID, getXmiId(method));
						t.addAttribute(NAME, method.getName());
						t.startTag(OWNED_PARAMETER);
						t.addAttribute(XMI_ID, getXmiId(id++ + ""));
						t.addAttribute(DIRECTION, DIRECTION_RETURN);
						if (method.getReturnType() != null) {
							t.addAttribute(TYPE, getXmiId(method
									.getReturnType()));
							deps.add(new Dependency(new ClassWrapper(method
									.getReturnType(), filter)));
						}
						t.closeTag();
						int argNo = 1;
						for (Class<?> parameter : method.getParameterTypes()) {
							deps.add(new Dependency(new ClassWrapper(parameter,
									filter)));
							t.startTag(OWNED_PARAMETER);
							t.addAttribute(XMI_ID, getXmiId(classWrapper
									.getWrappedClass().getName()
									+ ".method" + methodNo + ".p" + argNo));
							t.addAttribute(NAME, "a" + argNo);
							t.addAttribute(TYPE, getXmiId(parameter));
							t.closeTag();
							argNo++;
						}
						t.closeTag();
					}
				}
				for (Dependency dependency : deps)
					t.append(getPackageElements(dependency, defined));
			}

			// add generalizations and realizations
			for (Dependency inter : (Set<Dependency>) classWrapper
					.getInterfaceDependencies())
				if (cls.isInterface()) {
					t.startTag(GENERALIZATION);
					t.addAttribute(XMI_ID, getGeneralizationXmiId(cls, inter
							.getClassWrapper().getWrappedClass()));
					t.addAttribute(GENERAL, getXmiId(inter));
					t.closeTag();
				} else {
					t.startTag(INTERFACE_REALIZATION);
					t.addAttribute(XMI_ID, getInterfaceXmiId(cls, inter
							.getClassWrapper().getWrappedClass()));
					t.addAttribute(NAME, "Realization" + id++);
					t.addAttribute(SUPPLIER, getXmiId(inter));
					t.addAttribute(CLIENT, getXmiId(cls));
					t.addAttribute("contract", getXmiId(inter));
					t.closeTag();
				}

			// add super class generalization

			if (hasSuper) {
				t.startTag(GENERALIZATION);
				t.addAttribute(XMI_ID, getGeneralizationXmiId(cls, classWrapper
						.getSuperDependency().getClassWrapper()
						.getWrappedClass()));
				t.addAttribute(GENERAL, getXmiId(classWrapper
						.getSuperDependency().getClassWrapper()
						.getWrappedClass()));
				t.closeTag();
			}
			t.closeTag();
			t.close();

			s.append(t.toString());
		}

		// --------------------------------------------------
		// add related class definitions
		// --------------------------------------------------

		// add interfaces
		for (Dependency inter : (Set<Dependency>) classWrapper
				.getInterfaceDependencies())
			s.append(getPackageElements(inter, defined));

		// add super
		if (hasSuper) {
			s.append(getPackageElements(classWrapper.getSuperDependency(),
					defined));
		}

		// add definitions for constructor parameters
		for (Dependency dependency : (Set<Dependency>) classWrapper
				.getConstructorDependencies()) {
			s.append(getPackageElements(dependency, defined));
		}

		{
			// add associations via constructor/fields
			for (Dependency dependency : (Set<Dependency>) classWrapper
					.getConstructorDependencies()) {
				// <packagedElement xmi:type="uml:Usage"
				// xmi:id="_q_k1ANSiEd6IEsbu0VI_Hg" supplier="id151"
				// client="id121"/>
				TaggedString t = new TaggedString();
				t.startTag(PACKAGED_ELEMENT);
				t.addAttribute(XMI_TYPE, USAGE);
				String name = "U" + id++;
				String xmiId = getXmiId(name);
				t.addAttribute(XMI_ID, xmiId);
				// t.addAttribute(NAME, name);
				t
						.addAttribute(CLIENT, getXmiId(classWrapper
								.getWrappedClass()));
				t.addAttribute(SUPPLIER, getXmiId(dependency.getClassWrapper()
						.getWrappedClass()));
				t.closeTag();
				t.close();
				s.append(t.toString());
			}

		}
		return s.toString();
	}

	private String getClassName(Class cls, boolean useFullClassNames) {
		if (cls.isArray())
			return "["
					+ getClassName(cls.getComponentType(), useFullClassNames)
					+ "]";
		else if (useFullClassNames)
			return cls.getName();
		else
			return cls.getSimpleName();
	}

	private String getPackageElements(Type genericParameterType,
			Set<Class> defined) {
		StringBuffer s = new StringBuffer();
		if (genericParameterType instanceof ParameterizedType) {
			ParameterizedType aType = (ParameterizedType) genericParameterType;
			Type[] parameterArgTypes = aType.getActualTypeArguments();
			for (Type parameterArgType : parameterArgTypes) {
				System.out.println(parameterArgType);
				if (parameterArgType instanceof Class) {
					Class parameterArgClass = (Class) parameterArgType;
					s.append(getPackageElements(parameterArgClass, defined));
				}
			}
		}
		return s.toString();
	}

}
