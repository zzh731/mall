package cn.im731.mall.service.impl;

import cn.im731.mall.common.ServerResponse;
import cn.im731.mall.dao.CategoryMapper;
import cn.im731.mall.pojo.Category;
import cn.im731.mall.service.ICategoryService;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public ServerResponse<String> addCategory(String categoryName, Integer parentId) {
        if (StringUtils.isBlank(categoryName) || parentId == null) {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);

        int rowCount = categoryMapper.insert(category);
        if (rowCount > 0) {
            return ServerResponse.createBySuccess("添加品类成功");
        }

        return ServerResponse.createByErrorMessage("添加品类失败");
    }

    public ServerResponse<String> updateCategoryName(Integer categoryId, String categoryName) {
        if (StringUtils.isBlank(categoryName) || categoryId == null) {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setId(categoryId);

        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if (rowCount > 0) {
            return ServerResponse.createBySuccess("修改品类成功");
        }

        return ServerResponse.createByErrorMessage("修改品类失败");
    }

    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId) {
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if (CollectionUtils.isEmpty(categoryList)) {
            logger.info("未找到当前分类的子分类,categoryId="+categoryId);
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    /**
     *  递归查询子节点
     */
    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId) {
        List<Integer> categoryIdList = new ArrayList<>();
        if (categoryId == null) {
            return ServerResponse.createBySuccess(categoryIdList);
        }

        Set<Category> categorySet = new HashSet<>();
        findChildCategory(categorySet, categoryId);
        for (Category c : categorySet) {
            categoryIdList.add(c.getId());
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }

    /**
     *  递归查找子节点(BFS)
     */
    private Set<Category> findChildCategory(Set<Category> categorySet, Integer categoryId) {
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category != null) {
            categorySet.add(category);
        }

        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if (categoryList == null) {
            return categorySet;
        }

        for (Category c : categoryList) {
            findChildCategory(categorySet, c.getId());
        }
        return categorySet;
    }



}
