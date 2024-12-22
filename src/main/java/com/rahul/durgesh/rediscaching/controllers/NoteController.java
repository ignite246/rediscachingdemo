package com.rahul.durgesh.rediscaching.controllers;

import com.rahul.durgesh.rediscaching.entities.Note;
import com.rahul.durgesh.rediscaching.services.NoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/notes")
public class NoteController {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoteController.class);

    @Autowired
    private NoteService service;

    @PostMapping()
    public Note createANote(@RequestBody Note note) {
        LOGGER.info("createANote::STARTS::note={}", note);
        System.out.println(note.getId() + "..." + note.getTitle() + "..." + note.getDescription());
        return service.saveANote(note);
    }

    @GetMapping("/{id}")
    public Note findANote(@PathVariable("id") Integer id) {
        LOGGER.info("findANote::STARTS::id={}", id);
        return service.getANoteById(id);
    }
}