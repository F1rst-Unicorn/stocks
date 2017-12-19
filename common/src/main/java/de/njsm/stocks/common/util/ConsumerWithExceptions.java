package de.njsm.stocks.common.util;

@FunctionalInterface
public interface ConsumerWithExceptions<T, E extends Exception>  {
    void accept(T t) throws E;
}