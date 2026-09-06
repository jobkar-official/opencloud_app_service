
package dev.opencloud.domain.controller;

import dev.opencloud.domain.entity.Server;
import dev.opencloud.domain.repository.ServerRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/servers")
@RequiredArgsConstructor
public class ServerApiController {
  private final ServerRepository repo;

  @GetMapping
  public List<Server> list() {
    return repo.findAll();
  }

  @PostMapping
  public Server create(@RequestBody Server s) {
    return repo.save(s);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Server> get(@PathVariable String id) {
    return ResponseEntity.of(repo.findById(id));
  }

  @PostMapping("/{id}/test-connection")
  public Map<String, Object> test(@PathVariable String id) {
    // real SSH check would go here using JSch
    Server s = repo.findById(id).orElseThrow();
    s.setStatus(Server.Status.CONNECTED);
    repo.save(s);
    return Map.of("ok", true, "host", s.getHost());
  }
}
