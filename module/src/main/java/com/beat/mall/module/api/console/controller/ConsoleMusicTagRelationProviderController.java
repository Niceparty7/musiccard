package com.beat.mall.module.api.console.controller;

import com.beat.mall.common.entity.musictagrelation.MusicTagRelation;
import com.beat.mall.module.auth.ProviderAuthService;
import com.beat.mall.module.musictagrelation.service.MusicTagRelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/musicTagRelation")
public class ConsoleMusicTagRelationProviderController {
    private final MusicTagRelationService service;
    private final ProviderAuthService providerAuthService;

    @RequestMapping(value = "/info", headers = "X-Client-Type=console")
    public MusicTagRelation getDetail(@RequestHeader("X-User-Id") Long userId,
                                      @RequestParam Long musicId,
                                      @RequestParam Long tagId) {
        providerAuthService.requireUser(userId);
        return service.getByMusicIdAndTagId(musicId, tagId);
    }

    @RequestMapping(value = "/list", headers = "X-Client-Type=console")
    public List<MusicTagRelation> getAll(@RequestHeader("X-User-Id") Long userId) {
        providerAuthService.requireUser(userId);
        return service.getAll();
    }

    @PostMapping(value = "/create", headers = "X-Client-Type=console")
    public void create(@RequestHeader("X-User-Id") Long userId,
                       @RequestBody MusicTagRelation entity) {
        providerAuthService.requireUser(userId);
        service.insert(entity);
    }

    @PutMapping(value = "/update", headers = "X-Client-Type=console")
    public void update(@RequestHeader("X-User-Id") Long userId,
                       @RequestBody MusicTagRelation entity) {
        providerAuthService.requireUser(userId);
        service.update(entity);
    }

    @DeleteMapping(value = "/delete", headers = "X-Client-Type=console")
    public void delete(@RequestHeader("X-User-Id") Long userId,
                       @RequestParam Long musicId,
                       @RequestParam Long tagId) {
        providerAuthService.requireUser(userId);
        service.delete(musicId, tagId);
    }
}
