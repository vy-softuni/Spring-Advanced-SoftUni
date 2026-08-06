package app.mendnook.hub.web;

import app.mendnook.hub.member.MemberAccountService;
import app.mendnook.hub.member.RegistrationForm;
import app.mendnook.hub.shared.DomainRuleException;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthenticationController {

    private final MemberAccountService memberAccountService;

    public AuthenticationController(MemberAccountService memberAccountService) {
        this.memberAccountService = memberAccountService;
    }

    @GetMapping("/login")
    String login(Authentication authentication) {
        return authentication == null ? "auth/login" : "redirect:/dashboard";
    }

    @GetMapping("/register")
    String registrationForm(Model model, Authentication authentication) {
        if (authentication != null) {
            return "redirect:/dashboard";
        }
        model.addAttribute("registrationForm", new RegistrationForm());
        return "auth/register";
    }

    @PostMapping("/register")
    String register(@Valid @ModelAttribute RegistrationForm registrationForm,
                    BindingResult bindingResult,
                    RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        try {
            memberAccountService.register(registrationForm);
        } catch (DomainRuleException exception) {
            bindingResult.rejectValue("email", "duplicate", exception.getMessage());
            return "auth/register";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Account created. You can now sign in.");
        return "redirect:/login";
    }
}
