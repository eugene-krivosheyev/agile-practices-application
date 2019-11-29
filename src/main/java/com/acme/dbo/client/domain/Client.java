package com.acme.dbo.client.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.Instant;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ApiModel(description = "Entity with personalized information about client")
@Entity
@Table(name = "CLIENT")
public class Client {
    @ApiModelProperty(notes = "Unique identification of client", hidden = true)
    @EqualsAndHashCode.Exclude
    @Nullable @PositiveOrZero
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ApiModelProperty(notes = "Client login for auth", required = true, example = "admin@email.com")
    @NonNull @Email @Size(min = 5, max = 128)
    @Column(name = "LOGIN")
    String login;

    @ApiModelProperty(notes = "Client secret", required = true, example = "749f09bade8aca7556749f09bade8aca7556")
    @EqualsAndHashCode.Exclude
    @NonNull @Pattern(regexp = "^[a-zA-Z0-9_@\\-\\\\.]+$") @Size(min = 5, max = 128)
    @Column(name = "SECRET")
    String secret;

    @ApiModelProperty(notes = "Client salt", required = true, example = "some-salt")
    @EqualsAndHashCode.Exclude
    @NonNull @Pattern(regexp = "^[a-zA-Z0-9_@\\-\\\\.]+$") @Size(min = 5, max = 128)
    @Column(name = "SALT")
    String salt;

    @ApiModelProperty(notes = "Date registered client", hidden = true)
    @EqualsAndHashCode.Exclude
    @Nullable @Past
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Column(name = "CREATED", insertable = false)
    Instant created;

    @ApiModelProperty(notes = "Active client login", hidden = true)
    @EqualsAndHashCode.Exclude
    @Nullable
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Column(name = "ENABLED", insertable = false)
    Boolean enabled;
}
