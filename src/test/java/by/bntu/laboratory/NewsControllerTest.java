package by.bntu.laboratory;

import by.bntu.laboratory.controllers.NewsController;
import by.bntu.laboratory.models.News;
import by.bntu.laboratory.repo.NewsRepository;
import by.bntu.laboratory.repo.TagsRepository;
import by.bntu.laboratory.services.NewsService;
import by.bntu.laboratory.services.TagsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class NewsControllerTest {

    @InjectMocks
    private NewsController newsController;

    @Mock
    private NewsService newsService;

    @Mock
    private NewsRepository newsRepository;

    @Mock
    private TagsRepository tagsRepository;

    @Mock
    private TagsService tagsService;

    @Mock
    private Model model;

    @Mock
    private Principal principal;
    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Настройка замоканного SecurityContext
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
    @Test
    void addNews_ShouldRedirectAfterAdd() throws IOException {
        String title = "Test Title";
        String description = "Test Description";
        String content = "Test Content";
        MultipartFile cover = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[0]);
        String tags = "tag1,tag2";
        boolean visible = true;

        String result = newsController.addNews(title, content, description, cover, tags, visible, model);

        verify(newsService, times(1)).saveNews(any(News.class), eq(cover));
        verify(model, times(1)).addAttribute("message", "News added successfully!");
        assertEquals("config/back", result);
    }

    @Test
    void newsEditPost_ShouldRedirectAfterEdit() {
        long newsId = 1L;
        String title = "Test Title";
        String content = "Test Content";
        String description = "Test Description";
        MultipartFile cover = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[0]);
        String tags = "tag1,tag2";
        boolean visible = true;

        News existingNews = new News();
        when(newsRepository.findByNewsId(newsId)).thenReturn(existingNews);

        String result = newsController.newsEditPost(newsId, title, content, description, cover, tags, visible, model);

        verify(newsRepository, times(1)).save(existingNews);
        verify(model, times(1)).addAttribute("message", "News edited successfully!");
        assertEquals("config/back", result);
    }

    @Test
    void deleteNews_ShouldRedirectAfterDelete() {
        Long newsId = 1L;
        News news = new News();
        when(newsRepository.findById(newsId)).thenReturn(Optional.of(news));

        String result = newsController.deleteNews(newsId);

        verify(newsRepository, times(1)).delete(news);
        assertEquals("redirect:config/back", result);
    }

    @Test
    void news_ShouldReturnNewsListPage() {
        List<News> newsList = Collections.singletonList(new News());
        when(newsRepository.findAll()).thenReturn(newsList);

        String result = newsController.news(model);

        verify(model, times(1)).addAttribute("newsList", newsList);
        verify(model, times(1)).addAttribute("emptyOrAllHidden", newsService.isNewsListEmptyOrAllHidden(newsList));
        assertEquals("views/lists/news-list", result);
    }

    @Test
    void viewNews_ShouldReturnNewsView() {
        Long newsId = 1L;
        News news = new News();
        news.setVisible(true);
        when(newsService.findById(newsId)).thenReturn(Optional.of(news));

        String result = newsController.viewNews(newsId, model, principal);

        verify(model, times(1)).addAttribute("news", news);
        assertEquals("views/news-view", result);
    }

    @Test
    void viewNews_ShouldRedirectIfNewsNotVisibleAndNotWriter() {
        Long newsId = 1L;
        News news = new News();
        news.setVisible(false);
        when(newsService.findById(newsId)).thenReturn(Optional.of(news));
        when(principal.getName()).thenReturn("user");

        String result = newsController.viewNews(newsId, model, principal);

        assertEquals("redirect:/", result);
    }

    @Test
    void viewNews_ShouldReturnErrorIfNewsNotFound() {
        Long newsId = 1L;
        when(newsService.findById(newsId)).thenReturn(Optional.empty());

        String result = newsController.viewNews(newsId, model, principal);

        assertEquals("error", result);
    }
}
