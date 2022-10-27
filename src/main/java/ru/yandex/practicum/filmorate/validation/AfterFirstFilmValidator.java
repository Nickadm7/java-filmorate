package ru.yandex.practicum.filmorate.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class AfterFirstFilmValidator implements ConstraintValidator<AfterFirstFilm, LocalDate> {

        @Override
        public boolean isValid(LocalDate date, ConstraintValidatorContext constraintValidatorContext) {
            if (date != null) {
                return date.isAfter(LocalDate.of(1895, 12, 28));
            }
            return true;
        }
}