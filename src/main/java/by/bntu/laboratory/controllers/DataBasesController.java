package by.bntu.laboratory.controllers;

import by.bntu.laboratory.exceptions.CustomNotFoundException;
import by.bntu.laboratory.models.DataBases;
import by.bntu.laboratory.repo.DataBaseRepository;
import by.bntu.laboratory.services.DataBasesService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Controller
public class DataBasesController {
    private final DataBaseRepository dataBaseRepository;
    private final DataBasesService dataBasesService;

    public DataBasesController(DataBaseRepository dataBaseRepository, DataBasesService dataBasesService) {
        this.dataBaseRepository = dataBaseRepository;
        this.dataBasesService = dataBasesService;
    }

    @GetMapping("/data-bases")
    public String dataBases(Model model) {
        List<DataBases> dataBasesList = dataBaseRepository.findAll();
        model.addAttribute("dataBasesList", dataBasesList);
        model.addAttribute("emptyOrAllHidden", dataBasesService.isDataBaseListEmptyOrAllHidden(dataBasesList));
        return "views/lists/data-bases-list";
    }

    @PreAuthorize("hasAuthority('Writer')")
    @GetMapping("/writer/data-base/add")
    public String dataBasesAdd(Model model) {
        model.addAttribute("dbs", new DataBases());
        return "users/writer/add/data-bases-add";
    }

    @PreAuthorize("hasAuthority('Writer')")
    @PostMapping(value = "/writer/data-base/add", consumes = MULTIPART_FORM_DATA_VALUE)
    public String addDataBase(@RequestParam String title,
                              @RequestParam String description,
                              @RequestParam String content,
            /*     @RequestParam(value = "tags", required = false) String tags,*/
                              @RequestParam(value = "visible", defaultValue = "false") boolean visible,
                              Model model) {
        try {
            // Save the news article to the database
            DataBases dataBases = new DataBases();
            dataBases.setTitle(title);
            dataBases.setDescription(description);
            dataBases.setContent(content);
            dataBases.setVisible(visible);

            dataBaseRepository.save(dataBases);

            model.addAttribute("message", "DB added successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to add db: " + e.getMessage());
        }

        // Redirect to the home page or any other appropriate page
        return "config/back";
    }

    @PreAuthorize("hasAuthority('Writer')")
    @GetMapping("writer/data-bases")
    public String hiddenDataBases(Model model) {
        List<DataBases> dataBasesList = dataBaseRepository.findAll();
        model.addAttribute("dataBasesList", dataBasesList);
        model.addAttribute("emptyOrAllHidden", dataBasesService.isDataBaseListEmptyOrAllHidden(dataBasesList));
        return "users/writer/hidden/data-bases-list";
    }


    @PreAuthorize("hasAuthority('Writer')")
    @GetMapping("/writer/data-base/edit/{db_id}")
    public String dataBaseEdit(@PathVariable(value = "db_id") long db_id, Model model) {
        DataBases dataBases = dataBaseRepository.findById(db_id)
                .orElseThrow(() -> new CustomNotFoundException("Data Base not found with id " + db_id));
        model.addAttribute("dataBase", dataBases);
        //model.addAttribute("tags", tags);
        return "users/writer/edit/data-base-edit";
    }

    /**
     * Method for adding news
     *
     * @param title   Title of the news
     * @param content Content of the news
     */
    @PreAuthorize("hasAuthority('Writer')")
    @PostMapping(value = "/writer/data-base/edit/{db_id}", consumes = MULTIPART_FORM_DATA_VALUE)
    public String dataBaseEditPost(@PathVariable(value = "db_id") long db_id,
                                   @RequestParam String title,
                                   @RequestParam String content,
                                   @RequestParam String description,
                                   @RequestParam(value = "visible", defaultValue = "false") boolean visible,
                                   Model model) {
        try {
            // Validate file type for cover image

            DataBases existingDataBase = dataBaseRepository.findDataBasesByDbId(db_id);

            // Save the news article to the database
            existingDataBase.setTitle(title);
            existingDataBase.setContent(content);
            existingDataBase.setDescription(description);

            existingDataBase.setVisible(visible);
            dataBaseRepository.save(existingDataBase);

            model.addAttribute("message", "DataBase edited successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Failed to edit dataBase: " + e.getMessage());
        }

        // Redirect to the home page or any other appropriate page
        return "config/back";
    }

    @PreAuthorize("hasAuthority('Writer')")
    @PostMapping(value = "/writer/data-base/delete/{db_id}", consumes = MULTIPART_FORM_DATA_VALUE)
    public String deleteDataBase(@PathVariable("db_id") Long dbId) {
        DataBases dataBase = dataBaseRepository.findById(dbId).orElseThrow();
        dataBaseRepository.delete(dataBase);
        return "config/back";
    }

    @GetMapping("/data-base/{dbId}")
    public String viewDataBase(@PathVariable("dbId") Long dbId, Model model, Principal principal) {
        Optional<DataBases> dataBasesOptional = dataBasesService.findById(dbId); // Получение новости по ID
        if (dataBasesOptional.isEmpty()) {
            return "users/error"; // Вернуть страницу с ошибкой
        }

        DataBases dataBases = dataBasesOptional.get();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isWriter = authentication.getAuthorities().stream()
                .anyMatch(g -> g.getAuthority().equals("Writer"));

        // Проверка, является ли пользователь владельцем новости или имеет права Writer
        if (!dataBases.getVisible() && !isWriter) {
            return "redirect:/";
        }

        model.addAttribute("dataBase", dataBases);
        return "views/data-base-view"; // Вернуть шаблон для отображения новости
    }

}
