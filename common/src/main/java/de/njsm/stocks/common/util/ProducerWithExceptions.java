package de.njsm.stocks.common.util;

@FunctionalInterface
public interface ProducerWithExceptions<T, E extends Exception>  {
    T accept() throws E;
}