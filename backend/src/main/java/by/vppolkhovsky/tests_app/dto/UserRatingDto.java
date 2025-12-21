package by.vppolkhovsky.tests_app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRatingDto {
    private UserDto user;
    @Builder.Default
    private Integer rating = 0;
}
