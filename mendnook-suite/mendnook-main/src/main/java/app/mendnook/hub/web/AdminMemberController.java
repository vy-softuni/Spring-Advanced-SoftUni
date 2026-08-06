package app.mendnook.hub.web;

import app.mendnook.hub.member.AppRole;
import app.mendnook.hub.member.MemberAccount;
import app.mendnook.hub.member.MemberAccountService;
import app.mendnook.hub.member.RoleChangeForm;
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
@RequestMapping("/admin/members")
public class AdminMemberController {

    private final MemberAccountService memberAccountService;

    public AdminMemberController(MemberAccountService memberAccountService) {
        this.memberAccountService = memberAccountService;
    }

    @GetMapping
    String list(Model model) {
        model.addAttribute("members", memberAccountService.findAll());
        return "admin/members";
    }

    @GetMapping("/{id}")
    String details(@PathVariable UUID id, Model model) {
        MemberAccount member = memberAccountService.findById(id);
        RoleChangeForm form = new RoleChangeForm();
        form.setRoles(member.getRoles());
        model.addAttribute("member", member);
        model.addAttribute("roleChangeForm", form);
        model.addAttribute("allRoles", AppRole.values());
        return "admin/member-details";
    }

    @PostMapping("/{id}/roles")
    String changeRoles(@PathVariable UUID id,
                       @Valid @ModelAttribute RoleChangeForm roleChangeForm,
                       BindingResult bindingResult,
                       Authentication authentication,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("member", memberAccountService.findById(id));
            model.addAttribute("allRoles", AppRole.values());
            return "admin/member-details";
        }
        memberAccountService.replaceRoles(id, roleChangeForm.getRoles(), authentication.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Member roles updated.");
        return "redirect:/admin/members/" + id;
    }

    @PostMapping("/{id}/access")
    String changeAccess(@PathVariable UUID id,
                        @RequestParam boolean enabled,
                        Authentication authentication,
                        RedirectAttributes redirectAttributes) {
        memberAccountService.changeEnabled(id, enabled, authentication.getName());
        redirectAttributes.addFlashAttribute("successMessage", enabled ? "Member enabled." : "Member disabled.");
        return "redirect:/admin/members/" + id;
    }
}
