package moten.david.squabble;

import java.util.List;

public class Word {
    @Override
    public String toString() {
        return word;
    }

    private final String word;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((history == null) ? 0 : history.hashCode());
        result = prime * result + ((owner == null) ? 0 : owner.hashCode());
        result = prime * result + ((word == null) ? 0 : word.hashCode());
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
        Word other = (Word) obj;
        if (history == null) {
            if (other.history != null)
                return false;
        } else if (!history.equals(other.history))
            return false;
        if (owner == null) {
            if (other.owner != null)
                return false;
        } else if (!owner.equals(other.owner))
            return false;
        if (word == null) {
            if (other.word != null)
                return false;
        } else if (!word.equals(other.word))
            return false;
        return true;
    }

    private final User owner;
    private final List<Word> history;

    public String getWord() {
        return word;
    }

    public User getOwner() {
        return owner;
    }

    public List<Word> getHistory() {
        return history;
    }

    public Word(User owner, String word, List<Word> history) {
        super();
        this.owner = owner;
        this.word = word;
        this.history = history;
    }
}
