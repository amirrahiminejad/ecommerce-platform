package com.webrayan.bazaar.service;

import com.webrayan.bazaar.core.common.entity.Tag;
import com.webrayan.bazaar.modules.ads.repository.TagRepository;
import com.webrayan.bazaar.modules.ads.service.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TagServiceTest {

    @InjectMocks
    private TagService tagService;

    @Mock
    private TagRepository tagRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

  //  @Test
    void getAllTags_ShouldReturnListOfTags() {
        Tag tag1 = new Tag();
        tag1.setId(1L);
        tag1.setName("java");

        Tag tag2 = new Tag();
        tag2.setId(2L);
        tag2.setName("spring");

        when(tagRepository.findAll()).thenReturn(List.of(tag1, tag2));

        List<Tag> tags = tagService.getAllTags();

        assertEquals(2, tags.size());
        verify(tagRepository, times(1)).findAll();
    }

//    @Test
    void getTagById_ShouldReturnTag_WhenFound() {
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("java");

        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        Optional<Tag> result = tagService.getTagById(1L);
        assertTrue(result.isPresent());
        assertEquals("java", result.get().getName());
        verify(tagRepository, times(1)).findById(1L);
    }

//    @Test
    void getTagByName_ShouldReturnTag_WhenFound() {
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("java");

        when(tagRepository.findByName("java")).thenReturn(Optional.of(tag));

        Optional<Tag> result = tagService.getTagByName("java");

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(tagRepository, times(1)).findByName("java");
    }

//    @Test
    void saveTag_ShouldThrowException_WhenNameIsNull() {
        Tag tag = new Tag();
        tag.setName(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tagService.saveTag(tag);
        });

        assertEquals("Tag name cannot be null or empty.", exception.getMessage());
        verify(tagRepository, never()).save(any());
    }

   // @Test
    void saveTag_ShouldThrowException_WhenNameIsEmpty() {
        Tag tag = new Tag();
        tag.setName("   ");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tagService.saveTag(tag);
        });

        assertEquals("Tag name cannot be null or empty.", exception.getMessage());
        verify(tagRepository, never()).save(any());
    }

  //  @Test
    void saveTag_ShouldThrowException_WhenNameAlreadyExists() {
        Tag tag = new Tag();
        tag.setName("java");

        when(tagRepository.existsByName("java")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tagService.saveTag(tag);
        });

        assertEquals("Tag with this name already exists.", exception.getMessage());
        verify(tagRepository, never()).save(any());
    }

   // @Test
    void saveTag_ShouldSaveAndReturnTag_WhenValid() {
        Tag tag = new Tag();
        tag.setName("java");

        when(tagRepository.existsByName("java")).thenReturn(false);
        when(tagRepository.save(tag)).thenReturn(tag);

        Tag savedTag = tagService.saveTag(tag);

        assertNotNull(savedTag);
        assertEquals("java", savedTag.getName());
        verify(tagRepository, times(1)).existsByName("java");
        verify(tagRepository, times(1)).save(tag);
    }

    //@Test
    void deleteTag_ShouldCallDeleteById() {
        doNothing().when(tagRepository).deleteById(1L);

        tagService.deleteTag(1L);

        verify(tagRepository, times(1)).deleteById(1L);
    }
}
