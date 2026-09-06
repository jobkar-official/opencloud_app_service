package dev.opencloud.presentation.web;

import dev.opencloud.domain.entity.User;
import dev.opencloud.domain.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/users")
public class UsersController {

  private final UserRepository userRepo;
  private final PasswordEncoder encoder;

  public UsersController(UserRepository userRepo, PasswordEncoder encoder) {
    this.userRepo = userRepo;
    this.encoder = encoder;
  }

  @GetMapping
  public String list(Model m) {
    m.addAttribute("team", userRepo.findAll());
    return "users/list";
  }

  @PostMapping("/invite")
  public String invite(@RequestParam String email, @RequestParam String role) {
    if (userRepo.findByEmail(email).isPresent()) {
      return "redirect:/users";
    }
    User u = new User();
    u.setEmail(email);
    u.setPasswordHash(encoder.encode(UUID.randomUUID().toString()));
    u.setDisplayName(email);
    u.setRole(User.Role.valueOf(role));
    u.setStatus(User.Status.INVITED);
    userRepo.save(u);
    return "redirect:/users";
  }

  @PostMapping("/{id}/permission")
  public String changePermission(@PathVariable String id, @RequestParam String role) {
    userRepo.findById(id).ifPresent(u -> {
      if (u.getRole() == User.Role.OWNER) return;
      u.setRole(User.Role.valueOf(role));
      userRepo.save(u);
    });
    return "redirect:/users";
  }

  @PostMapping("/{id}/remove")
  public String remove(@PathVariable String id) {
    userRepo.findById(id).ifPresent(u -> {
      if (u.getRole() == User.Role.OWNER) return;
      u.setStatus(User.Status.CLOSED);
      userRepo.save(u);
    });
    return "redirect:/users";
  }
}
