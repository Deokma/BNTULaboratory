package by.bntu.laboratory;

import by.bntu.laboratory.controllers.DataBasesController;
import by.bntu.laboratory.models.DataBases;
import by.bntu.laboratory.repo.DataBaseRepository;
import by.bntu.laboratory.services.DataBasesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class DataBasesControllerTest {

    @Mock
    private DataBaseRepository dataBaseRepository;

    @Mock
    private DataBasesService dataBasesService;

    @Mock
    private Model model;

    @InjectMocks
    private DataBasesController dataBasesController;

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
    public void testDataBases() {
        List<DataBases> dataBasesList = Collections.singletonList(new DataBases());
        when(dataBaseRepository.findAll()).thenReturn(dataBasesList);
        when(dataBasesService.isDataBaseListEmptyOrAllHidden(dataBasesList)).thenReturn(true);

        String result = dataBasesController.dataBases(model);

        verify(model, times(1)).addAttribute("dataBasesList", dataBasesList);
        verify(model, times(1)).addAttribute("emptyOrAllHidden", true);
        assertEquals("views/lists/data-bases-list", result);
    }

    @Test
    public void testDataBasesAdd() {
        String result = dataBasesController.dataBasesAdd(model);

        verify(model, times(1)).addAttribute("dbs", new DataBases());
        assertEquals("users/writer/add/data-bases-add", result);
    }

    @Test
    public void testAddDataBase() {
        String result = dataBasesController.addDataBase("title", "description", "content", true, model);

        verify(dataBaseRepository, times(1)).save(any(DataBases.class));
        verify(model, times(1)).addAttribute("message", "DB added successfully!");
        assertEquals("config/back", result);
    }

    @Test
    public void testHiddenDataBases() {
        List<DataBases> dataBasesList = Collections.singletonList(new DataBases());
        when(dataBaseRepository.findAll()).thenReturn(dataBasesList);
        when(dataBasesService.isDataBaseListEmptyOrAllHidden(dataBasesList)).thenReturn(true);

        String result = dataBasesController.hiddenDataBases(model);

        verify(model, times(1)).addAttribute("dataBasesList", dataBasesList);
        verify(model, times(1)).addAttribute("emptyOrAllHidden", true);
        assertEquals("users/writer/hidden/data-bases-list", result);
    }

    @Test
    public void testDataBaseEdit() {
        DataBases dataBase = new DataBases();
        when(dataBaseRepository.findById(1L)).thenReturn(Optional.of(dataBase));

        String result = dataBasesController.dataBaseEdit(1L, model);

        verify(model, times(1)).addAttribute("dataBase", dataBase);
        assertEquals("users/writer/edit/data-base-edit", result);
    }

    @Test
    public void testDataBaseEditPost() {
        DataBases dataBase = new DataBases();
        when(dataBaseRepository.findDataBasesByDbId(1L)).thenReturn(dataBase);

        String result = dataBasesController.dataBaseEditPost(1L, "title", "content", "description", true, model);

        verify(dataBaseRepository, times(1)).save(dataBase);
        verify(model, times(1)).addAttribute("message", "DataBase edited successfully!");
        assertEquals("config/back", result);
    }

    @Test
    public void testDeleteDataBase() {
        DataBases dataBase = new DataBases();
        when(dataBaseRepository.findById(1L)).thenReturn(Optional.of(dataBase));

        String result = dataBasesController.deleteDataBase(1L);

        verify(dataBaseRepository, times(1)).delete(dataBase);
        assertEquals("config/back", result);
    }
}
