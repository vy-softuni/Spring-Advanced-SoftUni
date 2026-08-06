package app.mendnook.hub.web;

import app.mendnook.hub.gathering.WorkshopGatheringService;
import app.mendnook.hub.mend.MendRequestService;
import app.mendnook.hub.pledge.HelperPledgeService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final MendRequestService mendRequestService;
    private final HelperPledgeService pledgeService;
    private final WorkshopGatheringService gatheringService;

    public DashboardController(MendRequestService mendRequestService,
                               HelperPledgeService pledgeService,
                               WorkshopGatheringService gatheringService) {
        this.mendRequestService = mendRequestService;
        this.pledgeService = pledgeService;
        this.gatheringService = gatheringService;
    }

    @GetMapping("/dashboard")
    String dashboard(Authentication authentication, Model model) {
        model.addAttribute("myMends", mendRequestService.findOwnedBy(authentication.getName()));
        model.addAttribute("myPledges", pledgeService.findForHelper(authentication.getName()));
        model.addAttribute("upcomingGatherings", gatheringService.findPublicGatherings());
        return "dashboard";
    }
}
