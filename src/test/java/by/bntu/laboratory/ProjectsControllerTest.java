package by.bntu.laboratory;

import by.bntu.laboratory.controllers.ProjectsController;
import by.bntu.laboratory.exceptions.CustomNotFoundException;
import by.bntu.laboratory.models.Projects;
import by.bntu.laboratory.models.Tags;
import by.bntu.laboratory.repo.ProjectsRepository;
import by.bntu.laboratory.repo.TagsRepository;
import by.bntu.laboratory.services.ProjectsService;
import by.bntu.laboratory.services.TagsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProjectsControllerTest {

    @Mock
    private ProjectsRepository projectsRepository;

    @Mock
    private ProjectsService projectsService;

    @Mock
    private TagsRepository tagsRepository;

    @Mock
    private TagsService tagsService;

    @Mock
    private Model model;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ProjectsController projectsController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
    }

    @Test
    public void testProjectAdd() {
        when(tagsRepository.findAll()).thenReturn(Collections.emptyList());

        String result = projectsController.projectAdd(model);

        verify(model, times(1)).addAttribute(eq("projects"), any(Projects.class));
        verify(model, times(1)).addAttribute(eq("tags"), eq(Collections.emptyList()));
        assertEquals("users/writer/add/project-add", result);
    }

    @Test
    public void testProject() {
        List<Projects> projectsList = Collections.singletonList(new Projects());
        when(projectsRepository.findAll()).thenReturn(projectsList);
        when(projectsService.isProjectListEmptyOrAllHidden(projectsList)).thenReturn(true);

        String result = projectsController.project(model);

        verify(model, times(1)).addAttribute("projectsList", projectsList);
        verify(model, times(1)).addAttribute("emptyOrAllHidden", true);
        assertEquals("views/lists/project-list", result);
    }

    @Test
    public void testViewProjectByTitle() {
        Projects project = new Projects();
        project.setVisible(true);
        when(projectsService.findById(1L)).thenReturn(Optional.of(project));
        when(authentication.getAuthorities()).thenReturn(Collections.emptyList());

        String result = projectsController.viewProjectByTitle(1L, model);

        verify(model, times(1)).addAttribute("project", project);
        assertEquals("views/project-view", result);
    }

    @Test
    public void testViewProjectByTitleNotFound() {
        when(projectsService.findById(1L)).thenReturn(Optional.empty());

        String result = projectsController.viewProjectByTitle(1L, model);

        assertEquals("error", result);
    }

    @Test
    public void testAddProject() throws Exception {
        String title = "Project Title";
        String content = "Project Content";
        String tags = "tag1, tag2";
        List<Tags> tagsList = Arrays.asList(new Tags(), new Tags());
        when(tagsService.parseAndSaveTags(tags)).thenReturn(tagsList);
        when(multipartFile.getOriginalFilename()).thenReturn("cover.png");

        String result = projectsController.addProject(title, content, multipartFile, tags, model);

        verify(projectsRepository, times(1)).save(any(Projects.class));
        verify(model, times(1)).addAttribute("message", "Project added successfully!");
        assertEquals("config/back", result);
    }

    @Test
    public void testProjectsEdit() {
        Projects project = new Projects();
        List<Tags> tags = Collections.singletonList(new Tags());
        project.setTags(tags);
        when(projectsRepository.findById(1L)).thenReturn(Optional.of(project));
        when(tagsRepository.findAll()).thenReturn(tags);

        String result = projectsController.projectsEdit(1L, model);

        verify(model, times(1)).addAttribute("projects", project);
        verify(model, times(1)).addAttribute("projectsTags", Collections.singletonList(tags.get(0).getTagId()));
        verify(model, times(1)).addAttribute("tags", tags);
        assertEquals("users/writer/edit/project-edit", result);
    }

    @Test
    public void testProjectsEditNotFound() {
        when(projectsRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CustomNotFoundException.class, () -> {
            projectsController.projectsEdit(1L, model);
        });
    }

    @Test
    public void testProjectsEditPost() throws Exception {
        String title = "Updated Title";
        String content = "Updated Content";
        String tags = "tag1, tag2";
        Projects project = new Projects();
        List<Tags> tagsList = Arrays.asList(new Tags(), new Tags());
        when(projectsRepository.findByProjectId(1L)).thenReturn(project);
        when(tagsService.parseAndSaveTags(tags)).thenReturn(tagsList);
        when(multipartFile.getOriginalFilename()).thenReturn("cover.png");

        String result = projectsController.projectsEditPost(1L, title, content, multipartFile, tags, true, model);

        verify(projectsRepository, times(1)).save(project);
        verify(model, times(1)).addAttribute("message", "Project edited successfully!");
        assertEquals("config/back", result);
    }

    @Test
    public void testDeleteProject() {
        Projects project = new Projects();
        when(projectsRepository.findById(1L)).thenReturn(Optional.of(project));

        String result = projectsController.deleteProject(1L);

        verify(projectsRepository, times(1)).delete(project);
        assertEquals("config/back", result);
    }

    @Test
    public void testHiddenProjects() {
        List<Projects> projectsList = Collections.singletonList(new Projects());
        when(projectsRepository.findAll()).thenReturn(projectsList);
        when(projectsService.isProjectListEmptyOrAllHidden(projectsList)).thenReturn(true);

        String result = projectsController.hiddenProjects(model);

        verify(model, times(1)).addAttribute("projectsList", projectsList);
        verify(model, times(1)).addAttribute("emptyOrAllHidden", true);
        assertEquals("users/writer/hidden/projects-list", result);
    }
}
