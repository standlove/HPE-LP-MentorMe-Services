package com.livingprogress.mentorme.controllers;

import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.livingprogress.mentorme.entities.MenteeMentorGoal;
import com.livingprogress.mentorme.entities.MenteeMentorProgram;
import com.livingprogress.mentorme.entities.UsefulLink;
import com.livingprogress.mentorme.exceptions.ConfigurationException;
import com.livingprogress.mentorme.exceptions.EntityNotFoundException;
import com.livingprogress.mentorme.exceptions.MentorMeException;
import com.livingprogress.mentorme.services.MenteeMentorGoalService;
import com.livingprogress.mentorme.services.MenteeMentorProgramService;
import com.livingprogress.mentorme.utils.EntityTypes;
import com.livingprogress.mentorme.utils.Helper;

import lombok.NoArgsConstructor;

/**
 * The UsefulLink REST controller. Is effectively thread safe.
 */
@RestController
@RequestMapping("/usefulLinks")
@NoArgsConstructor
public class UsefulLinkController {
    /**
     * The mentee mentor program service used to perform operations.
     */
    @Autowired
    private MenteeMentorProgramService menteeMentorProgramService;
    
    /**
     * The MenteeMentorGoalService instance
     */
    @Autowired
    private MenteeMentorGoalService menteeMentorGoalService;
    
    /**
     * Check if all required fields are initialized properly.
     *
     * @throws ConfigurationException if any required field is not initialized properly.
     */
    @PostConstruct
    protected void checkConfiguration() {
        Helper.checkConfigNotNull(menteeMentorProgramService, "menteeMentorProgramService");
        Helper.checkConfigNotNull(menteeMentorGoalService, "menteeMentorGoalService");
    }

    /**
     * Add useful links to the specified entity.
     * 
     * @param entityType the entity type
     * @param entityId the entity id
     * @param usefulLink the link to add
     * @return the added link info
     * @throws MentorMeException if any error occurs
     */
    @RequestMapping(value = "{entityType}/{entityId}", method = RequestMethod.POST)
    @Transactional
    public UsefulLink create(@PathVariable String entityType, @PathVariable long entityId,
            @RequestBody UsefulLink usefulLink) throws MentorMeException {

        if (EntityTypes.MENTEE_MENTOR_PROGRAM.equalsIgnoreCase(entityType)) {
            MenteeMentorProgram program = menteeMentorProgramService.get(entityId);
            program.getUsefulLinks().add(usefulLink);
        } else if (EntityTypes.MENTEE_MENTOR_GOAL.equalsIgnoreCase(entityType)) {
            MenteeMentorGoal goal = menteeMentorGoalService.get(entityId);
            goal.getUsefulLinks().add(usefulLink);
        } else {
            throw new MentorMeException("The provided entityType is unknown.");
        }
        
        return usefulLink;
    }
    
    /**
     * Delete the link from the specified entity.
     * 
     * @param entityType the entity type
     * @param entityId the entity id
     * @param linkId the link id
     * @throws MentorMeException if any error occurs
     */
    @RequestMapping(value = "{entityType}/{entityId}/link/{linkId}", method = RequestMethod.DELETE)
    @Transactional
    public void delete(@PathVariable String entityType, @PathVariable long entityId, @PathVariable long linkId) throws MentorMeException {
        List<UsefulLink> links = null;
        
        if (EntityTypes.MENTEE_MENTOR_PROGRAM.equalsIgnoreCase(entityType)) {
            links = menteeMentorProgramService.get(entityId).getUsefulLinks();
        } else if (EntityTypes.MENTEE_MENTOR_GOAL.equalsIgnoreCase(entityType)) {
            links = menteeMentorGoalService.get(entityId).getUsefulLinks();
        } else {
            throw new MentorMeException("The provided entityType is unknown.");
        }

        boolean removed = false;
        Iterator<UsefulLink> it = links.iterator();
        while (it.hasNext()) {
            UsefulLink link = it.next();
            if (link.getId() == linkId) {
                it.remove();
                removed = true;
                break;
            }
        }
        
        if (!removed) {
            throw new EntityNotFoundException("The link is not in the entity");
        }
    }
    
    /**
     * Get all useful links of the specified entity.
     * 
     * @param entityType the entity type
     * @param entityId the entity id
     * @return all the associated links
     * @throws MentorMeException if any error occurs
     */
    @RequestMapping(value = "{entityType}/{entityId}", method = RequestMethod.GET)
    public List<UsefulLink> get(@PathVariable String entityType, @PathVariable long entityId) throws MentorMeException {
        if (EntityTypes.MENTEE_MENTOR_PROGRAM.equalsIgnoreCase(entityType)) {
            return menteeMentorProgramService.get(entityId).getUsefulLinks();
        } else if (EntityTypes.MENTEE_MENTOR_GOAL.equalsIgnoreCase(entityType)) {
            return menteeMentorGoalService.get(entityId).getUsefulLinks();
        } else {
            throw new MentorMeException("The provided entityType is unknown.");
        }
    }
}
