package by.bntu.laboratory.controllers;

import by.bntu.laboratory.exceptions.CustomNotFoundException;
import by.bntu.laboratory.models.Projects;
import by.bntu.laboratory.models.Tags;
import by.bntu.laboratory.repo.ProjectsRepository;
import by.bntu.laboratory.repo.TagsRepository;
import by.bntu.laboratory.services.ProjectsService;
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

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
@Controller
public class ProjectsController {
    private final ProjectsRepository projectsRepository;
    private final ProjectsService projectsService;
    private final TagsRepository tagsRepository;
    private final TagsService tagsService;
    public ProjectsController(ProjectsRepository projectsRepository, ProjectsService projectsService, TagsRepository tagsRepository, TagsService tagsService) {
        this.projectsRepository = projectsRepository;
        this.projectsService = projectsService;
        this.tagsRepository = tagsRepository;
        this.tagsService = tagsService;
    }

    /**
     * Go to Add News Page
     */
    @PreAuthorize("hasAuthority('Writer')")
    @GetMapping("/writer/project/add")
    public String projectAdd(Model model) {
        model.addAttribute("projects", new Projects());
        model.addAttribute("tags", tagsRepository.findAll());

        return "users/writer/add/project-add";
    }

    @GetMapping("/projects")
    public String project(Model model) {
        List<Projects> projects = projectsRepository.findAll();
        model.addAttribute("projectsList", projects);
        model.addAttribute("emptyOrAllHidden", projectsService.isProjectListEmptyOrAllHidden(projects));

        return "views/lists/project-list";
    }

    /**
     * Go to News Page
     */
    @GetMapping("/project/{projectId}")
    public String viewProjectByTitle(@PathVariable("projectId") Long projectId, Model model) {
        // Здесь нужно написать код для получения новости по её названию из базы данных
        Optional<Projects> projectOptional = projectsService.findById(projectId); // Предполагается, что у вас есть сервис для работы с новостями
        if (projectOptional.isEmpty()) {
            // Обработка случая, если новость не найдена
            return "error"; // Вернуть страницу с ошибкой или перенаправить на другую страницу
        }
        Projects projects = projectOptional.get();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isWriter = authentication.getAuthorities().stream()
                .anyMatch(g -> g.getAuthority().equals("Writer"));

        // Проверка, является ли пользователь владельцем новости или имеет права Writer
        if (!projects.getVisible() && !isWriter) {
            return "redirect:/";
        }
        model.addAttribute("project", projects);
        return "views/project-view"; // Предполагается, что у вас есть шаблон для отображения одной новости
    }

    /**
     * Method for adding project
     *
     * @param title   Title of the project
     * @param content Content of the project
     * @param cover   Cover image for the project
     * @param tags    Tags for the project
     */
    @PreAuthorize("hasAuthority('Writer')")
    @PostMapping(value = "/writer/project/add", consumes = MULTIPART_FORM_DATA_VALUE)
    public String addProject(@RequestParam String title,
                             @RequestParam String content,
                             @RequestParam("cover") MultipartFile cover,
                             @RequestParam(value = "tags", required = false) String tags,
                             Model model) {
        try {
            // Validate file type for cover image
            if (!cover.getOriginalFilename().matches(".*\\.(png|jpg|jpeg)$")) {
                throw new Exception("Invalid file type for cover image. Only PNG, JPG, and JPEG files are allowed.");
            }
            // Set the current date
            //Date currentDate = new Date();
            // Parse and save tags if provided
            List<Tags> savedTags = null;
            if (tags != null && !tags.isEmpty()) {
                savedTags = tagsService.parseAndSaveTags(tags);
            }

            // Save the news article to the database
            Projects projects = new Projects();
            projects.setTitle(title);
            projects.setContent(content);
            projects.setTags(savedTags);
            projects.setVisible(true);
            projectsService.saveProjects(projects, cover);
            //  news.setCover(cover.getBytes());


            projectsRepository.save(projects);

            model.addAttribute("message", "Project added successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to add project: " + e.getMessage());
        }

        // Redirect to the home page or any other appropriate page
        return "config/back";
    }

    @PreAuthorize("hasAuthority('Writer')")
    @GetMapping("/writer/project/edit/{project_id}")
    public String projectsEdit(@PathVariable(value = "project_id") long projectId, Model model) {
        Projects projects = projectsRepository.findById(projectId)
                .orElseThrow(() -> new CustomNotFoundException("Project not found with id " + projectId));
        Collection<Tags> projectTags = projects.getTags();
        List<Long> projectsTagIds = projectTags.stream().map(Tags::getTagId).collect(Collectors.toList());
        Collection<Tags> tags = tagsRepository.findAll();
        model.addAttribute("projects", projects);
        model.addAttribute("projectsTags", projectsTagIds);
        model.addAttribute("tags", tags);
        return "users/writer/edit/project-edit";
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
    @PostMapping(value = "/writer/project/edit/{project_id}", consumes = MULTIPART_FORM_DATA_VALUE)
    public String projectsEditPost(@PathVariable(value = "project_id") Long projectId,
                                   @RequestParam String title,
                                   @RequestParam String content,
                                   @RequestParam("cover") MultipartFile cover,
                                   @RequestParam(value = "tags", required = false) String tags,
                                   @RequestParam(value = "visible", defaultValue = "false") boolean visible,
                                   Model model) {
        try {
            // Validate file type for cover image

            Projects existingProject = projectsRepository.findByProjectId(projectId);
            List<Tags> savedTags = null;
            if (tags != null && !tags.isEmpty()) {
                savedTags = tagsService.parseAndSaveTags(tags);
                existingProject.setTags(savedTags);
            }

            // Save the news article to the database
            existingProject.setTitle(title);
            existingProject.setContent(content);

            if (cover != null && !cover.isEmpty()) {
                if (!cover.getOriginalFilename().matches(".*\\.(png|jpg|jpeg)$")) {
                    throw new Exception("Invalid file type for cover image. Only PNG, JPG, and JPEG files are allowed.");
                }
                projectsService.saveProjectImage(existingProject, cover);
            }
            existingProject.setVisible(visible);
            projectsRepository.save(existingProject);

            model.addAttribute("message", "Project edited successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to edit project: " + e.getMessage());
        }

        // Redirect to the home page or any other appropriate page
        return "config/back";
    }

    @PreAuthorize("hasAuthority('Writer')")
    @PostMapping(value = "/writer/project/delete/{project_id}", consumes = MULTIPART_FORM_DATA_VALUE)
    public String deleteProject(@PathVariable("project_id") Long projectId) {
        Projects projects = projectsRepository.findById(projectId).orElseThrow();
        projectsRepository.delete(projects);
        return "config/back";
    }

    @PreAuthorize("hasAuthority('Writer')")
    @GetMapping("writer/projects")
    public String hiddenProjects(Model model) {
        List<Projects> projectsList = projectsRepository.findAll();
        model.addAttribute("projectsList", projectsList);
        model.addAttribute("emptyOrAllHidden", projectsService.isProjectListEmptyOrAllHidden(projectsList));
        return "users/writer/hidden/projects-list";
    }

}
