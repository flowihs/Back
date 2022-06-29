package io.github.enkarin.bookcrossing.admin.dto;

import io.github.enkarin.bookcrossing.user.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Getter
@Schema(description = "Данные пользователя для администатора")
public class InfoUsersDto {

    @Schema(description = "Идентификатор", example = "0")
    private final int userId;

    @Schema(description = "Имя", example = "Alex")
    private final String name;

    @Schema(description = "Логин", example = "alex")
    private final String login;

    @Schema(description = "Почта", example = "al@yandex.ru")
    private final String email;

    @Schema(description = "Город", example = "Новосибирск")
    private final String city;

    @Schema(description = "Заблокирован ли аккаунт", example = "true")
    private final boolean accountNonLocked;

    @Schema(description = "Активирован ли аккаунт", example = "true")
    private final boolean enabled;

    @Schema(description = "Время последнего входа", example = "2022-11-03T23:15:09.61")
    private String loginDate;

    private InfoUsersDto(final int userId, final String name, final String login, final String email, final String city,
                        final boolean accountNonLocked, final boolean enable, final long loginDate, final int zone) {
        this.userId = userId;
        this.name = name;
        this.login = login;
        this.email = email;
        this.city = city;
        this.accountNonLocked = accountNonLocked;
        this.enabled = enable;
        if (loginDate != 0) {
            this.loginDate = LocalDateTime.ofEpochSecond(loginDate, 0, ZoneOffset.ofHours(zone)).toString();
        }
    }

    public static InfoUsersDto fromUser(final User user, final int zone) {
        return new InfoUsersDto(user.getUserId(), user.getName(), user.getLogin(), user.getEmail(),
                user.getCity(), user.isAccountNonLocked(), user.isEnabled(), user.getLoginDate(), zone);
    }
}
