package com.example.capstone3.Service;

import com.example.capstone3.Api.ApiException;
import com.example.capstone3.Model.Field;
import com.example.capstone3.Model.MatchModel;
import com.example.capstone3.Model.Organizer;
import com.example.capstone3.Repository.FieldRepository;
import com.example.capstone3.Repository.OrganizerRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FieldService {
    private final FieldRepository fieldRepository;
    private final OrganizerRepository organizerRepository;

    public void addField(Integer organizer_id, Field field){
        Organizer organizer = organizerRepository.findOrganizerById(organizer_id);
        if(organizer == null){
            throw new ApiException("organizer does not exists");
        }
        field.setOrganizer(organizer);
        fieldRepository.save(field);
    }

    public List<Field> getAllField()
    {
        return fieldRepository.findAll();
    }

    public void updateField(Integer field_id, Field field){
        Field f = fieldRepository.findFieldById(field_id);
        if(f == null){
            throw new ApiException("field does not exists");
        }
        f.setName(field.getName());
        f.setLocation(field.getLocation());
        f.setDimensions(field.getDimensions());
        fieldRepository.save(field);
    }

    public void deleteField(Integer field_id){
        Field field = fieldRepository.findFieldById(field_id);
        if(field == null){
            throw new ApiException("field does not exists");
        }

        fieldRepository.delete(field);
    }

    //khaled alkuhaily
    public Field getFieldById(Integer field_id){
        Field field = fieldRepository.findFieldById(field_id);
        if(field == null){
            throw new ApiException("field not found");
        }
        return field;
    }

    //khaled alkuhaily
    public List<Field> getFieldsByOrganizer(Integer organizer_id){
        Organizer organizer = organizerRepository.findOrganizerById(organizer_id);
        if(organizer == null){
            throw new ApiException("organizer does not exists");
        }
        return fieldRepository.findFieldsByOrganizer(organizer);
    }

    //get matches with available matches to join
    //khaled alkuhaily
    public List<Field> getFieldsWithAvailableMatches(){
        List<Field> fields = new ArrayList<>();
        for(Field field:fieldRepository.findAll()){
            for(MatchModel match:field.getMatches()){
                if(match.getStatus().equals("AVAILABLE")){
                    fields.add(field);
                    break;
                }
            }
        }
        if(fields.isEmpty()){
            throw new ApiException("there are no fields with available matches to join");
        }
        return fields;
    }

    //khaled alkuhaily
    public List<Field> getFieldsByLocation(String keyword){
        List<Field> fields = new ArrayList<>();
        for(Field field:fieldRepository.findAll()){
            if(field.getLocation().contains(keyword)){
                fields.add(field);
            }
        }
        return fields;
    }


    //get all fields with no available match, helpful for an organizer to add matches to an empty fields
    //khaled alkuhaily
    public List<Field> getFieldsWithNoMatches(Integer organizer_id){
        Organizer organizer = organizerRepository.findOrganizerById(organizer_id);

        if(organizer==null){
            throw new ApiException("organizer not found");
        }
        List<Field> fields = new ArrayList<>();

        for(Field field:fieldRepository.findAll()){
            if(field.getMatches().isEmpty()){
                fields.add(field);
                break;
            }

            for(MatchModel match:field.getMatches()){
                if(match.getStatus().equalsIgnoreCase("finished")){
                    fields.add(field);
                }
            }
        }
        return fields;
    }


    //khaled alkuhaily
    public Integer getNumberOfFieldMatchesByDate(Integer field_id, LocalDateTime dateTime){
        Field field = fieldRepository.findFieldById(field_id);
        Set<MatchModel> fieldMatches = field.getMatches();
        Integer counter = 0;
        for(MatchModel match:fieldMatches){
            if(match.getStartDate().toLocalDate().equals(dateTime.toLocalDate())){
                counter++;
            }
        }
        return counter;
    }

    //khaled alkuhaily
    public Integer totalMatchesByField(Integer field_id){
        Field field = fieldRepository.findFieldById(field_id);
        if(field == null){
            throw new ApiException("field is not valid");
        }
        return field.getMatches().size();
    }
}
