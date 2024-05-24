package by.bntu.laboratory.services;

import by.bntu.laboratory.models.OnlineServices;
import by.bntu.laboratory.repo.OnlineServicesRepository;
import by.bntu.laboratory.repo.PublicationImageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class OnlineServicesService {
    @Autowired
    OnlineServicesRepository onlineServicesRepository;
    @Autowired
    PublicationImageRepository publicationImageRepository;

    public List<OnlineServices> getAll() {
        return onlineServicesRepository.findAll();
    }
    public Optional<OnlineServices> findById(Long dbId) {
        return onlineServicesRepository.findById(dbId);
    }
    public void deleteOnlineService(Long id) {
        onlineServicesRepository.deleteById(id);
    }
    public boolean isOnlineServicesListEmptyOrAllHidden(List<OnlineServices> onlineServicesList) {
        return onlineServicesList.isEmpty() || onlineServicesList.stream().noneMatch(OnlineServices::getVisible);
    }
}
