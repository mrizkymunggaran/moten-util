package moten.david.squabble;

import java.util.List;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;

public class Controller {

	private final ImmutableListMultimap<String, String> map = ImmutableListMultimap
			.of();

	private ImmutableListMultimap<String, String> add(String user, String word) {
		Iterable<String> words = createWordFrom(getCurrentWords(), word);
	}

	private Iterable<String> createWordFrom(List<String> currentWords,
			final String word) {
		final Set<Character> wordSet = getSet(word);
		Iterable<String> intersects = Iterables.filter(currentWords,
				new Predicate<String>() {
					@Override
					public boolean apply(String w) {
						return w.length() <= word.length()
								&& Sets.intersection(getSet(w), wordSet).size() > 0;
					}
				});

		return intersects;
	}

	private Iterable<String> createWordFrom(Iterable<String> used,
			Iterable<String> unused, final String word) {
		String usedJoined = getSingledWord(used);
		if (usedJoined.length() > word.length())
			return null;
		else {
			if (getSet(usedJoined).equals(usedJoined))
		}
	}

	private String getSingledWord(Iterable<String> strings) {
		StringBuffer s = new StringBuffer();
		for (String str : strings)
			s.append(str);
		return s.toString();
	}

	private String sort(String word) {
		com.google.common.collect.ImmutableSortedSet.Builder<Character> builder = ImmutableSortedSet
				.naturalOrder();
		for (Character ch : word.toCharArray())
			builder.add(ch);
		StringBuffer s = new StringBuffer();
		for (Character ch : builder.build()) {
			s.append(ch);
		}
		return s.toString();
	}

	private Set<Character> getSet(String word) {
		com.google.common.collect.ImmutableSet.Builder<Character> builder = ImmutableSet
				.builder();
		for (Character ch : word.toCharArray())
			builder.add(ch);
		return builder.build();
	}

	private Set<Character> getList(String word) {
		com.google.common.collect.ImmutableSet.Builder<Character> builder = ImmutableSet
				.builder();
		for (Character ch : word.toCharArray())
			builder.add(ch);
		return builder.build();
	}

	private List<String> getCurrentWords() {
		Builder<String> builder = ImmutableList.builder();
		for (String user : map.keys())
			builder.addAll(map.get(user));
		return builder.build();
	}

	public synchronized void wordSubmitted(String user, String word) {

	}

	private void fireMessage(String message) {

	}
}
