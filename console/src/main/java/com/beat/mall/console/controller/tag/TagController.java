package com.beat.mall.console.controller.tag;

import com.beat.mall.common.api.console.tag.TagInfoVO;
import com.beat.mall.common.api.console.tag.TagListFeedVO;
import com.beat.mall.common.entity.user.User;
import com.beat.mall.common.response.Response;
import com.beat.mall.common.utils.BaseUtil;
import com.beat.mall.console.annotations.VerifiedUser;
import com.beat.mall.console.feign.TagFeign;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TagController {
    private final TagFeign tagFeign;

    @GetMapping("/tag/info")
    public Response<TagInfoVO> getInfo(@VerifiedUser User loginUser,
                                       @RequestParam Long id) {
        return BaseUtil.isEmpty(loginUser) ? new Response<>(1002) : tagFeign.getInfo(loginUser.getId(), id);
    }

    @GetMapping("/tag/list")
    public Response<TagListFeedVO> getList(@VerifiedUser User loginUser) {
        return BaseUtil.isEmpty(loginUser) ? new Response<>(1002) : tagFeign.getList(loginUser.getId());
    }

    @PostMapping("/tag/create")
    public Response<String> create(@VerifiedUser User loginUser,
                                   @RequestParam String tagName,
                                   @RequestParam(required = false) String tagDesc) {
        return BaseUtil.isEmpty(loginUser) ? new Response<>(1002)
                : tagFeign.create(loginUser.getId(), tagName, tagDesc);
    }

    @PutMapping("/tag/update")
    public Response<String> update(@VerifiedUser User loginUser,
                                   @RequestParam Long id,
                                   @RequestParam String tagName,
                                   @RequestParam(required = false) String tagDesc) {
        return BaseUtil.isEmpty(loginUser) ? new Response<>(1002)
                : tagFeign.update(loginUser.getId(), id, tagName, tagDesc);
    }

    @DeleteMapping("/tag/delete")
    public Response<String> delete(@VerifiedUser User loginUser,
                                   @RequestParam Long id) {
        return BaseUtil.isEmpty(loginUser) ? new Response<>(1002)
                : tagFeign.delete(loginUser.getId(), id);
    }
}
