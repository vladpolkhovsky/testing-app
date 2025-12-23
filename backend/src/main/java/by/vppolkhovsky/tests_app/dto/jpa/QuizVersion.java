package by.vppolkhovsky.tests_app.dto.jpa;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuizVersion {
    V1(null);
    private final QuizVersion previousVersion;
}
