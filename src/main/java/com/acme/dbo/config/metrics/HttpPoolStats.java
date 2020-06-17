package com.acme.dbo.config.metrics;

import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class HttpPoolStats {
    private final HttpRoute httpRoute;
    private final PoolingHttpClientConnectionManager poolingHttpClientConnectionManager;

    public HttpPoolStats(HttpRoute httpRoute, PoolingHttpClientConnectionManager poolingHttpClientConnectionManager) {
        this.httpRoute = httpRoute;
        this.poolingHttpClientConnectionManager = poolingHttpClientConnectionManager;
    }

    public int getLeased() {
        return poolingHttpClientConnectionManager.getStats(httpRoute).getLeased();
    }

    public int getPending() {
        return poolingHttpClientConnectionManager.getStats(httpRoute).getPending();
    }

    public int getAvailable() {
        return poolingHttpClientConnectionManager.getStats(httpRoute).getAvailable();
    }

    public int getMax() {
        return poolingHttpClientConnectionManager.getStats(httpRoute).getMax();
    }
}
