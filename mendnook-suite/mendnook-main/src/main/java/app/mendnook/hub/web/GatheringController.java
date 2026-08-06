package app.mendnook.hub.web;

import app.mendnook.hub.gathering.GatheringForm;
import app.mendnook.hub.gathering.GatheringState;
import app.mendnook.hub.gathering.WorkshopGathering;
import app.mendnook.hub.gathering.WorkshopGatheringService;
import jakarta.validation.Valid;
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
@RequestMapping("/gatherings")
public class GatheringController {

    private final WorkshopGatheringService gatheringService;

    public GatheringController(WorkshopGatheringService gatheringService) {
        this.gatheringService = gatheringService;
    }

    @GetMapping
    String list(Model model) {
        model.addAttribute("gatherings", gatheringService.findAll());
        return "gathering/list";
    }

    @GetMapping("/{id}")
    String details(@PathVariable UUID id, Model model) {
        model.addAttribute("gathering", gatheringService.findById(id));
        return "gathering/details";
    }

    @GetMapping("/manage/new")
    String createForm(Model model) {
        model.addAttribute("gatheringForm", new GatheringForm());
        return "gathering/form";
    }

    @PostMapping("/manage")
    String create(@Valid @ModelAttribute GatheringForm gatheringForm,
                  BindingResult bindingResult,
                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "gathering/form";
        }
        WorkshopGathering created = gatheringService.create(gatheringForm);
        redirectAttributes.addFlashAttribute("successMessage", "Workshop gathering created.");
        return "redirect:/gatherings/" + created.getId();
    }

    @GetMapping("/manage/{id}/edit")
    String editForm(@PathVariable UUID id, Model model) {
        WorkshopGathering gathering = gatheringService.findById(id);
        model.addAttribute("gathering", gathering);
        model.addAttribute("gatheringForm", GatheringForm.from(gathering));
        return "gathering/form";
    }

    @PostMapping("/manage/{id}/edit")
    String revise(@PathVariable UUID id,
                  @Valid @ModelAttribute GatheringForm gatheringForm,
                  BindingResult bindingResult,
                  Model model,
                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("gathering", gatheringService.findById(id));
            return "gathering/form";
        }
        gatheringService.revise(id, gatheringForm);
        redirectAttributes.addFlashAttribute("successMessage", "Workshop gathering updated.");
        return "redirect:/gatherings/" + id;
    }

    @PostMapping("/manage/{id}/state")
    String changeState(@PathVariable UUID id,
                       @RequestParam GatheringState state,
                       RedirectAttributes redirectAttributes) {
        gatheringService.changeState(id, state);
        redirectAttributes.addFlashAttribute("successMessage", "Gathering state changed to " + state + ".");
        return "redirect:/gatherings/" + id;
    }
}
