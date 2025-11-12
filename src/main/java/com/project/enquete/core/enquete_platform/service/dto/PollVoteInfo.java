package com.project.enquete.core.enquete_platform.service.dto;

import java.util.Map;

public record PollVoteInfo(int totalVotes, Map<Long, Integer> votesByOption) {
}
