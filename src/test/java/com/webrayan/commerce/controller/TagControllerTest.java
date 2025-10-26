package com.webrayan.commerce.controller;

import com.webrayan.commerce.modules.ads.controller.TagController;
import com.webrayan.commerce.core.common.entity.Tag;
import com.webrayan.commerce.modules.ads.service.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TagControllerTest {

    @InjectMocks
    private TagController tagController;

    @Mock
    private TagService tagService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllTags() {
        Tag tag1 = new Tag();
        Tag tag2 = new Tag();
        List<Tag> tags = Arrays.asList(tag1, tag2);

        when(tagService.getAllTags()).thenReturn(tags);

        List<Tag> result = tagController.getAllTags();

        assertEquals(2, result.size());
        verify(tagService, times(1)).getAllTags();
    }

    @Test
    void testGetTagById_Found() {
        Long id = 1L;
        Tag tag = new Tag();

        when(tagService.getTagById(id)).thenReturn(Optional.of(tag));

        Tag result = tagController.getTagById(id);

        assertNotNull(result);
        verify(tagService, times(1)).getTagById(id);
    }

    @Test
    void testGetTagById_NotFound() {
        Long id = 1L;

        when(tagService.getTagById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            tagController.getTagById(id);
        });

        assertEquals("Tag not found.", exception.getMessage());
        verify(tagService, times(1)).getTagById(id);
    }

    @Test
    void testGetTagByName_Found() {
        String name = "exampleTag";
        Tag tag = new Tag();

        when(tagService.getTagByName(name)).thenReturn(Optional.of(tag));

        Tag result = tagController.getTagByName(name);

        assertNotNull(result);
        verify(tagService, times(1)).getTagByName(name);
    }

    @Test
    void testGetTagByName_NotFound() {
        String name = "exampleTag";

        when(tagService.getTagByName(name)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            tagController.getTagByName(name);
        });

        assertEquals("Tag not found.", exception.getMessage());
        verify(tagService, times(1)).getTagByName(name);
    }

    @Test
    void testSaveTag() {
        Tag tag = new Tag();
        when(tagService.saveTag(any(Tag.class))).thenReturn(tag);

        Tag result = tagController.saveTag(tag);

        assertNotNull(result);
        verify(tagService, times(1)).saveTag(tag);
    }

    @Test
    void testDeleteTag() {
        Long id = 1L;

        tagController.deleteTag(id);

        verify(tagService, times(1)).deleteTag(id);
    }
}
