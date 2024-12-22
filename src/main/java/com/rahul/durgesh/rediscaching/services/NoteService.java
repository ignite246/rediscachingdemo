package com.rahul.durgesh.rediscaching.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rahul.durgesh.rediscaching.constants.AppConstants;
import com.rahul.durgesh.rediscaching.entities.Note;
import com.rahul.durgesh.rediscaching.repos.NoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;

@Service
public class NoteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoteService.class);

    @Autowired
    private NoteRepository repository;

    @Autowired
    private RedisService redisService;

    public Note getANoteById(int noteId) {
        try {
            if (noteId < 0) {
                throw new IllegalArgumentException("NoteId is invalid; noteId=" + noteId);
            } else {
                String noteFromCache = redisService.getDataFromRedisCache(AppConstants.NOTE_ID + noteId);
                LOGGER.info("Note found from the cache;noteFromCache={}", noteFromCache);
                if (noteFromCache != null && !noteFromCache.isBlank()) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    return objectMapper.readValue(noteFromCache, Note.class);
                } else {
                    final Optional<Note> optionalNote = repository.findById(noteId);
                    if (optionalNote.isPresent()) {
                        final Note noteFromDB = optionalNote.get();
                        final String noteInString = new ObjectMapper().writeValueAsString(noteFromDB);
                        redisService.setDataInRedisCache(AppConstants.NOTE_ID + noteId, noteInString, 60);
                        LOGGER.info("NoteId {} is stored in cache for {} sec", noteId, 60);
                        return noteFromDB;
                    } else {
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("ExceptionOccurred::getANoteById;Error={}", e.getLocalizedMessage());
            throw new RuntimeException("ExceptionOccurred::getANoteById;Error=" + e.getLocalizedMessage());
        }
    }

    public Note saveANote(Note note) {
        Note savedNote;
        try {
            if (Objects.isNull(note)) {
                throw new IllegalArgumentException("Note cannot be null");
            } else {
                note.setAddedOn(getAddedDate3());
                savedNote = repository.save(note);
            }

        } catch (Exception e) {
            LOGGER.error("ExceptionOccurred::saveANote;Error={}", e.getLocalizedMessage());
            throw new RuntimeException("ExceptionOccurred::saveANote;Error=" + e.getLocalizedMessage());
        }
        return savedNote;
    }

    public String getAddedDate() {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");
        return formatter.format(localDateTime);
    }

    public String getAddedDate2() {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy hh:mm:ss a", Locale.ENGLISH);
        return formatter.format(localDateTime);
    }


    public String getAddedDate3() {
        final TimeZone defaultTZ = TimeZone.getDefault();
        System.out.println("Timezone DisplayName=" + defaultTZ.getDisplayName() + ", Timezone ID=" + defaultTZ.getID());

        //ZoneId zoneId = ZoneId.of("Asia/Kolkata");
        ZoneId zoneId = ZoneId.of(defaultTZ.getID()); //dynamically setting the time zone

        ZonedDateTime now = ZonedDateTime.now(zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy hh:mm:ss a z", Locale.ENGLISH);
        return formatter.format(now);
    }
}
