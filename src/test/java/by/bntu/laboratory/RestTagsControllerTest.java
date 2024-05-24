package by.bntu.laboratory;

import by.bntu.laboratory.controllers.RestTagsController;
import by.bntu.laboratory.models.Tags;
import by.bntu.laboratory.repo.TagsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class RestTagsControllerTest {

    @Mock
    private TagsRepository tagsRepository;

    @InjectMocks
    private RestTagsController restTagsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getAllTags() {
        List<Tags> tagsList = Arrays.asList(new Tags(), new Tags());
        when(tagsRepository.findAll()).thenReturn(tagsList);

        List<Tags> result = restTagsController.getAllTags();

        assertEquals(tagsList.size(), result.size());
    }
}

