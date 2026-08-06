package app.mendnook.hub.web;

import app.mendnook.hub.gathering.WorkshopGatheringService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final WorkshopGatheringService gatheringService;

    public HomeController(WorkshopGatheringService gatheringService) {
        this.gatheringService = gatheringService;
    }

    @GetMapping("/")
    String home(Model model) {
        model.addAttribute("upcomingGatherings", gatheringService.findPublicGatherings().stream().limit(3).toList());
        return "home";
    }

    @GetMapping("/discover")
    String discover(Model model) {
        model.addAttribute("gatherings", gatheringService.findPublicGatherings());
        return "discover";
    }

    @GetMapping("/about")
    String about() {
        return "about";
    }

    @GetMapping("/access-denied")
    String accessDenied() {
        return "error/access-denied";
    }
}
