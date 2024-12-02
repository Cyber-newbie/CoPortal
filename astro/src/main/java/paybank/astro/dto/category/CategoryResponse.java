package paybank.astro.dto.category;

import lombok.Getter;
import lombok.Setter;
import paybank.astro.entity.Category;

@Getter
@Setter
public class CategoryResponse<T> {

    private String status;

    private String message;

    private T data;
}
