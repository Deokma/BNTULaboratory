package by.bntu.laboratory.controllers;

import by.bntu.laboratory.models.News;
import by.bntu.laboratory.models.Projects;
import by.bntu.laboratory.models.User;
import by.bntu.laboratory.repo.NewsRepository;
import by.bntu.laboratory.repo.ProjectsRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private Authentication authentication;
    @Autowired
    private final NewsRepository newsRepository;
    @Autowired
    private EntityManager entityManager;
    private final ProjectsRepository projectsRepository;


    /**
     * Go to main page
     */
    @Transactional
    @GetMapping( value = "/", produces = MediaType.IMAGE_JPEG_VALUE)
    public String main(Model model) {
        //  Iterable<Books> books = booksRepository.findAll();
        // Iterable<BooksCover> covers = booksCoverRepository.findAll();
        // model.addAttribute("books", books);
        // model.addAttribute("cover", covers);
        Pageable pageable = PageRequest.of(0, 4); // Получаем только первые 4 элемента
        List<News> latestNews = newsRepository.findTop4ByOrderByDateDesc(pageable);
        List<Projects> projectsList = projectsRepository.findAll();
        model.addAttribute("latestNews", latestNews);
        model.addAttribute("projectsList", projectsList);
        //booksCoverRepository.createTempTable();

        return "index";
    }


}