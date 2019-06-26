package cn.im731.mall.service;

import cn.im731.mall.common.ServerResponse;
import cn.im731.mall.pojo.Shipping;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

public interface IShippingService {

    public ServerResponse add(Integer userId, Shipping shipping);

    public ServerResponse delete(Integer userId, Integer shippingId);

    public ServerResponse update(Integer userId, Shipping shipping);

    public ServerResponse select(Integer userId, Integer shippingId);

    public ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize);
}
