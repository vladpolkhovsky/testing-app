package by.vppolkhovsky.tests_app.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ImageDto {
    private UUID id;
    private String name;
    private String path;
}
