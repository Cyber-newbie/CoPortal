package co.portal.submission_service.dto.quiz;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class CategoryDTO {

    private int id;

    @NotNull
    private String title;

    private String description;

}
