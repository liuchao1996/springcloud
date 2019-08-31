package com.lc.order_service.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.lc.order_service.domain.ProductOrder;
import com.lc.order_service.service.FeignProductClient;
import com.lc.order_service.service.ProductOrderService;
import com.lc.order_service.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;

@Service
public class ProductOrderServiceImpl implements ProductOrderService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RestTemplate restTemplate;

    @Resource
    private FeignProductClient feignProductClient;

    @Override
    public ProductOrder save(int userId, int productId) {

        //Map productMap = restTemplate.getForObject("http://product-service/api/v1/product/find?id=" + productId, Map.class);

        String response = feignProductClient.findById(productId);

        JsonNode jsonNode = JsonUtils.str2JsonNode(response);

        ProductOrder productOrder = new ProductOrder();
        productOrder.setCreateTime(new Date());
        productOrder.setUserId(userId);
        productOrder.setTradeNo(UUID.randomUUID().toString());
        productOrder.setProductName(jsonNode.get("name").toString());
        productOrder.setPrice(Integer.parseInt(jsonNode.get("price").toString()));

        return productOrder;
    }
}
