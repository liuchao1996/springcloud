package com.lc.zuul.filter;

import com.google.common.util.concurrent.RateLimiter;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

/**
 * 订单限流
 *
 * @author lc
 * @version 1.0
 * @date 2019-08-31 13:48
 * @see com.lc.zuul.filter
 */
@Component
public class OrderRateLimiterFilter extends ZuulFilter {

    //每秒产生1000个令牌
    private static final RateLimiter RATE_LIMITER = RateLimiter.create(1);

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return -4;
    }


    @Override
    public boolean shouldFilter() {


        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();

        //只对订单接口限流
        if ("/apigateway/order/api/v1/order/save".equalsIgnoreCase(request.getRequestURI())) {
            return true;
        }

        return false;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        if (!RATE_LIMITER.tryAcquire()) {
            requestContext.setSendZuulResponse(false);
            requestContext.setResponseStatusCode(HttpStatus.TOO_MANY_REQUESTS.value());
            requestContext.setResponseBody("{\"code\":" + HttpStatus.TOO_MANY_REQUESTS.value() + ", \"msg\":\"" + HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase() + "\"}");
        }
        return null;
    }
}
