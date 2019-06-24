package cn.im731.mall.service;

import cn.im731.mall.common.ServerResponse;
import cn.im731.mall.pojo.Category;

import java.util.List;

public interface ICategoryService {
    public ServerResponse<String> addCategory(String categoryName, Integer parentId);

    public ServerResponse<String> updateCategoryName(Integer categoryId, String categoryName);

    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer parentId);

    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);


}
