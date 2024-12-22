package com.rahul.durgesh.rediscaching.repos;

import com.rahul.durgesh.rediscaching.entities.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<Note, Integer> {
}
