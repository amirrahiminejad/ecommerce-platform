package com.webrayan.store.modules.ads.controller;

import com.webrayan.store.core.common.entity.Tag;
import com.webrayan.store.modules.ads.service.TagService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService service;

    public TagController(TagService service) {
        this.service = service;
    }

    @GetMapping
    public List<Tag> getAllTags() {
        return service.getAllTags();
    }

    @GetMapping("/{id}")
    public Tag getTagById(@PathVariable Long id) {
        return service.getTagById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found."));
    }

    @GetMapping("/name/{name}")
    public Tag getTagByName(@PathVariable String name) {
        return service.getTagByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found."));
    }

    @PostMapping
    public Tag saveTag(@RequestBody Tag tag) {
        return service.saveTag(tag);
    }

    @DeleteMapping("/{id}")
    public void deleteTag(@PathVariable Long id) {
        service.deleteTag(id);
    }
}
