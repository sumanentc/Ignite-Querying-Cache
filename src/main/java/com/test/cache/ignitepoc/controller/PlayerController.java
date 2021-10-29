package com.test.cache.ignitepoc.controller;

import com.test.cache.ignitepoc.model.Player;
import com.test.cache.ignitepoc.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/players")
public class PlayerController {
    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/search/{text}")
    @ResponseStatus(HttpStatus.OK)
    public List<Player> searchPlayer(@PathVariable("text") String text) {
        return playerService.textSearch(text);
    }

    @GetMapping("/fuzzy/search/{text}")
    @ResponseStatus(HttpStatus.OK)
    public List<Player> fuzzySearchPlayer(@PathVariable("text") String text) {
        return playerService.fuzzySearch(text);
    }

    @GetMapping("/fuzzy/search/{text}/{fieldName}")
    @ResponseStatus(HttpStatus.OK)
    public List<Player> fuzzySearchPlayerOnField(@PathVariable("text") String text, @PathVariable("fieldName") String fieldName) {
        return playerService.fuzzySearchOnSpecificField(text, fieldName);
    }

    @GetMapping("/scan/search/{text}")
    @ResponseStatus(HttpStatus.OK)
    public List<Player> scanQuerySearchPlayerOnField(@PathVariable("text") String text) {
        return playerService.scanQuerySearch(text);
    }

    @GetMapping("/sql/search/{text}")
    @ResponseStatus(HttpStatus.OK)
    public List<Player> sqlQuerySearchPlayerOnField(@PathVariable("text") String text) {
        return playerService.sqlQuerySearch(text);
    }
}
