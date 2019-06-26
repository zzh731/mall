package cn.im731.mall.service.impl;

import cn.im731.mall.common.ServerResponse;
import cn.im731.mall.dao.ShippingMapper;
import cn.im731.mall.pojo.Shipping;
import cn.im731.mall.service.IShippingService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    public ServerResponse add(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int effectedRows = shippingMapper.insert(shipping);
        if (effectedRows > 0) {
            Map result = new HashMap();
            result.put("shippingId", shipping.getId());
            return ServerResponse.createBySuccess("新增收货地址成功", result);
        }
        return ServerResponse.createByErrorMessage("新增收货地址失败");
    }

    public ServerResponse delete(Integer userId, Integer shippingId) {
        int effectedRows = shippingMapper.deleteByUserIdAndShippingId(shippingId, userId);
        if (effectedRows > 0) {
            return ServerResponse.createBySuccessMessage("删除收货地址成功");
        }
        return ServerResponse.createByErrorMessage("删除收货地址失败");
    }

    public ServerResponse update(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int effectedRows = shippingMapper.updateByUserIdAndShippingIdSelective(shipping);
        if (effectedRows > 0) {
            return ServerResponse.createBySuccessMessage("更新收货地址成功");
        }
        return ServerResponse.createByErrorMessage("更新收货地址失败");
    }

    /**
     * 根据userId和ShippingId查一条收货地址
     */
    public ServerResponse select(Integer userId, Integer shippingId) {
        Shipping shipping = shippingMapper.selectByUserIdAndShippingId(shippingId, userId);
        if (shipping == null) {
            return ServerResponse.createByErrorMessage("无法查询到该地址");
        }
        return ServerResponse.createBySuccess(shipping);
    }

    /**
     * 查userId下所有的收货地址
     * 要分页
     */
    public ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> shippings = shippingMapper.selectAllByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippings);

        return ServerResponse.createBySuccess(pageInfo);
    }

}
