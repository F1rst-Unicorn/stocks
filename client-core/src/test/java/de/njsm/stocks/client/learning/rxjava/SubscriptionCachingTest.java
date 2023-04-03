/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package de.njsm.stocks.client.learning.rxjava;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Supplier;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionCachingTest {

    @Mock
    Supplier<Integer> source;

    @BeforeEach
    void setUp() throws Throwable {
        when(source.get()).thenReturn(1);
    }

    @Test
    void subscribingTwiceCallsSourceTwice() throws Throwable {
        Observable<Integer> observable = Observable.fromSupplier(source);

        observable.subscribe();
        observable.subscribe();

        verify(source, times(2)).get();
    }

    @Test
    void cachingReducesCallsToOne() throws Throwable {
        Observable<Integer> observable = Observable.fromSupplier(source).cache();

        observable.subscribe();
        observable.subscribe();

        verify(source, times(1)).get();
    }

    @Test
    void behaviorSubjectReducesCallsToOne() throws Throwable {
        Observable<Integer> observable = Observable.fromSupplier(source).cache();
        BehaviorSubject<Object> subject = BehaviorSubject.create();
        observable.subscribe(subject);

        subject.subscribe();
        subject.subscribe();

        verify(source, times(1)).get();
    }

    @Test
    void subscribingABehaviorSubjectBeforeSubscribingToItYieldsNoValue() {
        var mock = Mockito.mock(Consumer.class);
        Observable<Integer> observable = Observable.fromSupplier(source);
        BehaviorSubject<Integer> subject = BehaviorSubject.create();

        observable.subscribe(subject);
        subject.subscribe(mock);

        verifyNoInteractions(mock);
    }

    @Test
    void subscribingABehaviorSubjectAfterSubscribingToItYieldsNoValue() throws Throwable {
        var mock = Mockito.mock(Consumer.class);
        Observable<Integer> observable = Observable.fromSupplier(source);
        BehaviorSubject<Integer> subject = BehaviorSubject.create();

        subject.subscribe(mock);
        observable.subscribe(subject);

        verify(mock).accept(any());
    }
}
