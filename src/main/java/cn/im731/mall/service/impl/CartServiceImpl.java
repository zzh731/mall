package cn.im731.mall.service.impl;

import cn.im731.mall.common.Const;
import cn.im731.mall.common.ResponseCode;
import cn.im731.mall.common.ServerResponse;
import cn.im731.mall.dao.CartMapper;
import cn.im731.mall.dao.ProductMapper;
import cn.im731.mall.pojo.Cart;
import cn.im731.mall.pojo.Product;
import cn.im731.mall.service.ICartService;
import cn.im731.mall.util.BigDecimalUtil;
import cn.im731.mall.util.PropertiesUtil;
import cn.im731.mall.vo.CartProductVo;
import cn.im731.mall.vo.CartVo;
import com.google.common.base.Splitter;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CartServiceImpl implements ICartService {
    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    public ServerResponse<CartVo> list(Integer userId) {
        CartVo cartVo = getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    public ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count) {

        if (productId == null || count == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);
        if (cart == null) {
            //此产品不在购物车中，需要向购物车中添加
            Cart cartItem = new Cart();
            cartItem.setQuantity(count);//添加的时候并不检查数量是否小于库存,在获取CartVo的时候才检查
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);

            cartMapper.insert(cartItem);
        } else {
            //此产品已经在购物车中，数量相加
            cart.setQuantity(cart.getQuantity() + count);
            cartMapper.updateByPrimaryKeySelective(cart);   //TODO 这个selective对吗？
        }

        return list(userId);
    }

    public ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count) {
        if (productId == null || count == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);
        if (cart != null) {
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKeySelective(cart);   //TODO 这个selective对吗？
        CartVo cartVo = getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    public ServerResponse<CartVo> deleteProducts(Integer userId, String productIds) {
        if (productIds == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
//        String[] productIdList = productIds.split(",");
//        List<String> productIdArrayList = Arrays.asList(productIdList);

        List<String> productIdArrayList = Splitter.on(",").splitToList(productIds);

//        for (String productId : productIdList) {
//            cartMapper.deleteCartProductByUserIdProductId(userId, productId);
//        }
        cartMapper.deleteCartProductByUserIdProductIdList(userId, productIdArrayList);
        return list(userId);
    }

    /**
     * 选择或反选所有商品
     * 自动判断当前全选、全不选状态
     */
    public ServerResponse<CartVo> selectAllTogle(Integer userId) {

        if (getAllCheckedStatus(userId)) {
            //如果是全选状态
            cartMapper.updateCartProductCheckedStatus(userId, Const.Cart.UN_CHECKED, null);
        } else {
            //如果是全没选状态
            cartMapper.updateCartProductCheckedStatus(userId, Const.Cart.CHECKED, null);
        }

        return list(userId);
    }

    /**
     * 选择所有商品
     */
    public ServerResponse<CartVo> selectAll(Integer userId) {

        cartMapper.updateCartProductCheckedStatus(userId, Const.Cart.CHECKED, null);

        return list(userId);
    }

    /**
     * 不选择所有商品
     */
    public ServerResponse<CartVo> unSelectAll(Integer userId) {

        cartMapper.updateCartProductCheckedStatus(userId, Const.Cart.UN_CHECKED, null);

        return list(userId);
    }


    /**
     * 选中或取消选中某一商品
     * 自动判断当前商品选中状态
     */
    public ServerResponse<CartVo> selectOneTogle(Integer userId, Integer productId) {

        if (getCheckedStatus(userId, productId)) {
            //如果是选中状态
            cartMapper.updateCartProductCheckedStatus(userId, Const.Cart.UN_CHECKED, productId);
        } else {
            //如果是没选中状态
            cartMapper.updateCartProductCheckedStatus(userId, Const.Cart.CHECKED, productId);
        }

        return list(userId);
    }

    /**
     * 选中某一商品
     */
    public ServerResponse<CartVo> selectOne(Integer userId, Integer productId) {

        cartMapper.updateCartProductCheckedStatus(userId, Const.Cart.CHECKED, productId);

        return list(userId);
    }

    /**
     * 取消选中某一商品
     */
    public ServerResponse<CartVo> unSelectOne(Integer userId, Integer productId) {

        cartMapper.updateCartProductCheckedStatus(userId, Const.Cart.UN_CHECKED, productId);

        return list(userId);
    }

    /**
     * 根据用户名查该用户购物车中所有商品数量
     */
    public ServerResponse<Integer> getCartProductCount(Integer userId) {
        if (userId == null) {
            return ServerResponse.createBySuccess(0);
        }
        int count = cartMapper.selectProductCountByUserId(userId);
        return ServerResponse.createBySuccess(count);
    }

    /**
     * 根据用户名，查该用户购物车中所有的商品信息
     * 同时检查购物车中的某商品数量是否大于库存，如果大于则改为库存数量
     */
    private CartVo getCartVoLimit(Integer userId) {
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        List<CartProductVo> cartProductVoList = new ArrayList<>();

        BigDecimal cartTotalPrice = new BigDecimal("0");

        if (CollectionUtils.isNotEmpty(cartList)) {
            for (Cart cartItem : cartList) {
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(userId);
                cartProductVo.setProductId(cartItem.getProductId());

                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if (product != null) {
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());

                    //购买数量，应不大于库存
                    int buyValidCount = 0;
                    //判断库存
                    if (product.getStock() >= cartItem.getQuantity()) {
                        buyValidCount = cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    } else {
                        buyValidCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        //更新购物车中该商品的数量为有效库存
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buyValidCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProductVo.setQuantity(buyValidCount);

                    //计算此商品的总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), buyValidCount));
                    //进行勾选
                    cartProductVo.setProductChecked(cartItem.getChecked());
                }

                //计算整个购物车的总价
                //如果勾选，则计入购物车总价
                if (cartItem.getChecked() == Const.Cart.CHECKED) {
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        return cartVo;
    }

    /**
     * 根据userId查购物车中是否全部选中
     */
    private boolean getAllCheckedStatus(Integer userId) {
        if (userId == null) {
            return false;
        }
        return cartMapper.selectCartProductCheckedStatusByUserId(userId) == Const.Cart.UN_CHECKED;
    }

    /**
     * 根据userId和ProductId查该该用户是否选中了该商品
     */
    private boolean getCheckedStatus(Integer userId, Integer productId) {
        if (userId == null || productId == null) {
            return false;
        }
        return cartMapper.selectCartProductCheckedStatusByUserIdAndProductId(userId, productId) == Const.Cart.CHECKED;
    }


}
