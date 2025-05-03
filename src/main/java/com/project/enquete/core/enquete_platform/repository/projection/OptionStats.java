package com.project.enquete.core.enquete_platform.repository.projection;

public interface OptionStats {
    Long getId();
    String getText();
    Long getVoteCount();
    Double getPercentage();
}
