package com.example.capstone3.Repository;

import com.example.capstone3.Model.Organizer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizerRepository  extends JpaRepository<Organizer, Integer> {
    Organizer findOrganizerById(Integer organizer_id);
}
