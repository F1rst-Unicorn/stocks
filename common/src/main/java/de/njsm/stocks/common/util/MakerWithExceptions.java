package de.njsm.stocks.common.util;

@FunctionalInterface
public interface MakerWithExceptions<E extends Exception>  {
    void accept() throws E;
}