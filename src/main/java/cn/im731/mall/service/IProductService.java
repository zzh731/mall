package cn.im731.mall.service;

import cn.im731.mall.common.ServerResponse;
import cn.im731.mall.pojo.Product;
import cn.im731.mall.vo.ProductDetailVo;
import com.github.pagehelper.PageInfo;

public interface IProductService {
    //后台部分
    public ServerResponse saveProduct(Product product);

    public ServerResponse<String> setSaleStatus(Integer productId, Integer status);

    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);

    public ServerResponse getProductList(int pageNum, int pageSize);

    public ServerResponse<PageInfo> searchProduct(String productName, Integer productId, int pageNum, int pageSize);

    //前台部分
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId);

    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId, int pageNum, int pageSize, String orderBy);
}
