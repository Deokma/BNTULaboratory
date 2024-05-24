package by.bntu.laboratory;

import by.bntu.laboratory.controllers.OnlineServicesController;
import by.bntu.laboratory.exceptions.CustomNotFoundException;
import by.bntu.laboratory.models.OnlineServices;
import by.bntu.laboratory.repo.OnlineServicesRepository;
import by.bntu.laboratory.services.OnlineServicesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OnlineServicesControllerTest {

    @Mock
    private OnlineServicesRepository onlineServicesRepository;

    @Mock
    private OnlineServicesService onlineServicesService;

    @Mock
    private Model model;

    @InjectMocks
    private OnlineServicesController onlineServicesController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testOnlineServicesAdd() {
        String result = onlineServicesController.OnlineServicesAdd(model);
        verify(model, times(1)).addAttribute("oss", new OnlineServices());
        assertEquals("users/writer/add/online-service-add", result);
    }

    @Test
    public void testAddOnlineService() {
        String title = "Service Title";
        String link = "http://example.com";
        String cover = "<svg fill=\"red\"></svg>";
        String sanitizedCover = "<svg ></svg>";
        boolean visible = true;

        String result = onlineServicesController.addOnlineService(title, link, cover, visible, model);

        verify(onlineServicesRepository, times(1)).save(any(OnlineServices.class));
        verify(model, times(1)).addAttribute("message", "Online Service added successfully!");
        assertEquals("config/back", result);
    }

    @Test
    public void testHiddenOnlineServices() {
        List<OnlineServices> onlineServicesList = Collections.singletonList(new OnlineServices());
        when(onlineServicesRepository.findAll()).thenReturn(onlineServicesList);
        when(onlineServicesService.isOnlineServicesListEmptyOrAllHidden(onlineServicesList)).thenReturn(true);

        String result = onlineServicesController.hiddenOnlineServices(model);

        verify(model, times(1)).addAttribute("onlineServicesList", onlineServicesList);
        verify(model, times(1)).addAttribute("emptyOrAllHidden", true);
        assertEquals("users/writer/hidden/online-services-list", result);
    }

    @Test
    public void testOnlineServicesEdit() {
        OnlineServices onlineServices = new OnlineServices();
        when(onlineServicesRepository.findById(1L)).thenReturn(Optional.of(onlineServices));

        String result = onlineServicesController.onlineServicesEdit(1L, model);

        verify(model, times(1)).addAttribute("onlineService", onlineServices);
        assertEquals("users/writer/edit/online-service-edit", result);
    }

    @Test
    public void testOnlineServicesEditNotFound() {
        when(onlineServicesRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CustomNotFoundException.class, () -> {
            onlineServicesController.onlineServicesEdit(1L, model);
        });
    }

    @Test
    public void testOnlineServicesEditPost() {
        String title = "Updated Title";
        String cover = "<svg fill=\"blue\"></svg>";
        String sanitizedCover = "<svg ></svg>";
        boolean visible = true;
        OnlineServices onlineServices = new OnlineServices();
        when(onlineServicesRepository.findOnlineServicesByOsID(1L)).thenReturn(onlineServices);

        String result = onlineServicesController.onlineServicesEditPost(1L, title, cover, visible, model);

        verify(onlineServicesRepository, times(1)).save(onlineServices);
        verify(model, times(1)).addAttribute("message", "DataBase edited successfully!");
        assertEquals("config/back", result);
    }

    @Test
    public void testDeleteOnlineServices() {
        OnlineServices onlineServices = new OnlineServices();
        when(onlineServicesRepository.findById(1L)).thenReturn(Optional.of(onlineServices));

        String result = onlineServicesController.deleteOnlineServices(1L);

        verify(onlineServicesRepository, times(1)).delete(onlineServices);
        assertEquals("config/back", result);
    }
}
