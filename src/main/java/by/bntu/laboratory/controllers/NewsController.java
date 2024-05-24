package by.bntu.laboratory.controllers;

import by.bntu.laboratory.exceptions.CustomNotFoundException;
import by.bntu.laboratory.models.News;
import by.bntu.laboratory.models.Tags;
import by.bntu.laboratory.repo.NewsRepository;
import by.bntu.laboratory.repo.TagsRepository;
import by.bntu.laboratory.services.NewsService;
import by.bntu.laboratory.services.TagsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Controller
public class NewsController {
    private final NewsRepository newsRepository;
    private final NewsService newsService;
    private final TagsRepository tagsRepository;
    private final TagsService tagsService;
    public NewsController(NewsRepository newsRepository, NewsService newsService, TagsRepository tagsRepository, TagsService tagsService) {
        this.newsRepository = newsRepository;
        this.newsService = newsService;
        this.tagsRepository = tagsRepository;
        this.tagsService = tagsService;
    }

    /**
     * Go to Add News Page
     */
    @PreAuthorize("hasAuthority('Writer')")
    @GetMapping("/writer/news/add")
    public String newsAdd(Model model) {
        model.addAttribute("news", new News());
        model.addAttribute("tags", tagsRepository.findAll());

        return "users/writer/add/news-add";
    }

    @PreAuthorize("hasAuthority('Writer')")
    @GetMapping("/writer/news/edit/{news_id}")
    public String newsEdit(@PathVariable(value = "news_id") long news_id, Model model) {
        News news = newsRepository.findById(news_id)
                .orElseThrow(() -> new CustomNotFoundException("News not found with id " + news_id));
        Collection<Tags> newsTags = news.getTags();
        List<Long> newsTagIds = newsTags.stream().map(Tags::getTagId).collect(Collectors.toList());
        Collection<Tags> tags = tagsRepository.findAll();
        model.addAttribute("news", news);
        model.addAttribute("newsTags", newsTagIds);
        model.addAttribute("tags", tags);
        return "users/writer/edit/news-edit";
    }

    /**
     * Method for adding news
     *
     * @param title   Title of the news
     * @param content Content of the news
     * @param cover   Cover image for the news
     * @param tags    Tags for the news
     */
    @PreAuthorize("hasAuthority('Writer')")
    @PostMapping(value = "/writer/news/edit/{news_id}", consumes = MULTIPART_FORM_DATA_VALUE)
    public String newsEditPost(@PathVariable(value = "news_id") long news_id,
                               @RequestParam String title,
                               @RequestParam String content,
                               @RequestParam String description,
                               @RequestParam("cover") MultipartFile cover,
                               @RequestParam(value = "tags", required = false) String tags,
                               @RequestParam(value = "visible", defaultValue = "false") boolean visible,
                               Model model) {
        try {
            // Validate file type for cover image

            News existingNews = newsRepository.findByNewsId(news_id);
            List<Tags> savedTags = null;
            if (tags != null && !tags.isEmpty()) {
                savedTags = tagsService.parseAndSaveTags(tags);
                existingNews.setTags(savedTags);
            }

            // Save the news article to the database
            existingNews.setTitle(title);
            existingNews.setContent(content);
            existingNews.setDescription(description);

            if (cover != null && !cover.isEmpty()) {
                if (!cover.getOriginalFilename().matches(".*\\.(png|jpg|jpeg)$")) {
                    throw new Exception("Invalid file type for cover image. Only PNG, JPG, and JPEG files are allowed.");
                }
                newsService.saveNewsImage(existingNews, cover);
            }
            existingNews.setVisible(visible);
            newsRepository.save(existingNews);

            model.addAttribute("message", "News edited successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to edit news: " + e.getMessage());
        }

        // Redirect to the home page or any other appropriate page
        return "config/back";
    }

    @PreAuthorize("hasAuthority('Writer')")
    @PostMapping(value = "/writer/news/delete/{news_id}", consumes = MULTIPART_FORM_DATA_VALUE)
    public String deleteNews(@PathVariable("news_id") Long newsId) {
        News news = newsRepository.findById(newsId).orElseThrow();
        newsRepository.delete(news);
        return "redirect:config/back";
    }

    @GetMapping("/news")
    public String news(Model model) {
        List<News> newsList = newsRepository.findAll();
        model.addAttribute("newsList", newsList);
        model.addAttribute("emptyOrAllHidden", newsService.isNewsListEmptyOrAllHidden(newsList));
        return "views/lists/news-list";
    }

    @PreAuthorize("hasAuthority('Writer')")
    @GetMapping("writer/news")
    public String hiddenNews(Model model) {
        List<News> newsList = newsRepository.findAll();
        model.addAttribute("newsList", newsList);
        model.addAttribute("emptyOrAllHidden", newsService.isNewsListEmptyOrAllHidden(newsList));
        return "users/writer/hidden/news-list";
    }

    /**
     * Go to News Page
     */
    @GetMapping("/news/{newsId}")
    public String viewNews(@PathVariable("newsId") Long newsId, Model model, Principal principal) {
        Optional<News> newsOptional = newsService.findById(newsId); // Получение новости по ID
        if (newsOptional.isEmpty()) {
            return "error"; // Вернуть страницу с ошибкой
        }

        News news = newsOptional.get();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isWriter = authentication.getAuthorities().stream()
                .anyMatch(g -> g.getAuthority().equals("Writer"));

        // Проверка, является ли пользователь владельцем новости или имеет права Writer
        if (!news.getVisible() && !isWriter) {
            return "redirect:/";
        }

        model.addAttribute("news", news);
        return "views/news-view"; // Вернуть шаблон для отображения новости
    }


    /**
     * Method for adding news
     *
     * @param title   Title of the news
     * @param content Content of the news
     * @param cover   Cover image for the news
     * @param tags    Tags for the news
     */
    @PreAuthorize("hasAuthority('Writer')")
    @PostMapping(value = "/writer/news/add", consumes = MULTIPART_FORM_DATA_VALUE)
    public String addNews(@RequestParam String title,
                          @RequestParam String content,
                          @RequestParam String description,
                          @RequestParam("cover") MultipartFile cover,
                          @RequestParam(value = "tags", required = false) String tags,
                          @RequestParam(value = "visible", defaultValue = "false") boolean visible,
                          Model model) {
        try {
            // Validate file type for cover image
            if (!cover.getOriginalFilename().matches(".*\\.(png|jpg|jpeg)$")) {
                throw new Exception("Invalid file type for cover image. Only PNG, JPG, and JPEG files are allowed.");
            }
            // Set the current date
            Date currentDate = new Date();
            // Parse and save tags if provided
            List<Tags> savedTags = null;
            if (tags != null && !tags.isEmpty()) {
                savedTags = tagsService.parseAndSaveTags(tags);
            }

            // Save the news article to the database
            News news = new News();
            news.setTitle(title);
            news.setContent(content);
            news.setDescription(description);
            news.setDate(currentDate);
            news.setTags(savedTags);
            newsService.saveNews(news, cover);
            //  news.setCover(cover.getBytes());
            news.setVisible(visible);
            //news.setVisible(true);

            newsRepository.save(news);

            model.addAttribute("message", "News added successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to add news: " + e.getMessage());
        }

        // Redirect to the home page or any other appropriate page
        return "config/back";
    }

}
