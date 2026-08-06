package app.mendnook.hub.web;

import app.mendnook.hub.gathering.WorkshopGatheringService;
import app.mendnook.hub.mend.MendRequestService;
import app.mendnook.hub.pledge.CompletionForm;
import app.mendnook.hub.pledge.HelperPledge;
import app.mendnook.hub.pledge.HelperPledgeService;
import app.mendnook.hub.pledge.PledgeForm;
import app.mendnook.hub.shared.DomainRuleException;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/pledges")
public class PledgeController {

    private final HelperPledgeService pledgeService;
    private final MendRequestService mendRequestService;
    private final WorkshopGatheringService gatheringService;

    public PledgeController(HelperPledgeService pledgeService,
                            MendRequestService mendRequestService,
                            WorkshopGatheringService gatheringService) {
        this.pledgeService = pledgeService;
        this.mendRequestService = mendRequestService;
        this.gatheringService = gatheringService;
    }

    @GetMapping
    String myPledges(Authentication authentication, Model model) {
        model.addAttribute("pledges", pledgeService.findForHelper(authentication.getName()));
        return "pledge/list";
    }

    @GetMapping("/opportunities")
    String opportunities(Model model) {
        model.addAttribute("mends", mendRequestService.findAvailableForPledging());
        return "pledge/opportunities";
    }

    @GetMapping("/new/{mendRequestId}")
    String pledgeForm(@PathVariable UUID mendRequestId, Model model) {
        model.addAttribute("mend", mendRequestService.findById(mendRequestId));
        model.addAttribute("gatherings", gatheringService.findOpenGatherings());
        model.addAttribute("pledgeForm", new PledgeForm());
        return "pledge/form";
    }

    @PostMapping("/new/{mendRequestId}")
    String create(@PathVariable UUID mendRequestId,
                  @Valid @ModelAttribute PledgeForm pledgeForm,
                  BindingResult bindingResult,
                  Authentication authentication,
                  Model model,
                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("mend", mendRequestService.findById(mendRequestId));
            model.addAttribute("gatherings", gatheringService.findOpenGatherings());
            return "pledge/form";
        }
        pledgeService.pledge(mendRequestId, pledgeForm, authentication.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Your offer to help has been sent for review.");
        return "redirect:/pledges";
    }

    @GetMapping("/manage")
    String manage(Model model) {
        model.addAttribute("pledges", pledgeService.findPending());
        return "pledge/manage";
    }

    @PostMapping("/manage/{id}/decision")
    String decide(@PathVariable UUID id,
                  @RequestParam boolean accept,
                  RedirectAttributes redirectAttributes) {
        pledgeService.decide(id, accept);
        redirectAttributes.addFlashAttribute("successMessage", accept ? "Pledge accepted." : "Pledge declined.");
        return "redirect:/pledges/manage";
    }

    @GetMapping("/{id}/complete")
    String completionForm(@PathVariable UUID id, Authentication authentication, Model model) {
        HelperPledge pledge = pledgeService.findById(id);
        if (!pledge.getHelper().getEmail().equalsIgnoreCase(authentication.getName())) {
            throw new DomainRuleException("You cannot complete another helper's pledge");
        }
        model.addAttribute("pledge", pledge);
        model.addAttribute("completionForm", new CompletionForm());
        return "pledge/complete";
    }

    @PostMapping("/{id}/complete")
    String complete(@PathVariable UUID id,
                    @Valid @ModelAttribute CompletionForm completionForm,
                    BindingResult bindingResult,
                    Authentication authentication,
                    Model model,
                    RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pledge", pledgeService.findById(id));
            return "pledge/complete";
        }
        pledgeService.complete(id, completionForm, authentication.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Repair marked as completed.");
        return "redirect:/pledges";
    }

    @PostMapping("/{id}/withdraw")
    String withdraw(@PathVariable UUID id,
                    Authentication authentication,
                    RedirectAttributes redirectAttributes) {
        pledgeService.withdraw(id, authentication.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Pledge withdrawn.");
        return "redirect:/pledges";
    }
}
