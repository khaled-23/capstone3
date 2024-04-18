package com.example.capstone3.Service;

import com.example.capstone3.Api.ApiException;
import com.example.capstone3.Model.Field;
import com.example.capstone3.Model.MatchModel;
import com.example.capstone3.Model.Organizer;
import com.example.capstone3.Model.Team;
import com.example.capstone3.Repository.FieldRepository;
import com.example.capstone3.Repository.MatchRepository;
import com.example.capstone3.Repository.OrganizerRepository;
import com.example.capstone3.Repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchService {
    private final MatchRepository matchRepository;
    private final FieldRepository fieldRepository;
    private final OrganizerRepository organizerRepository;
    private final TeamRepository teamRepository;
    public List<MatchModel> getAllMatch()

    {
        return matchRepository.findAll();
    }
    public void addMatch(Integer organizer_id,Integer field_id, MatchModel matchModel){
        Organizer organizer = organizerRepository.findOrganizerById(organizer_id);
        if(organizer == null){
            throw new ApiException("organizer does not exists");
        }
        Field field =fieldRepository.findFieldById(field_id);
        if (field==null){
            throw new ApiException("Field not found ");
        }
//        MatchModel tempMatch = matchRepository


        if(!field.getOrganizer().equals(organizer)){
            throw new ApiException("field does not belong to organizer");
        }
        matchModel.setField(field);
        matchModel.setStatus("available");
        matchRepository.save(matchModel);
    }
    public void UpdateMatch(Integer id , MatchModel matchModel){
        MatchModel m =matchRepository.findMatchById(id);
        if (m==null){
            throw new ApiException(" not found");
        }
        m.setStartDate(matchModel.getStartDate());
        m.setEndDate(matchModel.getEndDate());
        m.setWinner(matchModel.getWinner());
        m.setResult(matchModel.getResult());
        matchRepository.save(m);
    }
    public void deleteMatch(Integer id){
        MatchModel matchModel = matchRepository.findMatchById(id);
        if (matchModel ==null){
            throw new ApiException("not found");
        }
        matchRepository.delete(matchModel);
    }


    //khaled alkuhaily
    public MatchModel findMatchById(Integer match_id){
        MatchModel match = matchRepository.findMatchById(match_id);
        if(match==null){
            throw new ApiException("match does not exists");
        }
        return match;
    }


    //khaled alkuhaily
    public List<MatchModel> getAllAvailableMatchesByFieldId(Integer field_id){
        Field field = fieldRepository.findFieldById(field_id);
        if(field == null){
            throw new ApiException("field not found");
        }
        List<MatchModel> matches = new ArrayList<>();
        for(MatchModel match:field.getMatches()){
            if(match.getStatus().equalsIgnoreCase("available")){
                matches.add(match);
            }
        }
        return matches;
    }



    //update match status every 1m
    //khaled not end-point
    @Scheduled(fixedDelay = 60000)
    public void updateStatus(){
        LocalDateTime currentDateTime = LocalDateTime.now();
        for(MatchModel match:matchRepository.findAll()){
            if(currentDateTime.isAfter(match.getStartDate())
                    &&currentDateTime.isBefore(match.getEndDate())){
                match.setStatus("Playing");
                matchRepository.save(match);
            }else if(currentDateTime.isAfter(match.getEndDate())){
                match.setStatus("Finished");
            }
        }
    }

}