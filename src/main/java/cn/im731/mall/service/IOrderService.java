package cn.im731.mall.service;

import cn.im731.mall.common.ServerResponse;

public interface IOrderService {
    public ServerResponse createOrder(Integer userId, Integer shippingId);
    public ServerResponse<String> cancel(Integer userId, Long orderNo);
    public ServerResponse getOrderCartProduct(Integer userId);
}
