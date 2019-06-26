package cn.im731.mall.service.impl;

import cn.im731.mall.common.Const;
import cn.im731.mall.common.ResponseCode;
import cn.im731.mall.common.ServerResponse;
import cn.im731.mall.dao.CategoryMapper;
import cn.im731.mall.dao.ProductMapper;
import cn.im731.mall.pojo.Category;
import cn.im731.mall.pojo.Product;
import cn.im731.mall.service.ICategoryService;
import cn.im731.mall.service.IProductService;
import cn.im731.mall.util.DateTimeUtil;
import cn.im731.mall.util.PropertiesUtil;
import cn.im731.mall.vo.ProductDetailVo;
import cn.im731.mall.vo.ProductListVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService iCategoryService;

    /*****************************************
     * 后台部分
     ****************************************/


    /**
     * 新增或修改product
     * 如果id不为空则修改，为空则新增
     */
    public ServerResponse saveProduct(Product product) {
        if (product == null) {
            return ServerResponse.createByErrorMessage("产品参数不正确");
        }
        //第一个子图设为主图
        if (StringUtils.isNotBlank(product.getSubImages())) {
            String[] subImgArray = product.getSubImages().split(",");
            if (subImgArray.length > 0) {
                product.setMainImage(subImgArray[0]);
            }
        }
        //更新产品
        if (product.getId() != null) {
            int rowCount = productMapper.updateByPrimaryKeySelective(product);
            if (rowCount > 0) {
                return ServerResponse.createBySuccess("更新产品成功");
            } else {
                return ServerResponse.createByErrorMessage("更新产品失败");
            }
        } else {
            int rowCount = productMapper.insert(product);
            if (rowCount > 0) {
                return ServerResponse.createBySuccessMessage("新增产品成功");
            } else {
                return ServerResponse.createByErrorMessage("新增产品失败");
            }
        }
    }

    public ServerResponse<String> setSaleStatus(Integer productId, Integer status) {
        if (productId == null || status == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "参数不正确");
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if (rowCount > 0) {
            return ServerResponse.createBySuccessMessage("修改产品销售状态信息成功");
        } else {
            return ServerResponse.createByErrorMessage("修改产品销售状态信息失败");
        }
    }

    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId) {
        if (productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "参数不正确");
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMessage("产品不存在");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);

        return ServerResponse.createBySuccess(productDetailVo);
    }

    private ProductDetailVo assembleProductDetailVo(Product product) {
        ProductDetailVo productDetailVo = new ProductDetailVo(product.getId(),
                product.getCategoryId(), product.getName(), product.getSubtitle(),
                product.getMainImage(), product.getSubImages(), product.getDetail(),
                product.getPrice(), product.getStock(), product.getStatus());

        //imageHost
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.im731.cn/"));

        //parentCategoryId
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category == null) {
            //根节点，顶级类目
            productDetailVo.setParentCategoryId(0);
        } else {
            productDetailVo.setParentCategoryId(category.getParentId());
        }

        //createTime
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));

        //updateTime
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));

        return productDetailVo;
    }

    public ServerResponse getProductList(int pageNum, int pageSize) {
        //startPage
        PageHelper.startPage(pageNum, pageSize);

        List<Product> productList = productMapper.selectList();
        List<ProductListVo> productListVoList = new ArrayList<>();

        //To VO
        for (Product p : productList) {
            productListVoList.add(assembleProductListVo(p));
        }

        //PageInfo
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);

        return ServerResponse.createBySuccess(pageResult);

    }

    private ProductListVo assembleProductListVo(Product product) {
        ProductListVo productListVo = new ProductListVo(product.getId(), product.getCategoryId(), product.getName(), product.getSubtitle(), product.getMainImage(), product.getPrice(), product.getStatus(), PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.im731.cn/"));
        return productListVo;
    }

    public ServerResponse<PageInfo> searchProduct(String productName, Integer productId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        if (StringUtils.isNotBlank(productName)) {
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> productList = productMapper.selectByNameAndProductId(productName, productId);
        List<ProductListVo> productListVoList = new ArrayList<>();

        //TO VO
        for (Product p : productList) {
            productListVoList.add(assembleProductListVo(p));
        }

        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);

        return ServerResponse.createBySuccess(pageResult);
    }

    /*****************************************
     * 前台部分
     ****************************************/

    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId) {
        if (productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "参数不正确");
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMessage("产品不存在");
        }

        //判断产品状态是否可用
        if (product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()) {
            //如果不是上架在售状态
            return ServerResponse.createByErrorMessage("产品不可用");
        }


        ProductDetailVo productDetailVo = assembleProductDetailVo(product);

        return ServerResponse.createBySuccess(productDetailVo);
    }

    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId, int pageNum, int pageSize, String orderBy) {
        if (StringUtils.isBlank(keyword) && categoryId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> categoryIdList = new ArrayList<>();

        if (categoryId != null) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category == null && StringUtils.isBlank(keyword)) {
                //没有该分类，也没有关键字，返回空，不报错
                PageHelper.startPage(pageNum, pageSize);
                List<ProductListVo> productListVoList = new ArrayList<>();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            categoryIdList = iCategoryService.selectCategoryAndChildrenById(categoryId).getData();
        }
        if (StringUtils.isNotBlank(keyword)) {
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }

        //开始分页
        PageHelper.startPage(pageNum, pageSize);
        //排序
        if (StringUtils.isNotBlank(orderBy)) {
            if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
//                String[] orderByArray = orderBy.split("_");
//                PageHelper.orderBy(orderByArray[0]+""+orderByArray[1]);
                PageHelper.orderBy(orderBy.replace("_", " "));
            }
        }
        //搜索
        List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword) ? null:keyword, categoryIdList.size()==0?null:categoryIdList);

        //创建VO
        List<ProductListVo> productListVoList = new ArrayList<>();
        for (Product product : productList) {
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }

        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);

        return ServerResponse.createBySuccess(pageInfo);
    }

}
