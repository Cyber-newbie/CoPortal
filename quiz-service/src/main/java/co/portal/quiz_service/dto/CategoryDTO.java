package co.portal.quiz_service.dto;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CategoryDTO {

    @NotNull
    private String title;

    private String description;
}
