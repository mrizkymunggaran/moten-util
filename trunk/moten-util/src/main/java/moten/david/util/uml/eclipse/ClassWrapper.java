package moten.david.util.uml.eclipse;

import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.google.inject.Provider;

/**
 * Facade pattern for a java.lang.Class and provides dependency information
 * about that class including constructor and inherited dependencies
 * 
 * @author dave
 * 
 * @param <T>
 */
public class ClassWrapper<T extends Class> {

	/**
	 * the wrapped class
	 */
	private final Class<T> cls;
	private final Type[] genericTypes;

	/**
	 * the filter used to remove irrelevant classes from consideration in
	 * dependencies
	 */
	private final ClassFilter filter;

	public ClassWrapper(Class<T> cls, ClassFilter filter, Type... genericTypes) {
		this.cls = cls;
		if (filter == null)
			this.filter = ClassFilter.ACCEPT_ALL;
		else
			this.filter = filter;
		if (genericTypes == null)
			this.genericTypes = new Type[] {};
		else
			this.genericTypes = genericTypes;
	}

	/**
	 * Get the superclass dependency of the wrapped class. Java does not have
	 * multiple inheritance so there can only be one.
	 * 
	 * @return
	 */
	public Dependency getSuperDependency() {
		if (cls.getSuperclass() != null) {
			if (cls.getGenericSuperclass() instanceof Class)
				return filter(new Dependency(new ClassWrapper(cls
						.getSuperclass(), filter, null)));
			else
				return filter(new Dependency(new ClassWrapper(cls
						.getSuperclass(), filter, cls.getGenericSuperclass())));
		} else
			return null;
	}

	/**
	 * returns null if dependency not accepted by filter
	 * 
	 * @param dependency
	 * @return
	 */
	private Dependency filter(Dependency dependency) {
		if (filter == null)
			throw new RuntimeException("null filter!");
		if (filter.accept(dependency.getClassWrapper().getWrappedClass()))
			return dependency;
		else
			return null;
	}

	/**
	 * gets the dependencies corresponding to the interfaces implemented by the
	 * wrapped class
	 * 
	 * @return
	 */
	public Set<Dependency> getInterfaceDependencies() {
		Set<Dependency> set = new HashSet<Dependency>();
		for (Type type : cls.getGenericInterfaces()) {
			if (type instanceof Class)
				addClass(set, (Class) type);
			else
				addType(set, type);
		}
		return filter(set);
	}

	/**
	 * get the dependencies mentioned in the constructors of the wrapped class
	 * 
	 * @return
	 */
	public Set<Dependency> getConstructorDependencies() {
		Set<Dependency> set = new HashSet<Dependency>();
		for (Constructor<?> c : cls.getConstructors()) {
			for (int i = 0; i < c.getParameterTypes().length; i++) {
				Class c2;
				Type genericType = null;
				if (c.getGenericParameterTypes() != null)
					genericType = c.getGenericParameterTypes()[i];
				if (!c.getParameterTypes()[i].equals(genericType))
					set.add(new Dependency(new ClassWrapper(c
							.getParameterTypes()[i], filter, genericType)));
				else
					set.add(new Dependency(new ClassWrapper(c
							.getParameterTypes()[i], filter, null)));
			}
		}
		if (cls.isArray())
			set.add(new Dependency(new ClassWrapper(cls.getComponentType(),
					filter, cls.getGenericInterfaces())));
		for (Type type : genericTypes)
			addType(set, type);
		set.remove(new Dependency(this));
		return filter(set);
	}

	/**
	 * reduce the dependencies set to only include those accepted by the filter
	 * 
	 * @param set
	 * @return
	 */
	private Set<Dependency> filter(Set<Dependency> set) {
		Set<Dependency> deps = new HashSet<Dependency>(set);
		for (Dependency dep : set) {
			if (!filter.accept(dep.getClassWrapper().getWrappedClass()))
				deps.remove(dep);
		}
		return deps;
	}

	/**
	 * get the wrapped class
	 * 
	 * @return
	 */
	public Class getWrappedClass() {
		return cls;
	}

	/**
	 * add a dependency to a dependency set
	 * 
	 * @param set
	 * @param c
	 */
	private void addClass(Set<Dependency> set, Class c) {
		set.add(new Dependency(new ClassWrapper(c, filter)));
	}

	/**
	 * get all the dependencies (including super, interface and constructor
	 * dependencies)
	 * 
	 * @return
	 */
	public Set<Dependency> getDependencies() {
		Set<Dependency> deps = new HashSet<Dependency>();
		Dependency dep = getSuperDependency();
		if (dep != null)
			deps.add(dep);
		deps.addAll(getInterfaceDependencies());
		deps.addAll(getConstructorDependencies());
		return deps;
	}

	/**
	 * add a type to a set of dependencies
	 * 
	 * @param set
	 * @param type
	 */
	private void addType(Set<Dependency> set, Type type) {
		if (type instanceof Class)
			addClass(set, ((Class) type));
		else if (type instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) type;
			for (Type t : pt.getActualTypeArguments()) {
				addType(set, t);
			}
		}
	}

	private String getString(Type type) {
		StringBuffer s = new StringBuffer();
		if (type instanceof Class) {
			s.append(((Class) type).getName());
		} else if (type instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) type;
			for (Type t : pt.getActualTypeArguments()) {
				if (s.length() > 0)
					s.append(",");
				s.append(getString(t));
			}
		} else if (type instanceof GenericArrayType) {
			s.append("["
					+ getString(((GenericArrayType) type)
							.getGenericComponentType()) + "]");
		}
		return s.toString();
	}

	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();

		if (genericTypes != null && genericTypes.length > 0) {
			boolean first = true;

			for (Type type : genericTypes) {
				if (s.length() > 0)
					s.append(",");
				s.append(getString(type));
			}
			s.insert(0, "<");
			s.append(">");
		}
		s.insert(0, cls.getCanonicalName());
		return s.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cls == null) ? 0 : cls.hashCode());
		result = prime * result + Arrays.hashCode(genericTypes);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClassWrapper other = (ClassWrapper) obj;
		if (cls == null) {
			if (other.cls != null)
				return false;
		} else if (!cls.equals(other.cls))
			return false;
		if (!Arrays.equals(genericTypes, other.genericTypes))
			return false;
		return true;
	}

	private static void printDeps(Dependency dependency, Set<Dependency> used) {
		System.out.println(dependency);
		used.add(dependency);
		Set<Dependency> a = dependency.getClassWrapper().getDependencies();
		for (Dependency dep : a)
			if (!used.contains(dep))
				printDeps(dep, used);
	}

	public void printDeps() {
		printDeps(new Dependency(this), new HashSet<Dependency>());
	}

	// Test class
	private static class Test {
		public Test(Provider<UmlProducer> provider) {

		}
	}

	public static void main(String[] args) {
		ClassWrapper c = new ClassWrapper(Test.class, ClassFilter.STANDARD);
		c.printDeps();
	}
}