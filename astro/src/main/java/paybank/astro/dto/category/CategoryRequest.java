package paybank.astro.dto.category;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class CategoryRequest {

    @NotBlank
    private String title;

    private String description;

}
