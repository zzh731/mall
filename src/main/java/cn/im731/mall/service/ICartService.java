package cn.im731.mall.service;

import cn.im731.mall.common.ServerResponse;
import cn.im731.mall.vo.CartVo;

public interface ICartService {
    public ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count);

    public ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count);

    public ServerResponse<CartVo> deleteProducts(Integer userId, String productIds);

    public ServerResponse<CartVo> list(Integer userId);

    public ServerResponse<CartVo> selectAllTogle(Integer userId);

    public ServerResponse<CartVo> selectOneTogle(Integer userId, Integer productId);

    public ServerResponse<CartVo> selectAll(Integer userId);

    public ServerResponse<CartVo> unSelectAll(Integer userId);

    public ServerResponse<CartVo> selectOne(Integer userId, Integer productId);

    public ServerResponse<CartVo> unSelectOne(Integer userId, Integer productId);

    public ServerResponse<Integer> getCartProductCount(Integer userId);
}
