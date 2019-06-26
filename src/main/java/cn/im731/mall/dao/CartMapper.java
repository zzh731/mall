package cn.im731.mall.dao;

import cn.im731.mall.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectCartByUserIdProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);

    List<Cart> selectCartByUserId(Integer userId);

    List<Cart> selectCheckedCartByUserId(Integer userId);

    int selectCartProductCheckedStatusByUserId(Integer userId);

    int selectCartProductCheckedStatusByUserIdAndProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);

    int deleteCartProductByUserIdProductId(@Param("userId") Integer userId, @Param("productId") String productId);

    int deleteCartProductByUserIdProductIdList(@Param("userId") Integer userId, @Param("productIds") List<String> productIds);

    int updateCheckedAllForThisUser(@Param("userId") Integer userId, @Param("checked") Integer checked);

    int updateCartProductCheckedStatusByUserIdAndProductId(@Param("userId") Integer userId, @Param("checked") Integer checked, @Param("productId") Integer productId);

    int updateCartProductCheckedStatus(@Param("userId") Integer userId, @Param("checked") Integer checked, @Param("productId") Integer productId);

    int selectProductCountByUserId(Integer userId);

}