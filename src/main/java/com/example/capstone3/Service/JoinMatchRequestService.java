package com.example.capstone3.Service;

import com.example.capstone3.Api.ApiException;
import com.example.capstone3.Model.*;
import com.example.capstone3.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JoinMatchRequestService {
    private final JoinMatchRequestRepository joinMatchRequestRepository;
    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;
    private final OrganizerRepository organizerRepository;
    private final PlayerRepository playerRepository;


    //khaled alkuhaily
    public void sendJoinMatchRequest(Integer team_leader_id,Integer team_id, Integer match_id){
        Player teamLeader = playerRepository.findPlayerById(team_leader_id);
        if(teamLeader == null){
            throw new ApiException("team leader does not exists");
        }

        Team team = teamRepository.findTeamById(team_id);
        if(team == null){
            throw new ApiException("team does not exists");
        }

        if(!team.getLeader().equals(teamLeader)){
            throw new ApiException("only a team leader can send join match request");
        }

        MatchModel match = matchRepository.findMatchById(match_id);
        if(match == null){
            throw new ApiException("match does not exists");
        }
        for(MatchModel tempMatch:team.getMatchModel()){
            if(!tempMatch.getStatus().equalsIgnoreCase("finished")
                    && match.getStartDate().isBefore(tempMatch.getEndDate())){
                throw new ApiException("can not send join Request because of time conflict");
            }
        }

//        if(team.getPlayers().size()!=11){
//            throw new ApiException("your team need to have 11 players");
//        }
        JoinMatchRequest request = new JoinMatchRequest();
        request.setTeam(team);
        request.setMatchModel(match);
        request.setStatus(JoinMatchRequest.RequestStatus.PENDING);
        joinMatchRequestRepository.save(request);
    }

    //khaled alkuhaily
    public JoinMatchRequest getJoinMatchRequestById(Integer request_id){
        JoinMatchRequest request = joinMatchRequestRepository.findJoinMatchRequestById(request_id);
        if(request == null){
            throw new ApiException("request not found");
        }
        return request;
    }

    //khaled alkuhaily
    public void acceptJoinMatchRequest(Integer organizer_id, Integer request_id, String status){
        Organizer organizer = organizerRepository.findOrganizerById(organizer_id);
        if(organizer == null){
            throw new ApiException("organizer does not exists");
        }

        JoinMatchRequest request = joinMatchRequestRepository.findJoinMatchRequestById(request_id);
        if(request == null){
            throw new ApiException("join match request not found");
        }

        Team team = request.getTeam();

        if(team == null){
            throw new ApiException("team does not exists");
        }

        MatchModel match = request.getMatchModel();

        if(request.getMatchModel().getTeams().size()==2){
            throw new ApiException("match already have two teams");
        }

        if(status.equalsIgnoreCase("ACCEPTED")){
            request.setStatus(JoinMatchRequest.RequestStatus.ACCEPTED);
            joinMatchRequestRepository.save(request);
            request.getMatchModel().getTeams().add(request.getTeam());
            team.getMatchModel().add(match);
            match.getTeams().add(team);
            teamRepository.save(team);
            matchRepository.save(match);
        }else if(status.equalsIgnoreCase("REJECTED")){
            request.setStatus(JoinMatchRequest.RequestStatus.REJECTED);
            joinMatchRequestRepository.save(request);
        }else throw new ApiException("status must be ACCEPTED or REJECTED");
    }

    //khaled alkuhaily
    public List<JoinMatchRequest> getAllRequestsByMatchId(Integer organizer_id,Integer match_id){
        Organizer organizer = organizerRepository.findOrganizerById(organizer_id);
        if(organizer == null){
            throw new ApiException("organizer does not exists");
        }
        MatchModel match = matchRepository.findMatchById(match_id);
        if(match == null){
            throw new ApiException("match does not exists");
        }
        if(match.getField().getOrganizer()!=organizer){
            throw new ApiException("can not view join request to matches does not belong to your organization");
        }
        return joinMatchRequestRepository.findJoinMatchRequestsByMatchModel(match);
    }

}