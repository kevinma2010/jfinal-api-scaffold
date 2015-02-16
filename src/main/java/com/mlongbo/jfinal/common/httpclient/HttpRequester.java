package com.mlongbo.jfinal.common.httpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Vector;

/**
 * HTTP请求对象
 * <br></br>
 * 默认使用utf-8编码
 *
 * @author malongbo
 */
public class HttpRequester {
    private String defaultContentEncoding;
    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_DELETE = "DELETE";
    private static final String METHOD_PUT = "PUT";

    public HttpRequester() {
        this.defaultContentEncoding = Charset.defaultCharset().name();
    }

    /**
     * 发送GET请求
     *
     * @param urlString
     *            URL地址
     * @return 响应对象
     * @throws java.io.IOException
     */
    public HttpResponse sendGet(String urlString) throws IOException {
        return this.send(urlString, METHOD_GET, null, null);
    }

    /**
     * 发送GET请求
     *
     * @param urlString
     *            URL地址
     * @param params
     *            参数集合
     * @return 响应对象
     * @throws java.io.IOException
     */
    public HttpResponse sendGet(String urlString, Map<String, Object> params)
            throws IOException {
        return this.send(urlString, METHOD_GET, params, null);
    }

    /**
     * 发送GET请求
     *
     * @param urlString
     *            URL地址
     * @param params
     *            参数集合
     * @param propertys
     *            请求属性
     * @return 响应对象
     * @throws java.io.IOException
     */
    public HttpResponse sendGet(String urlString, Map<String, Object> params,
                                Map<String, String> propertys) throws IOException {
        return this.send(urlString, METHOD_GET, params, propertys);
    }

    /**
     * 发送POST请求
     *
     * @param urlString
     *            URL地址
     * @return 响应对象
     * @throws java.io.IOException
     */
    public HttpResponse sendPost(String urlString) throws IOException {
        return this.send(urlString, METHOD_POST, null, null);
    }

    /**
     * 发送POST请求
     *
     * @param urlString
     *            URL地址
     * @param params
     *            参数集合
     * @return 响应对象
     * @throws java.io.IOException
     */
    public HttpResponse sendPost(String urlString, Map<String, Object> params)
            throws IOException {
        return this.send(urlString, METHOD_POST, params, null);
    }

    /**
     * 发送POST请求
     *
     * @param urlString
     *            URL地址
     * @param params
     *            参数集合
     * @param propertys
     *            请求属性
     * @return 响应对象
     * @throws java.io.IOException
     */
    public HttpResponse sendPost(String urlString, Map<String, Object> params,
                                 Map<String, String> propertys) throws IOException {
        return this.send(urlString, METHOD_POST, params, propertys);
    }

    /**
     * 发送DELETE请求
     *
     * @param urlString
     *            URL地址
     * @return 响应对象
     * @throws java.io.IOException
     */
    public HttpResponse sendDelete(String urlString) throws IOException {
        return this.send(urlString, METHOD_DELETE, null, null);
    }

    /**
     * 发送DELETE请求
     *
     * @param urlString
     *            URL地址
     * @param params
     *            参数集合
     * @return 响应对象
     * @throws java.io.IOException
     */
    public HttpResponse sendDelete(String urlString, Map<String, Object> params)
            throws IOException {
        return this.send(urlString, METHOD_DELETE, params, null);
    }

    /**
     * 发送DELETE请求
     *
     * @param urlString
     *            URL地址
     * @param params
     *            参数集合
     * @param propertys
     *            请求属性
     * @return 响应对象
     * @throws java.io.IOException
     */
    public HttpResponse sendDelete(String urlString, Map<String, Object> params,
                                   Map<String, String> propertys) throws IOException {
        return this.send(urlString, METHOD_DELETE, params, propertys);
    }

    /**
     * 发送PUT请求
     *
     * @param urlString
     *            URL地址
     * @return 响应对象
     * @throws java.io.IOException
     */
    public HttpResponse sendPut(String urlString) throws IOException {
        return this.send(urlString, METHOD_PUT, null, null);
    }

    /**
     * 发送PUT请求
     *
     * @param urlString
     *            URL地址
     * @param params
     *            参数集合
     * @return 响应对象
     * @throws java.io.IOException
     */
    public HttpResponse sendPut(String urlString, Map<String, Object> params)
            throws IOException {
        return this.send(urlString, METHOD_PUT, params, null);
    }

    /**
     * 发送PUT请求
     *
     * @param urlString
     *            URL地址
     * @param params
     *            参数集合
     * @param propertys
     *            请求属性
     * @return 响应对象
     * @throws java.io.IOException
     */
    public HttpResponse sendPut(String urlString, Map<String, Object> params,
                                Map<String, String> propertys) throws IOException {
        return this.send(urlString, METHOD_PUT, params, propertys);
    }

