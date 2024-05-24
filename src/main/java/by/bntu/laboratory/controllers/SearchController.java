package by.bntu.laboratory.controllers;

import by.bntu.laboratory.models.*;
import by.bntu.laboratory.services.SearchService;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SearchController {

    @Autowired
    private SearchService searchService;

    @PostMapping("/search")
    public String performSearch(@RequestParam("query") String query,
                                @RequestParam(value = "category", required = false) String category,
                                Model model,
                                HttpServletResponse response) {
        List<News> newsResults = new ArrayList<>();
        List<Projects> projectResults = new ArrayList<>();
        List<DataBases> databaseResults = new ArrayList<>();
        List<Events> eventResults = new ArrayList<>();
        List<TimesReviews> timesResults = new ArrayList<>();
        List<OnlineServices> serviceResults = new ArrayList<>();

        if (category == null || category.isEmpty() || category.equals("all")) {
            newsResults = searchService.searchNews(query);
            projectResults = searchService.searchProjects(query);
            databaseResults = searchService.searchDataBases(query);
            eventResults = searchService.searchEvents(query);
            timesResults = searchService.searchTimes(query);
            serviceResults = searchService.searchOnlineServices(query);
        } else {
            switch (category.toLowerCase()) {
                case "news":
                    newsResults = searchService.searchNews(query);
                    break;
                case "projects":
                    projectResults = searchService.searchProjects(query);
                    break;
                case "database":
                    databaseResults = searchService.searchDataBases(query);
                    break;
                case "eventcalendar":
                    eventResults = searchService.searchEvents(query);
                    break;
                case "timesreview":
                    timesResults = searchService.searchTimes(query);
                    break;
                case "onlineservices":
                    serviceResults = searchService.searchOnlineServices(query);
                    break;
                default:
                    // Handle default case
                    break;
            }
        }

        model.addAttribute("newsResults", newsResults);
        model.addAttribute("projectResults", projectResults);
        model.addAttribute("databaseResults", databaseResults);
        model.addAttribute("eventResults", eventResults);
        model.addAttribute("timesResults", timesResults);
        model.addAttribute("serviceResults", serviceResults);
        model.addAttribute("category", category != null ? category : "all");

        response.setHeader("Cache-Control", "public, max-age=3600");

        return "views/search_results";
    }


    @GetMapping("/search")
    public String showSearchPage(Model model) {
        return "search"; // Return the name of your search template
    }
}
