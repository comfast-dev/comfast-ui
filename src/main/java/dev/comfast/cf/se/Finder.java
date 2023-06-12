package dev.comfast.cf.se;
import java.util.List;

public interface Finder<T> {
    T find();
    List<T> findAll();
}
