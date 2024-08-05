/*
 * Copyright (C) 2017 zhouyou(478319399@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ciot.deliverywear.network.interceptor;

import com.ciot.deliverywear.utils.JsonUtil;
import com.ciot.deliverywear.utils.MyLog;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;
import okio.GzipSource;

public class HttpLoggingInterceptor implements Interceptor {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private volatile Level level = Level.BODY;
    private Logger logger;
    private String tag;
    private boolean isLogEnable = false;
    private ThreadLocal<StringBuilder> mThreadLocal = new ThreadLocal<>();

    public enum Level {
        NONE,       //不打印log
        BASIC,      //只打印 请求首行 和 响应首行
        HEADERS,    //打印请求和响应的所有 Header
        BODY        //所有数据全部打印
    }

    public void log(String message) {
        StringBuilder sbMessage = mThreadLocal.get();
        if (null == sbMessage) {
            sbMessage = new StringBuilder();
            mThreadLocal.set(sbMessage);
        }
        // 请求或者响应开始
        if (message.startsWith("--> GET") || message.startsWith("--> PUT") || message.startsWith("--> POST") || message.startsWith("--> DELETE")) {
            sbMessage.setLength(0);
        }
        // 以{}或者[]形式的说明是响应结果的json数据，需要进行格式化
        if ((message.startsWith("{") && message.endsWith("}")) || (message.startsWith("[") && message.endsWith("]"))) {
            message = JsonUtil.formatJson(JsonUtil.decodeUnicode(message));
        }
        sbMessage.append(message.concat("\n"));
        // 响应结束，打印整条日志
        if (message.startsWith("<-- END HTTP") || message.startsWith("<-- HTTP FAILED")) {
            String result = sbMessage.toString();
            if (result.contains("<-- 200 OK")) {
                MyLog.d(this.tag, result);
            } else {
                MyLog.e(this.tag, result);
            }
        }
    }

    public HttpLoggingInterceptor(String tag) {
        this.tag = tag;
        logger = Logger.getLogger(tag);
    }

    public HttpLoggingInterceptor(String tag, boolean isLogEnable) {
        this.tag = tag;
        this.isLogEnable = isLogEnable;
        logger = Logger.getLogger(tag);
    }

    public HttpLoggingInterceptor setLevel(Level level) {
        if (level == null) throw new NullPointerException("level == null. Use Level.NONE instead.");
        this.level = level;
        return this;
    }

    public Level getLevel() {
        return level;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Level level = this.level;

        Request request = chain.request();
        if (level == Level.NONE) {
            return chain.proceed(request);
        }

        boolean logBody = level == Level.BODY;
        boolean logHeaders = logBody || level == Level.HEADERS;

        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;

        Connection connection = chain.connection();
        String requestStartMessage = "--> "
                + request.method()
                + ' ' + request.url()
                + (connection != null ? " " + connection.protocol() : "");
        if (!logHeaders && hasRequestBody) {
            requestStartMessage += " (" + requestBody.contentLength() + "-byte body)";
        }
        log(requestStartMessage);

        if (logHeaders) {
            if (hasRequestBody) {
                // Request body headers are only present when installed as a network interceptor. Force
                // them to be included (when available) so there values are known.
                if (requestBody.contentType() != null) {
                    log("Content-Type: " + requestBody.contentType());
                }
                if (requestBody.contentLength() != -1) {
                    log("Content-Length: " + requestBody.contentLength());
                }
            }

            Headers headers = request.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                String name = headers.name(i);
                // Skip headers from the request body as they are explicitly logged above.
                if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                    log(name + ": " + headers.value(i));
                }
            }

            if (!logBody || !hasRequestBody) {
                log("--> END " + request.method());
            } else if (bodyHasUnknownEncoding(request.headers())) {
                log("--> END " + request.method() + " (encoded body omitted)");
            } else {
                MediaType contentType = requestBody.contentType();
                if (isPlaintext(contentType)) {
                    Buffer buffer = new Buffer();
                    requestBody.writeTo(buffer);
                    Charset charset = UTF8;
                    if (contentType != null) {
                        charset = contentType.charset(UTF8);
                    }
                    log("");
                    log(buffer.readString(charset));
                }
                log("--> END " + request.method() + " (binary " + requestBody.contentLength() + "-byte body omitted)");
            }
        }

        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            log("<-- HTTP FAILED: " + e);
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();
        String bodySize = contentLength != -1 ? contentLength + "-byte" : "unknown-length";
        log("<-- "
                + response.code()
                + (response.message().isEmpty() ? "" : ' ' + response.message())
                + ' ' + response.request().url()
                + " (" + tookMs + "ms" + (!logHeaders ? ", " + bodySize + " body" : "") + ')');

        if (logHeaders) {
            Headers headers = response.headers();
            for (int i = 0, count = headers.size(); i < count; i++) {
                log(headers.name(i) + ": " + headers.value(i));
            }

            if (!logBody || !HttpHeaders.hasBody(response)) {
                log("<-- END HTTP");
            } else if (bodyHasUnknownEncoding(response.headers())) {
                log("<-- END HTTP (encoded body omitted)");
            } else {
                MediaType contentType = responseBody.contentType();
                if (null != contentType) {
                    BufferedSource source = responseBody.source();
                    source.request(Long.MAX_VALUE); // Buffer the entire body.
                    Buffer buffer = source.buffer();

                    Long gzippedLength = null;
                    if ("gzip".equalsIgnoreCase(headers.get("Content-Encoding"))) {
                        gzippedLength = buffer.size();
                        GzipSource gzippedResponseBody = null;
                        try {
                            gzippedResponseBody = new GzipSource(buffer.clone());
                            buffer = new Buffer();
                            buffer.writeAll(gzippedResponseBody);
                        } finally {
                            if (gzippedResponseBody != null) {
                                gzippedResponseBody.close();
                            }
                        }
                    }

                    Charset charset = contentType.charset(UTF8);
                    if (!isPlaintext(contentType)) {
                        log("");
                        log("<-- END HTTP (binary " + buffer.size() + "-byte body omitted)");
                        return response;
                    }
                    if (contentLength != 0) {
                        log("");
                        log(buffer.clone().readString(charset));
                    }
                    if (gzippedLength != null) {
                        log("<-- END HTTP (" + buffer.size() + "-byte, "
                                + gzippedLength + "-gzipped-byte body)");
                    } else {
                        log("<-- END HTTP (" + buffer.size() + "-byte body)");
                    }
                } else {
                    log("<-- END HTTP (" + responseBody.contentLength() + "-byte body)");
                }
            }
        }
        return response;
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    static boolean isPlaintext(MediaType mediaType) {
        if (mediaType == null) return false;
        if (mediaType.type() != null && mediaType.type().equals("text")) {
            return true;
        }
        String subtype = mediaType.subtype();
        if (subtype != null) {
            subtype = subtype.toLowerCase();
            if (subtype.contains("x-www-form-urlencoded") ||
                    subtype.contains("json") ||
                    subtype.contains("xml") ||
                    subtype.contains("html")) //
                return true;
        }
        return false;
    }

    private boolean bodyHasUnknownEncoding(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null
                && !contentEncoding.equalsIgnoreCase("identity")
                && !contentEncoding.equalsIgnoreCase("gzip");
    }

    private void bodyToString(Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            Charset charset = UTF8;
            MediaType contentType = copy.body().contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }
            String result = buffer.readString(charset);
            log("\tbody:" + URLDecoder.decode(replacer(result),UTF8.name()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String replacer(String content) {
        String data = content;
        try {
            data = data.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
            data = data.replaceAll("\\+", "%2B");
            data = URLDecoder.decode(data, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public void e(Throwable t) {
        if (isLogEnable) t.printStackTrace();
    }
}