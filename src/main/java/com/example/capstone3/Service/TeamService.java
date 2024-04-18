package com.example.capstone3.Service;


import com.example.capstone3.Api.ApiException;
import com.example.capstone3.Model.MatchModel;
import com.example.capstone3.Model.Player;
import com.example.capstone3.Model.Team;
import com.example.capstone3.Repository.PlayerRepository;
import com.example.capstone3.Repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;


    public List<Team> getAll() {
        return teamRepository.findAll();
    }

    public Team getTeamById(Integer id){
        Team team = teamRepository.findTeamById(id);
        {
            if (team == null){
                throw new ApiException("Team not found with ID : " + id);
            }
                return teamRepository.findTeamById(id);
        }
    }



    public void add(Integer player_id, Team team) {
        Player player = playerRepository.findPlayerById(player_id);
        if (player == null){
            throw new ApiException("player not found");
        }

        if (player.getTeam() != null){
            throw new ApiException("leave your current team to create new one");
        }
        team.setLeader(player);
        player.setTeam(team);
        teamRepository.save(team);
        playerRepository.save(player);

    }

    public void update(Integer teamId, Team team) {
        if (teamRepository.existsById(teamId)) {
            team.setId(teamId);
            teamRepository.save(team);
        }else{
            throw new ApiException("team not found");

        }
    }

    public void delete(Integer id) {
        if (teamRepository.existsById(id)) {
            teamRepository.deleteById(id);
        } else {
            throw new ApiException("team not found");
        }


    }

    public void kickPlayer(Integer player_id, Integer leader_id, Integer team_id){
        Player leader = playerRepository.findPlayerById(leader_id);
        if (leader == null){
            throw new ApiException("Leader not found");
        }
        Team team = teamRepository.findTeamById(team_id);
        if (team == null){
            throw new ApiException("team not found");
        }
        if (team.getLeader().getId() != leader_id){
            throw new ApiException("only leader can kick player from the team");
        }

        Player player = playerRepository.findPlayerById(player_id);

        if (player == null){
            throw new ApiException("player not found");
        }

        if (player.getTeam().getId() != team_id){
            throw new ApiException("the player not in team");
        }

        player.setTeam(null);
        playerRepository.save(player);

    }

    public void leaveTeam(Integer player_id){

        Player player = playerRepository.findPlayerById(player_id);

        if (player == null){
            throw new ApiException("player not found");
        }
        if (player.getTeam() == null){
            throw new ApiException("you don't have a team to leave");
        }

        if (player.getTeam().getLeader().getId() == player_id){
            throw new ApiException("You are the leader. you need to change the leader before leaving");
        }

        player.setTeam(null);
        playerRepository.save(player);



    }

    public void changeLeader(Integer newLeader_id, Integer leader_id, Integer team_id){
        Player newLeader = playerRepository.findPlayerById(newLeader_id);
        if (newLeader == null){
            throw new ApiException("new Leader not found with ID : " + newLeader_id);
        }
        Player leader = playerRepository.findPlayerById(leader_id);
        if (leader == null){
            throw new ApiException("the leader not found with ID: " + leader_id);
        }
        Team team = teamRepository.findTeamById(team_id);
        if (team == null){
            throw new ApiException("team not found with ID : "+ team_id);
        }
        if (team.getLeader().getId() != leader_id){
            throw new ApiException("only leader can change the leader");
        }

        team.setLeader(newLeader);
        teamRepository.save(team);
    }

    //khaled alkuhaily
    public Set<MatchModel> getTeamMatches(Integer team_id){
        Team team = teamRepository.findTeamById(team_id);
        if(team == null){
            throw new ApiException("team does not exists");
        }
        return team.getMatchModel();
    }


}
