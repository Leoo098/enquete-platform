package com.project.enquete.core.enquete_platform.controller.web;

import com.project.enquete.core.enquete_platform.form.PasswordForm;
import com.project.enquete.core.enquete_platform.form.UserForm;
import com.project.enquete.core.enquete_platform.form.UsernameForm;
import com.project.enquete.core.enquete_platform.model.User;
import com.project.enquete.core.enquete_platform.security.auth.CustomAuthentication;
import com.project.enquete.core.enquete_platform.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountViewController {

    private final UserService userService;

    @GetMapping
    public String myAccount(Model model, Authentication authentication){
        User userPrincipal = (User) authentication.getPrincipal();
        User user = userService.findByEmail(userPrincipal.getEmail());

        model.addAttribute("authentication", authentication);
        model.addAttribute("user", user);
        return "account/my-account";
    }

    @GetMapping("/change-username")
    public String showEditForm(Model model, Authentication authentication){
        String currentUsername = authentication.getName();
        model.addAttribute("user", new UsernameForm(currentUsername));
        model.addAttribute("authentication", authentication);
        return "account/change-username";
    }

    @PostMapping("/change-username")
    public String changeUsername(@ModelAttribute("user") @Valid UsernameForm usernameForm,
                                 BindingResult result, RedirectAttributes redirectAttributes){

        if (result.hasErrors()){
            return "account/change-username";
        }

        if (userService.usernameExists(usernameForm.getUsername())) {
            result.rejectValue("username", "error.user", "Nome de usuário já cadastrado!");
            return "account/change-username";
        }

        User updateUser = userService.updateUsername(usernameForm);
        userService.updateAuthentication(updateUser);

        redirectAttributes.addFlashAttribute("toast", "success");
        redirectAttributes.addFlashAttribute("toastMessage", "Nome de usuário alterado com sucesso!");

        return "redirect:/account";
    }

    @GetMapping("/check-password")
    public String checkPasswordPage(){
        return "/account/check-password";
    }

    @PostMapping("/check-password")
    public String checkPassword(@RequestParam String typedPassword,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {

        boolean checkedPassword = userService.checkPassword(typedPassword, authentication);

        if (!checkedPassword) {
            redirectAttributes.addFlashAttribute("error", "Senha incorreta!");
            return "redirect:/account/check-password";
        }

        return "redirect:/account/change-password";
    }

    @GetMapping("/change-password")
    public String changePassowrdForm(Model model, PasswordForm passwordForm){
        model.addAttribute("password", new PasswordForm());
        return "account/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@ModelAttribute("passwordForm") @Valid PasswordForm passwordForm, RedirectAttributes redirectAttributes){
        try {
            userService.updatePassword(passwordForm);

            redirectAttributes.addFlashAttribute("toast", "success");
            redirectAttributes.addFlashAttribute("toastMessage", "Senha alterada com sucesso!");
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("toast", "error");
            redirectAttributes.addFlashAttribute("toastMessage", "Erro ao alterar senha");
        }

        return "redirect:/account";
    }

//    @PostMapping("/check-password")
//    public String checkPassword(String typedPassword, Authentication authentication, BindingResult result){
//        boolean checkedPassword = userService.checkPassword(typedPassword, authentication);
//
//        if (result.hasErrors()){
//            result.rejectValue("typedPassword", "error.user", "A senha está incorreta!");
//            return "account/check-password";
//        }
//
//        if (checkedPassword){
//            return "redirect:account/change-password";
//        }
//
//        return "/check-password";
//    }
}
