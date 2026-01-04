package by.vppolkhovsky.tests_app.controller;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import by.vppolkhovsky.tests_app.dto.ImageDto;
import by.vppolkhovsky.tests_app.jpa.entity.ImageEntity;
import by.vppolkhovsky.tests_app.jpa.repository.ImageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
public class ImageRestController {

    @Value("${server.servlet.context-path}")
    private String contextPath;

    private final ImageRepository imageRepository;

    @SneakyThrows
    @Transactional
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageDto> saveImage(@RequestPart("image") MultipartFile filePart) {
        ImageEntity entity = new ImageEntity();
        entity.setContent(filePart.getBytes());
        entity.setName(filePart.getOriginalFilename());

        entity = imageRepository.save(entity);

        ImageDto image = ImageDto.builder()
            .path(contextPath + "/" + entity.getId())
            .name(entity.getName())
            .id(entity.getId())
            .build();

        return ResponseEntity.ok(image);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Resource> getImage(@PathVariable UUID id) {

        Optional<ImageEntity> byId = imageRepository.findById(id);

        if (byId.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ImageEntity image = byId.get();

        HttpHeaders headers = new HttpHeaders();

        Resource resource = new ByteArrayResource(image.getContent());
        MediaTypeFactory.getMediaType(image.getName()).ifPresent(headers::setContentType);

        headers.setContentDisposition(ContentDisposition.builder("inline")
            .filename(image.getName())
            .build());

        return ResponseEntity.ok()
            .headers(headers)
            .body(resource);
    }
}
