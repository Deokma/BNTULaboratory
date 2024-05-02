package by.bntu.laboratory.controllers;

import by.bntu.laboratory.models.Tags;
import by.bntu.laboratory.repo.TagsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RestTagsController {
    @Autowired
    private TagsRepository tagsRepository;
    @GetMapping("/tags")
    public List<Tags> getAllTags() {
        return tagsRepository.findAll();
    }

}
