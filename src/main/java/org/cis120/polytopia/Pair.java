package org.cis120.polytopia;

import java.util.Objects;

public class Pair<A, B> {
    public final A first;
    public final B second;

    public Pair(A a, B b) {
        first = a;
        second = b;
    }

    public Pair() {
        first = null;
        second = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }
        return first == ((Pair<?, ?>) o).first && second == ((Pair<?, ?>) o).second;
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return String.valueOf(first) + "," + String.valueOf(second);
    }

    public Pair fromString(Object ss) {
        String s = (String) ss;
        return new Pair(Integer.parseInt(s.split(",")[0]), Integer.parseInt(s.split(",")[1]));
    }
}
