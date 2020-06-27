package com.zzj.miaosha.dao;

import com.zzj.miaosha.domain.MiaoShaOrder;
import com.zzj.miaosha.domain.OrderInfo;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrderDao {

    @Select("select * from miaosha_order where user_id=#{userId} and goods_id=#{goodsId}")
    public MiaoShaOrder getMiaoshaOrderByUserIdGoodsId(@Param("userId") Long userId,
                                                       @Param("goodsId") long goodsId);

    @Insert("insert into order_info(user_id, goods_id, goods_name, goods_count, goods_price, order_channel, status, create_date)"+
            "values(#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel},#{status},#{createDate})")
    //取出id值
    @SelectKey(keyColumn="id", keyProperty="id", resultType=long.class, before=false, statement="select last_insert_id()")
    public long insert(OrderInfo orderInfo);

    @Insert("insert into miaosha_order (user_id, goods_id, order_id)values(#{userId}, #{goodsId}, #{orderId})")
    public void insertMiaoshaOrder(MiaoShaOrder miaoShaOrder);

    @Select("select * from order_info where id =#{orderId}")
    OrderInfo getOrderByOrderId(@Param("orderId")long orderId);
}
