package app.mendnook.hub.web;

import app.mendnook.hub.member.MemberAccount;
import app.mendnook.hub.member.MemberAccountService;
import app.mendnook.hub.member.ProfileForm;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final MemberAccountService memberAccountService;

    public ProfileController(MemberAccountService memberAccountService) {
        this.memberAccountService = memberAccountService;
    }

    @GetMapping
    String profile(Authentication authentication, Model model) {
        MemberAccount account = memberAccountService.findByEmail(authentication.getName());
        ProfileForm form = new ProfileForm();
        form.setDisplayName(account.getDisplayName());
        form.setRepairInterests(account.getRepairInterests());
        model.addAttribute("member", account);
        model.addAttribute("profileForm", form);
        return "profile";
    }

    @PostMapping
    String revise(@Valid @ModelAttribute ProfileForm profileForm,
                  BindingResult bindingResult,
                  Authentication authentication,
                  Model model,
                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("member", memberAccountService.findByEmail(authentication.getName()));
            return "profile";
        }
        memberAccountService.reviseOwnProfile(authentication.getName(), profileForm);
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated.");
        return "redirect:/profile";
    }
}
