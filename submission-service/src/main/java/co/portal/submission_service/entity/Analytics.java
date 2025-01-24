package co.portal.submission_service.entity;


import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Builder
@Data
public class Analytics {

    @NotNull
    private int marksFactor;

    @NotNull
    private int attemptFactor;

    @NotNull
    private int timeFactor;

    @NotNull
    private int pointsScored;


}
