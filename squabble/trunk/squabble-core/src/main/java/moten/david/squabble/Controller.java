package moten.david.squabble;

import java.util.List;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;

public class Controller {

	private final ImmutableListMultimap<String, String> map = ImmutableListMultimap
			.of();

	private ImmutableListMultimap<String, String> add(String user, String word) {
		List<String> words = createWordFrom(getCurrentWords(), word);
	}

	private List<String> createWordFrom(List<String> currentWords,
			final String word) {
		Set<Character> wordSet = getSet(word);
		Iterable<String> intersects = Iterables.filter(currentWords,
				new Predicate<String>() {
					@Override
					public boolean apply(String w) {
						return Sets.intersection(getSet(w), wordSet).size() > 0;
					}

				});

	}

	private Set<Character> getSet(String word) {
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
