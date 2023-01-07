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

package de.njsm.stocks.client.network;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

@Singleton
class HostnameInterceptor implements Interceptor {

    private State state;

    @Inject
    HostnameInterceptor() {
        this.state = new NoInterceptionState();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        request = request.newBuilder()
                .url(state.intercept(request.url()))
                .build();
        return chain.proceed(request);
    }

    public void setHost(String serverName) {
        state = state.withHostName(serverName);
    }

    public void setPort(int port) {
        state = state.withPort(port);
    }

    private interface State {
        HttpUrl intercept(HttpUrl url);

        default State withHostName(String hostName) {
            return new UpdatedHostNameState(hostName);
        }

        default State withPort(int port) {
            return new UpdatedPortState(port);
        }
    }

    private static class NoInterceptionState implements State {
        @Override
        public HttpUrl intercept(HttpUrl url) {
            return url;
        }
    }

    private static class UpdatedHostNameState implements State {

        private final String hostName;

        public UpdatedHostNameState(String hostName) {
            this.hostName = hostName;
        }

        @Override
        public HttpUrl intercept(HttpUrl url) {
            return url.newBuilder()
                    .host(hostName)
                    .build();
        }

        @Override
        public State withPort(int port) {
            return new UpdatedConnectionState(hostName, port);
        }
    }

    private static class UpdatedPortState implements State {

        private final int port;

        public UpdatedPortState(int port) {
            this.port = port;
        }

        @Override
        public HttpUrl intercept(HttpUrl url) {
            return url.newBuilder()
                    .port(port)
                    .build();
        }

        @Override
        public State withHostName(String hostName) {
            return new UpdatedConnectionState(hostName, port);
        }
    }

    private static class UpdatedConnectionState implements State {

        private final String hostName;
        private final int port;

        public UpdatedConnectionState(String hostName, int port) {
            this.hostName = hostName;
            this.port = port;
        }

        @Override
        public HttpUrl intercept(HttpUrl url) {
            return url.newBuilder()
                    .host(hostName)
                    .port(port)
                    .build();
        }

        @Override
        public State withHostName(String hostName) {
            return new UpdatedConnectionState(hostName, port);
        }

        @Override
        public State withPort(int port) {
            return new UpdatedConnectionState(hostName, port);
        }
    }
}
