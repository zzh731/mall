package cn.im731.mall.dao;

import cn.im731.mall.pojo.Shipping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int deleteByUserIdAndShippingId(@Param("shippingId") Integer shippingId, @Param("userId") Integer userId);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByUserIdAndShippingIdSelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    Shipping selectByUserIdAndShippingId(@Param("shippingId") Integer shippingId, @Param("userId") Integer userId);

    List<Shipping> selectAllByUserId(Integer userId);
}