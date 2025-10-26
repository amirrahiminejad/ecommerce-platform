package com.webrayan.commerce.modules.ads.service;

import com.webrayan.commerce.core.common.entity.Tag;
import com.webrayan.commerce.modules.ads.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TagService {

    private final TagRepository repository;

    @Autowired
    private LogService logService;

    public TagService(TagRepository repository) {
        this.repository = repository;
    }

    public List<Tag> getAllTags() {
        List<Tag> list = repository.findAll();
        logService.log("TagService", "getAllTags", "Fetched all tags");
        return list;
    }

    public Optional<Tag> getTagById(Long id) {
        Optional<Tag> tag = repository.findById(id);
        logService.log("TagService", "getTagById", "Fetched tag id: " + id);
        return tag;
    }

    public Optional<Tag> getTagByName(String name) {
        Optional<Tag> tag = repository.findByName(name);
        logService.log("TagService", "getTagByName", "Fetched tag name: " + name);
        return tag;
    }

    public Tag saveTag(Tag tag) {
        if (tag.getName() == null || tag.getName().trim().isEmpty()) {
            logService.log("TagService", "saveTag", "Tag name is null or empty");
            throw new IllegalArgumentException("Tag name cannot be null or empty.");
        }
        if (repository.existsByName(tag.getName())) {
            logService.log("TagService", "saveTag", "Duplicate tag name: " + tag.getName());
            throw new IllegalArgumentException("Tag with this name already exists.");
        }
        Tag saved = repository.save(tag);
        logService.log("TagService", "saveTag", "Saved tag id: " + saved.getId());
        return saved;
    }

    public void deleteTag(Long id) {
        repository.deleteById(id);
        logService.log("TagService", "deleteTag", "Deleted tag id: " + id);
    }
}
