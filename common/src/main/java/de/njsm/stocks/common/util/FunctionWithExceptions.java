package de.njsm.stocks.common.util;

@FunctionalInterface
public interface FunctionWithExceptions<T, R, E extends Exception> {
    R apply(T t) throws E;
}
