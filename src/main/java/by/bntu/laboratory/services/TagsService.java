package by.bntu.laboratory.services;

import by.bntu.laboratory.models.Tags;
import by.bntu.laboratory.repo.TagsRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TagsService {

    private final TagsRepository tagsRepository;

    public TagsService(TagsRepository tagsRepository) {
        this.tagsRepository = tagsRepository;
    }

    public List<Tags> parseAndSaveTags(String tagsString) {
        List<Tags> savedTags = new ArrayList<>();
        String[] tagNames = tagsString.split(",");
        for (String tagName : tagNames) {
            // Trim tag name to remove leading/trailing spaces
            tagName = tagName.trim();
            // Check if the tag already exists in the database
            Tags existingTag = tagsRepository.findByName(tagName);
            if (existingTag == null) {
                // If the tag does not exist, create a new one and save it
                Tags newTag = new Tags();
                newTag.setName(tagName);
                savedTags.add(tagsRepository.save(newTag));
            } else {
                // If the tag already exists, add it to the list of saved tags
                savedTags.add(existingTag);
            }
        }
        return savedTags;
    }

    public Optional<Tags> findById(Long tagId) {
        return tagsRepository.findById(tagId);
    }
}
