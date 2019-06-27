package cn.im731.mall.service;

import cn.im731.mall.common.ServerResponse;
import cn.im731.mall.vo.OrderVo;
import com.github.pagehelper.PageInfo;

public interface IOrderService {
    //用户侧
    public ServerResponse createOrder(Integer userId, Integer shippingId);
    public ServerResponse<String> cancel(Integer userId, Long orderNo);
    public ServerResponse getOrderCartProduct(Integer userId);
    public ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo);
    public ServerResponse<PageInfo> getOrderList(Integer userId, int pageNum, int pageSize);

    //后台侧
    public ServerResponse<PageInfo> manageList(int pageNum, int pageSize);
    public ServerResponse<OrderVo> manageDetail(Long orderNo);
    public ServerResponse<PageInfo> manageSearch(Long orderNo, int pageNum, int pageSize);
    public ServerResponse manageSendGoods(Long orderNo);
}
