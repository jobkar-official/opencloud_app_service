package dev.opencloud.presentation.web;

import dev.opencloud.domain.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {

  private final UserRepository userRepo;

  public AccountController(UserRepository userRepo) {
    this.userRepo = userRepo;
  }

  @GetMapping("/account")
  public String account(Authentication auth, Model m) {
    String email = auth != null ? auth.getName() : null;
    userRepo.findByEmail(email).ifPresent(u -> m.addAttribute("user", u));
    return "account";
  }
}
