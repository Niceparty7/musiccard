package com.beat.mall.console.controller.musictagrelation;

import com.beat.mall.common.entity.musictagrelation.MusicTagRelation;
import com.beat.mall.common.entity.user.User;
import com.beat.mall.common.utils.BaseUtil;
import com.beat.mall.console.annotations.VerifiedUser;
import com.beat.mall.console.feign.ConsoleMusicTagRelationFeign;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MusicTagRelationController {
    private final ConsoleMusicTagRelationFeign relationFeign;

    @GetMapping("/musicTagRelation/info")
    public MusicTagRelation getDetail(@VerifiedUser User loginUser,
                                      @RequestParam Long musicId,
                                      @RequestParam Long tagId) {
        return BaseUtil.isEmpty(loginUser) ? null
                : relationFeign.getDetail(loginUser.getId(), musicId, tagId);
    }

    @GetMapping("/musicTagRelation/list")
    public List<MusicTagRelation> getAll(@VerifiedUser User loginUser) {
        return BaseUtil.isEmpty(loginUser) ? Collections.emptyList()
                : relationFeign.getAll(loginUser.getId());
    }

    @PostMapping("/musicTagRelation/create")
    public void create(@VerifiedUser User loginUser,
                       @RequestBody MusicTagRelation entity) {
        if (!BaseUtil.isEmpty(loginUser)) {
            relationFeign.create(loginUser.getId(), entity);
        }
    }

    @PutMapping("/musicTagRelation/update")
    public void update(@VerifiedUser User loginUser,
                       @RequestBody MusicTagRelation entity) {
        if (!BaseUtil.isEmpty(loginUser)) {
            relationFeign.update(loginUser.getId(), entity);
        }
    }

    @DeleteMapping("/musicTagRelation/delete")
    public void delete(@VerifiedUser User loginUser,
                       @RequestParam Long musicId,
                       @RequestParam Long tagId) {
        if (!BaseUtil.isEmpty(loginUser)) {
            relationFeign.delete(loginUser.getId(), musicId, tagId);
        }
    }
}
