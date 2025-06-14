package com.datn.electronic_voting.service.impl;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class VoteChoiceCache {
    private final Map<Long, Map<Long, Map<Long, Boolean>>> voteChoices = new ConcurrentHashMap<>();

    public void putVoteChoice(Long electionId, Long candidateId, Long userId, Boolean voteChoice) {
        voteChoices
                .computeIfAbsent(electionId, e -> new ConcurrentHashMap<>())
                .computeIfAbsent(candidateId, c -> new ConcurrentHashMap<>())
                .put(userId, voteChoice);
    }

    public Map<Long, Boolean> getVoteChoices(Long electionId, Long candidateId) {
        return voteChoices
                .getOrDefault(electionId, new HashMap<>())
                .getOrDefault(candidateId, new HashMap<>());
    }

    public void clearVoteChoices(Long electionId, Long candidateId) {
        Map<Long, Map<Long, Boolean>> candidateMap = voteChoices.get(electionId);
        if (candidateMap != null) {
            candidateMap.remove(candidateId);
            if (candidateMap.isEmpty()) {
                voteChoices.remove(electionId);
            }
        }
    }
    public int countCandidatesInElection(Long electionId) {
        Map<Long, Map<Long, Boolean>> candidateMap = voteChoices.get(electionId);
        if (candidateMap != null) {
            return candidateMap.size();
        }
        return 0;
    }

    public boolean anyCandidateNotFullyVoted(Long electionId, Set<Long> allUserIds) {
        Map<Long, Map<Long, Boolean>> candidateMap = voteChoices.getOrDefault(electionId, new HashMap<>());
        for (Map.Entry<Long, Map<Long, Boolean>> entry : candidateMap.entrySet()) {
            if (entry.getValue().size() < allUserIds.size()) {
                return true; // Có ít nhất một ứng viên chưa được vote đầy đủ
            }
        }
        return false;
    }

    public void clearElection(Long electionId) {
        voteChoices.remove(electionId);
    }

}
