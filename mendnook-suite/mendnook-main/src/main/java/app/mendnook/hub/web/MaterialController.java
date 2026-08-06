package app.mendnook.hub.web;

import app.mendnook.hub.material.MaterialGatewayService;
import app.mendnook.hub.material.MaterialHoldForm;
import app.mendnook.hub.material.StockLevelForm;
import app.mendnook.hub.mend.MendRequestService;
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
@RequestMapping("/materials")
public class MaterialController {

    private final MaterialGatewayService materialGatewayService;
    private final MendRequestService mendRequestService;

    public MaterialController(MaterialGatewayService materialGatewayService,
                              MendRequestService mendRequestService) {
        this.materialGatewayService = materialGatewayService;
        this.mendRequestService = mendRequestService;
    }

    @GetMapping
    String catalog(@RequestParam(required = false) String query, Model model) {
        model.addAttribute("materials", materialGatewayService.findMaterials(query));
        model.addAttribute("query", query);
        model.addAttribute("stockLevelForm", new StockLevelForm());
        return "material/catalog";
    }

    @GetMapping("/reserve/{mendRequestId}")
    String reserveForm(@PathVariable UUID mendRequestId, Authentication authentication, Model model) {
        model.addAttribute("mend", mendRequestService.findVisible(mendRequestId, authentication.getName(), false));
        model.addAttribute("materials", materialGatewayService.findMaterials(null));
        model.addAttribute("materialHoldForm", new MaterialHoldForm());
        return "material/reserve";
    }

    @PostMapping("/reserve/{mendRequestId}")
    String reserve(@PathVariable UUID mendRequestId,
                   @Valid @ModelAttribute MaterialHoldForm materialHoldForm,
                   BindingResult bindingResult,
                   Authentication authentication,
                   Model model,
                   RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("mend", mendRequestService.findVisible(mendRequestId, authentication.getName(), false));
            model.addAttribute("materials", materialGatewayService.findMaterials(null));
            return "material/reserve";
        }
        materialGatewayService.reserve(mendRequestId, materialHoldForm, authentication.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Material reserved for this repair.");
        return "redirect:/mends/" + mendRequestId;
    }

    @PostMapping("/{allocationId}/release")
    String release(@PathVariable UUID allocationId,
                   @RequestParam UUID mendRequestId,
                   Authentication authentication,
                   RedirectAttributes redirectAttributes) {
        materialGatewayService.release(
                allocationId, mendRequestId, authentication.getName(), hasMaterialPermission(authentication));
        redirectAttributes.addFlashAttribute("successMessage", "Material reservation released.");
        return "redirect:/mends/" + mendRequestId;
    }

    @PostMapping("/manage/{allocationId}/consume")
    String consume(@PathVariable UUID allocationId,
                   @RequestParam UUID mendRequestId,
                   RedirectAttributes redirectAttributes) {
        materialGatewayService.consume(allocationId);
        redirectAttributes.addFlashAttribute("successMessage", "Reserved material marked as consumed.");
        return "redirect:/mends/" + mendRequestId;
    }

    @PostMapping("/manage/{materialId}/stock")
    String replaceStock(@PathVariable UUID materialId,
                        @Valid @ModelAttribute StockLevelForm stockLevelForm,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("materials", materialGatewayService.findMaterials(null));
            model.addAttribute("query", null);
            return "material/catalog";
        }
        materialGatewayService.replaceStock(materialId, stockLevelForm);
        redirectAttributes.addFlashAttribute("successMessage", "Material stock updated.");
        return "redirect:/materials";
    }

    private boolean hasMaterialPermission(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("MANAGE_MATERIALS"));
    }
}