    /**
     * 拼接一个参数多个值情况
     * @param key
     * @param values
     * @return
     */
    private String concatParams(String key, Object[] values) {
        StringBuffer buffer = new StringBuffer();
        int i = 0;
        for (Object value: values) {
            if (i != 0)
                buffer.append("&");
            buffer.append(key).append("=").append(value);
            i++;
        }
        return buffer.toString();
    }

    /**
     * 发送HTTP请求
     *
     * @param urlString
     * @return 响映对象
     * @throws java.io.IOException
     */
    private HttpResponse send(String urlString, String method,
                              Map<String, Object> parameters, Map<String, String> propertys)
            throws IOException {
        HttpURLConnection urlConnection = null;

        /*
        GET、DELETE和PUT函数，使用此参数拼接形式
         */
        if ((method.equalsIgnoreCase(METHOD_GET) || method.equalsIgnoreCase(METHOD_DELETE)
                || method.equalsIgnoreCase(METHOD_PUT)) && parameters != null) {
            StringBuffer param = new StringBuffer();
            int i = 0;
            for (String key : parameters.keySet()) {
                if (i == 0)
                    param.append("?");
                else
                    param.append("&");

                Object value = parameters.get(key);
                if (value == null) continue;

                //如果是数据，说明需要拼接一个参数多个值
                if (value.getClass().isArray())
                    param.append(concatParams(key, (Object[]) value));
                else
                    param.append(key).append("=").append(value);
                i++;
            }
            urlString += param.toString();
        }
        URL url = new URL(urlString);
        urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setRequestMethod(method);
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);
        urlConnection.setUseCaches(false);

        if (propertys != null)
            for (String key : propertys.keySet()) {
                urlConnection.addRequestProperty(key, propertys.get(key));
            }
        /*
        POST情况方式使用此参数拼接
         */
        if (method.equalsIgnoreCase(METHOD_POST) && parameters != null) {
            StringBuffer param = new StringBuffer();
            for (String key : parameters.keySet()) {
                param.append("&");
                Object value = parameters.get(key);
                if (value == null) continue;

                //如果是数据，说明需要拼接一个参数多个值
                if (value.getClass().isArray())
                    param.append(concatParams(key, (Object[]) value));
                else
                    param.append(key).append("=").append(value);
            }
            urlConnection.getOutputStream().write(param.toString().getBytes());
            urlConnection.getOutputStream().flush();
            urlConnection.getOutputStream().close();
        }

        //处理响应
        return this.makeContent(urlString, urlConnection);
    }

    /**
     * 处理响应
     *
     * @param urlConnection
     * @return 响应对象
     * @throws java.io.IOException
     */
    private HttpResponse makeContent(String urlString,
                                     HttpURLConnection urlConnection) throws IOException {
        HttpResponse httpResponser = new HttpResponse();
        try {
            InputStream in = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(in));
            httpResponser.contentCollection = new Vector<String>();
            StringBuffer temp = new StringBuffer();
            String line = bufferedReader.readLine();
            while (line != null) {
                httpResponser.contentCollection.add(line);
                temp.append(line).append("\r\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();

            String ecod = urlConnection.getContentEncoding();
            if (ecod == null)
                ecod = this.defaultContentEncoding;

            httpResponser.urlString = urlString;

            httpResponser.defaultPort = urlConnection.getURL().getDefaultPort();
            httpResponser.file = urlConnection.getURL().getFile();
            httpResponser.host = urlConnection.getURL().getHost();
            httpResponser.path = urlConnection.getURL().getPath();
            httpResponser.port = urlConnection.getURL().getPort();
            httpResponser.protocol = urlConnection.getURL().getProtocol();
            httpResponser.query = urlConnection.getURL().getQuery();
            httpResponser.ref = urlConnection.getURL().getRef();
            httpResponser.userInfo = urlConnection.getURL().getUserInfo();

            httpResponser.content = new String(temp.toString().getBytes(), ecod);
            httpResponser.contentEncoding = ecod;
            httpResponser.code = urlConnection.getResponseCode();
            httpResponser.message = urlConnection.getResponseMessage();
            httpResponser.contentType = urlConnection.getContentType();
            httpResponser.method = urlConnection.getRequestMethod();
            httpResponser.connectTimeout = urlConnection.getConnectTimeout();
            httpResponser.readTimeout = urlConnection.getReadTimeout();

            return httpResponser;
        } catch (IOException e) {
            throw e;
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
    }

    /**
     * 默认的响应字符集
     */
    public String getDefaultContentEncoding() {
        return this.defaultContentEncoding;
    }

    /**
     * 设置默认的响应字符集
     */
    public void setDefaultContentEncoding(String defaultContentEncoding) {
        this.defaultContentEncoding = defaultContentEncoding;
    }
}