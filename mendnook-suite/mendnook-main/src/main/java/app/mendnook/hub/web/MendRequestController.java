package app.mendnook.hub.web;

import app.mendnook.hub.gathering.WorkshopGatheringService;
import app.mendnook.hub.material.MaterialGatewayService;
import app.mendnook.hub.material.MaterialHoldForm;
import app.mendnook.hub.mend.ItemKind;
import app.mendnook.hub.mend.MendRequest;
import app.mendnook.hub.mend.MendRequestForm;
import app.mendnook.hub.mend.MendRequestService;
import app.mendnook.hub.pledge.PledgeForm;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/mends")
public class MendRequestController {

    private final MendRequestService mendRequestService;
    private final WorkshopGatheringService gatheringService;
    private final MaterialGatewayService materialGatewayService;

    public MendRequestController(MendRequestService mendRequestService,
                                 WorkshopGatheringService gatheringService,
                                 MaterialGatewayService materialGatewayService) {
        this.mendRequestService = mendRequestService;
        this.gatheringService = gatheringService;
        this.materialGatewayService = materialGatewayService;
    }

    @ModelAttribute("itemKinds")
    ItemKind[] itemKinds() {
        return ItemKind.values();
    }

    @GetMapping
    String list(Authentication authentication, Model model) {
        model.addAttribute("mends", mendRequestService.findOwnedBy(authentication.getName()));
        return "mend/list";
    }

    @GetMapping("/new")
    String createForm(Model model) {
        model.addAttribute("mendRequestForm", new MendRequestForm());
        return "mend/form";
    }

    @PostMapping
    String create(@Valid @ModelAttribute MendRequestForm mendRequestForm,
                  BindingResult bindingResult,
                  Authentication authentication,
                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "mend/form";
        }
        MendRequest created = mendRequestService.submit(mendRequestForm, authentication.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Your repair request has been submitted.");
        return "redirect:/mends/" + created.getId();
    }

    @GetMapping("/{id}")
    String details(@PathVariable UUID id, Authentication authentication, Model model) {
        boolean privileged = hasReviewPermission(authentication);
        MendRequest request = mendRequestService.findVisible(id, authentication.getName(), privileged);
        model.addAttribute("mend", request);
        model.addAttribute("gatherings", gatheringService.findOpenGatherings());
        model.addAttribute("pledgeForm", new PledgeForm());
        model.addAttribute("materialHoldForm", new MaterialHoldForm());
        model.addAttribute("materials", materialGatewayService.findMaterials(null));
        model.addAttribute("allocations", materialGatewayService.findAllocations(id, authentication.getName(), privileged));
        return "mend/details";
    }

    @GetMapping("/{id}/edit")
    String editForm(@PathVariable UUID id, Authentication authentication, Model model) {
        MendRequest request = mendRequestService.findVisible(id, authentication.getName(), false);
        model.addAttribute("mend", request);
        model.addAttribute("mendRequestForm", MendRequestForm.from(request));
        return "mend/form";
    }

    @PostMapping("/{id}/edit")
    String revise(@PathVariable UUID id,
                  @Valid @ModelAttribute MendRequestForm mendRequestForm,
                  BindingResult bindingResult,
                  Authentication authentication,
                  Model model,
                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("mend", mendRequestService.findVisible(id, authentication.getName(), false));
            return "mend/form";
        }
        mendRequestService.revise(id, mendRequestForm, authentication.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Repair request updated.");
        return "redirect:/mends/" + id;
    }

    @PostMapping("/{id}/cancel")
    String cancel(@PathVariable UUID id,
                  Authentication authentication,
                  RedirectAttributes redirectAttributes) {
        mendRequestService.cancel(id, authentication.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Repair request cancelled.");
        return "redirect:/mends/" + id;
    }

    private boolean hasReviewPermission(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("REVIEW_PLEDGE"));
    }
}
