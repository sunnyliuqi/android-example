package example.com.android_example.utils;

import android.content.Context;
import com.squareup.okhttp.OkHttpClient;
import example.com.android_example.response.ResponseVo;
import example.com.android_example.ssl.MySSLConnectionSocketFactory;
import org.springframework.http.*;
import org.springframework.http.client.OkHttpClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * @Auther: liuqi
 * @Date: 2019/3/13 16:18
 * @Description: 调用接口服务工具类
 */
public class RestTemplateUtil {
    private static RestTemplate restTemplate;
    public final static String HOST = "192.168.31.134:2019/websocket";
    private final static String API = "https://" + HOST;
    public static final String LOGIN = "/loginjwt";
    public static final String MY_FRIENDS = "/myfriends";

    /**
     * 初始化
     *
     */
    static {
        OkHttpClient okHttpClient = null;
        if (MySSLConnectionSocketFactory.getInstance().getTrustrCertificates() != null) {
            okHttpClient = new OkHttpClient();
            okHttpClient.setSslSocketFactory(MySSLConnectionSocketFactory.getInstance().getSslSocketFactory());
            okHttpClient.setHostnameVerifier(((hostname, session) -> true));
        } else {
            okHttpClient = new OkHttpClient();
        }
        OkHttpClientHttpRequestFactory okHttpClientHttpRequestFactory = new OkHttpClientHttpRequestFactory(okHttpClient);
        restTemplate = new RestTemplate(okHttpClientHttpRequestFactory);
    }

    /**
     * 不需要登录的请求
     *
     * @param url
     * @param t
     * @param <T>
     * @return
     */
    public static <T> ResponseVo post(String url, T t) {
        HttpHeaders requestHeaders = new HttpHeaders();

        requestHeaders.setContentType(new MediaType("application", "json"));
        return post(url, t, requestHeaders);
    }

    /**
     * 需要登录的请求
     *
     * @param context
     * @param url
     * @param t
     * @param <T>
     * @return
     */
    public static <T> ResponseVo postWithJwt(Context context, String url, T t) {
        HttpHeaders requestHeaders = new HttpHeaders();
        String jwtToken = JwtUtil.get(context);
        if (StringUtils.hasLength(JwtUtil.get(context))) {
            requestHeaders.set(JwtUtil.JWT, jwtToken);
        }
        requestHeaders.setContentType(new MediaType("application", "json"));
        return post(url, t, requestHeaders);
    }

    /**
     * 不需要登录的请求
     *
     * @param url
     * @param t
     * @param requestHeaders
     * @param <T>
     * @return
     */
    public static <T> ResponseVo post(String url, T t, HttpHeaders requestHeaders) {
        HttpEntity<T> requestEntity = new HttpEntity<>(t, requestHeaders);

        ResponseEntity<String> responseEntity = restTemplate.exchange(API + url, HttpMethod.POST, requestEntity, String.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            String body = responseEntity.getBody();
            return JsonUtil.parse(body, ResponseVo.class);
        } else {
            return ResponseVo.fail("系统繁忙，请稍后再试");
        }

    }

    /**
     * 需要登录的请求
     *
     * @param context
     * @param url
     * @param t
     * @param requestHeaders
     * @param <T>
     * @return
     */
    public static <T> ResponseVo postWithJwt(Context context, String url, T t, HttpHeaders requestHeaders) {

        String jwtToken = JwtUtil.get(context);
        if (StringUtils.hasLength(JwtUtil.get(context))) {
            requestHeaders.set(JwtUtil.JWT, jwtToken);
        }
        HttpEntity<T> requestEntity = new HttpEntity<>(t, requestHeaders);

        ResponseEntity<String> responseEntity = restTemplate.exchange(API + url, HttpMethod.POST, requestEntity, String.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            String body = responseEntity.getBody();
            return JsonUtil.parse(body, ResponseVo.class);
        } else {
            return ResponseVo.fail("系统繁忙，请稍后再试");
        }

    }
}
