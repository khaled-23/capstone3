package com.example.capstone3.Service;

import com.example.capstone3.Api.ApiException;
import com.example.capstone3.Model.Field;
import com.example.capstone3.Model.MatchModel;
import com.example.capstone3.Model.Organizer;
import com.example.capstone3.Repository.FieldRepository;
import com.example.capstone3.Repository.MatchRepository;
import com.example.capstone3.Repository.OrganizerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrganizerService {
    private final OrganizerRepository organizerRepository;
    private final FieldRepository fieldRepository;
    private final MatchRepository matchRepository;

    public void addOrganizerRepository(Organizer organizer)
    {
        organizerRepository.save(organizer);
    }

    public List<Organizer> getAllOrganizers(){
        return organizerRepository.findAll();
    }

    public void updateOrganizer(Integer organizer_id, Organizer organizer){
        Organizer o = organizerRepository.findOrganizerById(organizer_id);

        if(o == null){
            throw new ApiException("organizer does not exists");
        }

        o.setName(organizer.getName());
        o.setPhone(organizer.getPhone());
        organizerRepository.save(o);
    }

    public void deleteOrganizer(Integer organizer_id){
        Organizer organizer = organizerRepository.findOrganizerById(organizer_id);
        if(organizer == null){
            throw new ApiException("organizer does not exists");
        }
        organizerRepository.delete(organizer);
    }


    //khaled alkuhaily
    public Set<MatchModel> getAllMatchesByFieldId(Integer organizer_id, Integer field_id){
        Organizer organizer = organizerRepository.findOrganizerById(organizer_id);
        if(organizer == null){
            throw new ApiException("organizer does not exists");
        }
        Field field = fieldRepository.findFieldById(field_id);

        if(field == null){
            throw new ApiException("field does not exists");
        }

        if(!field.getOrganizer().equals(organizer)){
            throw new ApiException("field does not belong to organizer");
        }

        return field.getMatches();
    }


    //khaled alkuhaily
    public void addResultToMatch(Integer organizer_id, Integer match_id, String result, String winner){
        Organizer organizer = organizerRepository.findOrganizerById(organizer_id);
        if(organizer == null){
            throw new ApiException("organizer not found");
        }
        MatchModel match = matchRepository.findMatchById(match_id);
        if(match == null){
            throw new ApiException("match does not exists");
        }
        if(match.getField().getOrganizer()!=organizer){
            throw new ApiException("match does not belong to organizer");
        }
        if(!match.getStatus().equals("finished")){
            throw new ApiException("you can add result after the match ends");
        }
        match.setResult(result);
        //need to change
        match.setWinner(winner);
        matchRepository.save(match);
    }

}
