package com.example.urlshortener.link;

import com.example.urlshortener.link.dto.CreateLinkRequest;
import com.example.urlshortener.link.dto.LinkResponse;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/links")
public class LinkController {

    private final LinkService linkService;

    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LinkResponse create(@Valid @RequestBody CreateLinkRequest request, Principal principal) {
        return linkService.create(principal.getName(), request);
    }

    @GetMapping
    public List<LinkResponse> getAll(Principal principal) {
        return linkService.getAll(principal.getName());
    }

    @GetMapping("/active")
    public List<LinkResponse> getActive(Principal principal) {
        return linkService.getActive(principal.getName());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Principal principal) {
        linkService.delete(principal.getName(), id);
    }
}
