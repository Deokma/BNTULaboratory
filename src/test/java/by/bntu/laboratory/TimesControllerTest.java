package by.bntu.laboratory;

import by.bntu.laboratory.controllers.TimesController;
import by.bntu.laboratory.exceptions.CustomNotFoundException;
import by.bntu.laboratory.models.Tags;
import by.bntu.laboratory.models.TimesReviews;
import by.bntu.laboratory.repo.TagsRepository;
import by.bntu.laboratory.repo.TimesRepository;
import by.bntu.laboratory.services.TagsService;
import by.bntu.laboratory.services.TimesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class TimesControllerTest {

    @Mock
    private TagsRepository tagsRepository;

    @Mock
    private TagsService tagsService;

    @Mock
    private TimesService timesService;

    @Mock
    private TimesRepository timesRepository;

    @Mock
    private Model model;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private MultipartFile cover;

    @InjectMocks
    private TimesController timesController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @Test
    public void testTimesAdd() {
        when(tagsRepository.findAll()).thenReturn(Collections.emptyList());

        String viewName = timesController.timesAdd(model);

        assertEquals("users/writer/add/times-add", viewName);
        verify(model, times(1)).addAttribute(eq("times"), any(TimesReviews.class));
        verify(model, times(1)).addAttribute(eq("tags"), anyList());
    }

    @Test
    public void testTimes() {
        List<TimesReviews> timesReviewsList = Arrays.asList(new TimesReviews(), new TimesReviews());
        when(timesRepository.findAll()).thenReturn(timesReviewsList);
        when(timesService.isTimesListEmptyOrAllHidden(timesReviewsList)).thenReturn(false);

        String viewName = timesController.times(model);

        assertEquals("views/lists/times-list", viewName);
        verify(model, times(1)).addAttribute("timesList", timesReviewsList);
        verify(model, times(1)).addAttribute("emptyOrAllHidden", false);
    }

    @Test
    public void testViewTimes_NotFound() {
        when(timesService.findById(anyLong())).thenReturn(Optional.empty());

        String viewName = timesController.viewTimes(1L, model);

        assertEquals("error", viewName);
    }

    @Test
    public void testViewTimes_FoundButNotVisibleAndNotWriter() {
        TimesReviews timesReviews = new TimesReviews();
        timesReviews.setVisible(false);
        when(timesService.findById(anyLong())).thenReturn(Optional.of(timesReviews));
        when(authentication.getAuthorities()).thenReturn(Collections.emptyList());

        String viewName = timesController.viewTimes(1L, model);

        assertEquals("redirect:/", viewName);
    }

    @Test
    public void testViewTimes_FoundAndVisibleOrWriter() {
        TimesReviews timesReviews = new TimesReviews();
        timesReviews.setVisible(true);
        timesReviews.setLink("/sample-link");
        when(timesService.findById(anyLong())).thenReturn(Optional.of(timesReviews));

        List<GrantedAuthority> authorities = Collections.singletonList((GrantedAuthority) () -> "Writer");
      //  when(authentication.getAuthorities()).thenReturn(authorities);

        String viewName = timesController.viewTimes(1L, model);

        assertEquals("redirect:" + timesReviews.getLink(), viewName);
    }


    @Test
    public void testAddTimes_Success() throws Exception {
        when(cover.getOriginalFilename()).thenReturn("test.jpg");
        when(tagsService.parseAndSaveTags(anyString())).thenReturn(Collections.emptyList());

        String viewName = timesController.addTimes("title", cover, "link", "tags", model);

        assertEquals("config/back", viewName);
        verify(timesService, times(1)).saveTimes(any(TimesReviews.class), eq(cover));
        verify(timesRepository, times(1)).save(any(TimesReviews.class));
        verify(model, times(1)).addAttribute("message", "Times added successfully!");
    }

    @Test
    public void testAddTimes_InvalidCoverFileType() throws Exception {
        when(cover.getOriginalFilename()).thenReturn("test.txt");

        String viewName = timesController.addTimes("title", cover, "link", "tags", model);

        assertEquals("config/back", viewName);
        verify(model, times(1)).addAttribute("error", "Failed to add times: Invalid file type for cover image. Only PNG, JPG, and JPEG files are allowed.");
    }

    @Test
    public void testTimesEdit() {
        TimesReviews timesReviews = new TimesReviews();
        when(timesRepository.findById(anyLong())).thenReturn(Optional.of(timesReviews));

        String viewName = timesController.timesEdit(1L, model);

        assertEquals("users/writer/edit/times-edit", viewName);
        verify(model, times(1)).addAttribute("times", timesReviews);
        verify(model, times(1)).addAttribute("tags", timesReviews.getTags());
    }

    @Test
    public void testTimesEdit_NotFound() {
        when(timesRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(CustomNotFoundException.class, () -> timesController.timesEdit(1L, model));
    }

    @Test
    public void testTimesEditPost_Success() throws Exception {
        TimesReviews timesReviews = new TimesReviews();
        when(timesRepository.findByTimesId(anyLong())).thenReturn(timesReviews);
        when(cover.getOriginalFilename()).thenReturn("test.jpg");

        String viewName = timesController.timesEditPost(1L, "title", cover, "link", "tags", true, model);

        assertEquals("config/back", viewName);
        verify(timesService, times(1)).saveTimesImage(eq(timesReviews), eq(cover));
        verify(timesRepository, times(1)).save(eq(timesReviews));
        verify(model, times(1)).addAttribute("message", "Times edited successfully!");
    }

    @Test
    public void testTimesEditPost_InvalidCoverFileType() throws Exception {
        when(cover.getOriginalFilename()).thenReturn("test.txt");

        String viewName = timesController.timesEditPost(1L, "title", cover, "link", "tags", true, model);

        assertEquals("config/back", viewName);
        verify(model, times(1)).addAttribute("error", "Failed to edit times: Invalid file type for cover image. Only PNG, JPG, and JPEG files are allowed.");
    }

    @Test
    public void testDeleteTimes() {
        TimesReviews timesReviews = new TimesReviews();
        when(timesRepository.findById(anyLong())).thenReturn(Optional.of(timesReviews));

        String viewName = timesController.deleteTimes(1L);

        assertEquals("config/back", viewName);
        verify(timesRepository, times(1)).delete(eq(timesReviews));
    }

    @Test
    public void testHiddenTimes() {
        List<TimesReviews> timesReviewsList = Arrays.asList(new TimesReviews(), new TimesReviews());
        when(timesRepository.findAll()).thenReturn(timesReviewsList);
        when(timesService.isTimesListEmptyOrAllHidden(timesReviewsList)).thenReturn(false);

        String viewName = timesController.hiddenTimes(model);

        assertEquals("users/writer/hidden/times-list", viewName);
        verify(model, times(1)).addAttribute("timesReviewsList", timesReviewsList);
        verify(model, times(1)).addAttribute("emptyOrAllHidden", false);
    }
}
