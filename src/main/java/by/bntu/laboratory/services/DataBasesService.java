package by.bntu.laboratory.services;

import by.bntu.laboratory.models.DataBases;
import by.bntu.laboratory.repo.DataBaseRepository;
import by.bntu.laboratory.repo.PublicationImageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DataBasesService {
    @Autowired
    DataBaseRepository dataBaseRepository;
    @Autowired
    PublicationImageRepository publicationImageRepository;


    public List<DataBases> getAll() {
        return dataBaseRepository.findAll();
    }
    public Optional<DataBases> findById(Long dbId) {
        return dataBaseRepository.findById(dbId);
    }
    public void deleteDataBase(Long id) {
        dataBaseRepository.deleteById(id);
    }
    public boolean isDataBaseListEmptyOrAllHidden(List<DataBases> dataBasesList) {
        return dataBasesList.isEmpty() || dataBasesList.stream().noneMatch(DataBases::getVisible);
    }
}
