package com.beat.mall.console.controller.category;

import com.beat.mall.common.api.console.category.CategoryInfoVO;
import com.beat.mall.common.api.console.category.CategoryListFeedVO;
import com.beat.mall.common.entity.user.User;
import com.beat.mall.common.response.Response;
import com.beat.mall.console.annotations.VerifiedUser;
import com.beat.mall.console.feign.CategoryFeign;
import com.beat.mall.common.utils.BaseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryFeign categoryFeign;

    @GetMapping("/category/list")
    public Response<CategoryListFeedVO> getList(@VerifiedUser User loginUser) {
        return BaseUtil.isEmpty(loginUser) ? new Response<>(1002) : categoryFeign.getList(loginUser.getId());
    }

    @GetMapping("/category/info")
    public Response<CategoryInfoVO> getInfo(@VerifiedUser User loginUser,
                                            @RequestParam Long id) {
        return BaseUtil.isEmpty(loginUser) ? new Response<>(1002) : categoryFeign.getInfo(loginUser.getId(), id);
    }

    @PostMapping("/category/create")
    public Response<String> create(@VerifiedUser User loginUser,
                                   @RequestParam(required = false) String typeName,
                                   @RequestParam(required = false) String typeImage,
                                   @RequestParam String typeDesc,
                                   @RequestParam(required = false) Long parentId) {
        return BaseUtil.isEmpty(loginUser) ? new Response<>(1002)
                : categoryFeign.create(loginUser.getId(), typeName, typeImage, typeDesc, parentId);
    }

    @PutMapping("/category/update")
    public Response<String> update(@VerifiedUser User loginUser,
                                   @RequestParam Long id,
                                   @RequestParam(required = false) String typeName,
                                   @RequestParam(required = false) String typeImage,
                                   @RequestParam(required = false) String typeDesc,
                                   @RequestParam(required = false) Long parentId) {
        return BaseUtil.isEmpty(loginUser) ? new Response<>(1002)
                : categoryFeign.update(loginUser.getId(), id, typeName, typeImage, typeDesc, parentId);
    }

    @DeleteMapping("/category/delete")
    public Response<String> delete(@VerifiedUser User loginUser,
                                   @RequestParam Long id) {
        return BaseUtil.isEmpty(loginUser) ? new Response<>(1002)
                : categoryFeign.delete(loginUser.getId(), id);
    }
}
