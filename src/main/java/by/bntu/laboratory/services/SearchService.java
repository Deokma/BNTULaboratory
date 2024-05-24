package by.bntu.laboratory.services;

import by.bntu.laboratory.models.*;
import by.bntu.laboratory.repo.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class SearchService {
    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private ProjectsRepository projectsRepository;
    @Autowired
    private DataBaseRepository dataBaseRepository;
    @Autowired
    private EventsRepository eventsRepository;
    @Autowired
    private TimesRepository timesRepository;
    @Autowired
    private OnlineServicesRepository onlineServicesRepository;
    @Autowired
    private TagsRepository tagsRepository;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SearchService.class);

    public <T> List<T> searchAndFilter(List<T> items, String query, String... methodNames) {
        logger.info("Results before filtering: " + items.size());
        List<T> filteredItems = filterByLevenshtein(items, query, methodNames);
        logger.info("Results after filtering: " + filteredItems.size());
        return filteredItems;
    }

    public List<News> searchNews(String query) {
        List<News> news = newsRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(query, query);
        return searchAndFilter(news, query, "getTitle", "getContent");
    }

    public List<Projects> searchProjects(String query) {
        List<Projects> projects = projectsRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(query, query);
        return searchAndFilter(projects, query, "getTitle", "getContent");
    }

    public List<DataBases> searchDataBases(String query) {
        List<DataBases> databases = dataBaseRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
        return searchAndFilter(databases, query, "getTitle", "getContent");
    }

    public List<Events> searchEvents(String query) {
        List<Events> events = eventsRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(query, query);
        return searchAndFilter(events, query, "getTitle", "getContent");
    }

    public List<TimesReviews> searchTimes(String query) {
        List<TimesReviews> times = timesRepository.findByTitleContainingIgnoreCase(query);
        return searchAndFilter(times, query, "getTitle");
    }

    public List<OnlineServices> searchOnlineServices(String query) {
        List<OnlineServices> onlineServices = onlineServicesRepository.findByTitleContainingIgnoreCase(query);
        return searchAndFilter(onlineServices, query, "getTitle");
    }


    public List<Object> searchAll(String query) {
        Logger logger = Logger.getLogger("SearchLogger");
        List<Object> results = new ArrayList<>();

        List<News> newsResults = searchNews(query);
        logger.info("News Results: " + newsResults.size());
        results.addAll(newsResults);

        List<Projects> projectResults = searchProjects(query);
        logger.info("Project Results: " + projectResults.size());
        results.addAll(projectResults);

        List<DataBases> dataBaseResults = searchDataBases(query);
        logger.info("DataBase Results: " + dataBaseResults.size());
        results.addAll(dataBaseResults);

        List<Events> eventResults = searchEvents(query);
        logger.info("Event Results: " + eventResults.size());
        results.addAll(eventResults);

        List<TimesReviews> timesResults = searchTimes(query);
        logger.info("Times Results: " + timesResults.size());
        results.addAll(timesResults);

        List<OnlineServices> onlineServiceResults = searchOnlineServices(query);
        logger.info("OnlineService Results: " + onlineServiceResults.size());
        results.addAll(onlineServiceResults);

        logger.info("Total Results: " + results.size());
        return results;
    }

    private <T> List<T> filterByLevenshtein(List<T> items, String query, String... fields) {
        Logger logger = Logger.getLogger("FilterLogger");
        return items.stream()
                .filter(item -> {
                    for (String field : fields) {
                        try {
                            Method method = item.getClass().getMethod(field);
                            String value = (String) method.invoke(item);
                            int distance = calculateLevenshteinDistance(value.toLowerCase(), query.toLowerCase());
                            double threshold = Math.min(3, query.length() * 0.3);  // Левенштейнское расстояние (максимум 3 или 30% длины запроса)

                            if (distance <= threshold || containsMatchingWords(value.toLowerCase(), query.toLowerCase())) {
                                return true;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    private boolean containsMatchingWords(String text, String query) {
        String[] queryWords = query.toLowerCase().split("\\s+");
        for (String word : queryWords) {
            if (text.contains(word)) {
                return true;
            }
        }
        return false;
    }

    private int calculateLevenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = min(
                            dp[i - 1][j - 1] + (s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1
                    );
                }
            }
        }

        return dp[s1.length()][s2.length()];
    }

    private int min(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }
}
